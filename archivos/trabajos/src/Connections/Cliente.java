package Connections;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class Cliente {

    public static int puerto = 8000; // Puerto de conexión
    public static String direccion = "192.168.0.104"; // Dirección IP 
    static Socket skt_cliente; // Socket del cliente

    // Flujos de escritura y lectura sobre el socket
    static DataOutputStream dos_socket;
    static DataInputStream dis_socket;

    // Lista de archivos 
    public static String dir_relativa = ""; // Ruta relativa sobre la carpeta de arhivos del servidor en el que se encuentra el cliente
    public static ArrayList<Archivo> archs_relativos = new ArrayList<Archivo>(); // Lista de archivos dentro de la dirección relativa 

    // Dirección a la capeta de archivos
    static String dir_absoluta; // Apunta a la carpeta donde se encuentra el sistema de cliente

    /*
    DECRIPCIÓN DEL PROTOCOLO
    Datos:
        0 -> Hacer petición de lista de archivos
        1 -> Envío de archivo
        2 -> Solicitud de un archivo
        3 -> Solicitud de borrar un archivo
    
        255 -> Cerrar sesión
    
     */
    public static void main(String[] args) {

        

        // Simulando el envío de archivos
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        try {
            File[] seleccion = getChoice();

            // Compresión de archivos
            File comprimido = Archivo.comprimir(seleccion);
            enviarArchivo(comprimido); // Enviamos el archivo comprimido por el socket
            comprimido.delete(); // Eliminamos el archivo comprimido

            System.out.println(seleccion.length + " archivos/carpetas han sido enviados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo error al enviar el archivo");
        }

        

        // Simulando la solicitud de archivos
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        try {
            String nombres[] = {"Archivo.java", "Cliente.java"};
            recibirArchivo(nombres);
            System.out.println("Solicitud de archivo correcto");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo un error en la petición del archivo");
        }

        // Simulando la solicitud de borrar archivos
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        try {
            String nombres[] = {"Archivo.java", "Cliente.java"};
            eliminarArchivo(nombres);
            System.out.println("Solicitud de eliminación de archivos correcto");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo un error en la petición de eliminar el archivo.");
        }

        // Simulando la finalización del sistema
        cerrarConexion();
    }

    // Conectamos al servidor
    public static void conectar() throws Exception {
        skt_cliente = new Socket(direccion, puerto);
    }

    // Creamos el flujo de escritura del socket
    public static void flujoSalidaSkt() throws Exception {
        dos_socket = new DataOutputStream(skt_cliente.getOutputStream());
    }

    // Creamos el flujo de lectura del socket
    public static void flujoEntradaSkt() throws Exception {
        dis_socket = new DataInputStream(skt_cliente.getInputStream());
    }

    // Solicitamos lista de archivos del servidor
    public static void solicitarLista() throws Exception {
        archs_relativos = new ArrayList<Archivo>();
        // Envíamos la petición
        dos_socket.write(0); // Mandamos el comando en 0 indicando que será solicitud de la lista
        dos_socket.writeUTF(dir_relativa); // Mandamos la dirección relativa de la carpeta del servidor
        
        // Recibimos la respuesta
        int n_archs = dis_socket.read(); // Leemos el número de arhivos dentro de la carpeta
        
        for (int i = 0; i < n_archs; i++) {
            Archivo arch = new Archivo();

            // Leemos el nombre del archivo
            arch.setNombre(dis_socket.readUTF());
            // Leemos el tamaño del archivo
            arch.setTam(dis_socket.readLong());
            // Leemos la última modificación del archivo
            arch.setUltima_mod(dis_socket.readUTF());

            // Agregamos al arreglo
            archs_relativos.add(arch);
        }
    }

    // Enviamos un archivo comprimido con los archivos al servidor
    public static void enviarArchivo(File arch) throws Exception {
        // Enviamos la petición con el archivo
        dos_socket.write(1); // Enviando el comando de envío de archivo

        // Obteniendo datos del archivo
        String nombre = arch.getName();
        long tam = arch.length();
        String ruta = arch.getAbsolutePath();

        // Creamos el flujo de escritura
        DataInputStream dis = new DataInputStream(new FileInputStream(ruta));

        // Enviando metadatos del archivo
        dos_socket.writeUTF(nombre); // Indicamos el nombre
        dos_socket.writeLong(tam); // Indicando el tamaño en bytes
        dos_socket.flush();

        // Enviando archivo
        int leidos;
        for (long enviados = 0; enviados < tam;) {
            byte[] b = new byte[1500]; // Enviamos en paquete de 1500 bytes
            leidos = dis.read(b);

            dos_socket.write(b, 0, leidos); // Leemos desde el byte 0 hasta el número de leídos del archivo
            dos_socket.flush();
            enviados += leidos;
        }
        dis.close();
    }

    // Recibimos cada archivo enviado por el servidor
    public static void recibirArchivo(String nombres[]) throws IOException {
        // Enviamos la petición al servidor
        dos_socket.write(2); // Enviamos el comando indicando que solicitamos un archivo sobre la carpeta
        dos_socket.write(nombres.length); // Enviamos el número de archivos

        for (int i = 0; i < nombres.length; i++) {
            dos_socket.writeUTF(nombres[i]); // Enviamos el nombre de cada uno de los archivos
        }

        try {
            // Leemos metadatos del archivo
            String nombre = "ArchivoComprimido.zip";
            long tam = dis_socket.readLong();
            File arch = new File(dir_absoluta + "\\" + nombre);
            System.out.println(arch.getAbsolutePath());
            // Creamos el flujo de salida al archivo
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(arch.getAbsolutePath()));

            // Recibiendo archivo
            int recibidos;
            for (long escritos = 0; escritos < tam;) {
                byte[] b = new byte[1500]; // Recibimos un paquete de 1500 bytes
                recibidos = dis_socket.read(b);

                dos.write(b, 0, recibidos); // Leemos desde el byte 0 hasta el número de leídos del socket
                dos.flush();
                escritos += recibidos;
            }
            System.out.println("Archivo: " + dir_absoluta + "\\" + nombre + " recibido correctamente.");
            dos.close();

            // Descomprimimos el archivo en su carpeta         
            Archivo.descomprimir(arch);
            arch.delete();

        } catch (Exception e) {
            System.out.println("No se pudo recibir el archivo del cliente.");
            e.printStackTrace();
        }
    }

    // Elimiar archivos del servidor
    public static void eliminarArchivo(String nombres[]) throws IOException {
        // Enviamos la petición al servidor
        dos_socket.write(3); // Enviamos el comando indicando que solicitando borrar archivos sobre la carpeta
        dos_socket.write(nombres.length); // Enviamos el número de archivos

        for (int i = 0; i < nombres.length; i++) {
            dos_socket.writeUTF(nombres[i]); // Enviamos el nombre de cada uno de los archivos
        }
    }

    // Enviamos el comando de finalización al servidor
    public static void cerrarConexion() {
        try {
            dos_socket.write(255);
            dos_socket.close();
            dis_socket.close();

            skt_cliente.close();
            System.out.println("Sesión finalizada");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No se pudo finalizar la sesión correctamente");
        }
    }

    // Obtenemos lo que seleccionó para subir
    public static File[] getChoice() {
        // Lanzamos el file chooser para elegir carpetas o directorios
        JFileChooser f_chooser = new JFileChooser(new File(dir_absoluta));
        f_chooser.setMultiSelectionEnabled(true);
        f_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int seleccion = f_chooser.showOpenDialog(null);

        File[] archs = null;
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            archs = f_chooser.getSelectedFiles();
        }

        return archs;
    }
}

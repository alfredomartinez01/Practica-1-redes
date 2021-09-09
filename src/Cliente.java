
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class Cliente {

    static int puerto = 8000; // Puerto de conexión
    static String direccion = "192.168.0.109"; // Dirección IP 
    static Socket skt_cliente; // Socket del cliente

    // Flujos de escritura y lectura sobre el socket
    static DataOutputStream dos_socket;
    static DataInputStream dis_socket;

    // Lista de archivos 
    static String dir_relativa = ""; // Ruta relativa sobre la carpeta de arhivos del servidor en el que se encuentra el cliente
    static ArrayList<Archivo> archs_relativos = new ArrayList<Archivo>(); // Lista de archivos dentro de la dirección relativa 

    /*
    DECRIPCIÓN DEL PROTOCOLO
    Datos:
        0 -> Hacer petición de lista de archivos
        1 -> Envío de archivo
    
        255 -> Cerrar sesión
    
     */
    public static void main(String[] args) {
        // Simulando la apertura de la aplicación
        try {
            conectar(); // Creamos la conexión al socket
            System.out.println("Conexión establecida en: " + direccion + ":" + puerto);

            flujoSalidaSkt(); // Creamos el flujo de escritura del socket
            flujoEntradaSkt(); // Creamos el flujo de lectura del socket
            System.out.println("Flujos sobre el socket creados correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo error al conectar al servidor y/o hacer los flujos");
        }

        // Simulando la solicitud de la lista de archivos
        try {
            solicitarLista();
            System.out.println("Solicitud de lista correcta");
            for (Archivo arch : archs_relativos) {
                System.out.println(arch.getNombre() + ", " + arch.getUltima_mod());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo error al solicitar los archivos del servidor");
        }

        // Simulando el envío de un archivo
        try {
            File[] seleccion = getChoice();

            // Compresión de archivos
            File comprimido = Archivo.comprimir(seleccion);
            enviarArchivo(comprimido); // Enviamos el archivo comprimido por el socket
            comprimido.delete(); // Eliminamos el archivo comprimido
            
            System.out.println(seleccion.length +" archivos/carpetas han sido enviados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo error al enviar el archivo");
        }
        
        // Simulando la solicitud de la lista de archivos
        try{
            dir_relativa = "src\\";
            solicitarLista();
            System.out.println("Solicitud de lista correcta");
            for (Archivo arch : archs_relativos) {
                System.out.println(arch.getNombre() + ", " + arch.getUltima_mod());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hubo error al solicitar los archivos del servidor");
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
        JFileChooser f_chooser = new JFileChooser(new File("").getAbsolutePath());
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

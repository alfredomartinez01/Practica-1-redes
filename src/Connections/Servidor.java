package Connections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Servidor {

    static int puerto = 8000; // Puerto de conexión 
    static String direccion = "192.168.0.109"; // Dirección de conexión
    static ServerSocket skt_servidor; // Puerto de conexión del socket
    static Socket skt_cliente; // Socket de conexión de cada cliente

    // Flujos de escritura y lectura sobre el socket
    static DataOutputStream dos_socket;
    static DataInputStream dis_socket;

    // Lista de archivos 
    static String dir_relativa = ""; // Ruta relativa sobre la carpeta de arhivos del servidor en el que se encuentra el cliente
    static ArrayList<Archivo> archs_relativos = new ArrayList<Archivo>(); // Lista de archivos dentro de la dirección relativa

    // Dirección a la capeta de archivos
    static String dir_absoluta;

    public static void main(String[] args) {
        establecerCarpeta(); // Establecemos la carpeta de archivos como referencia

        try {
            conectar(); // Conectamos la aplicación al socket
            System.out.println("Servidor escuchando en: " + direccion + ":" + puerto);

            // Comenzamos la escucha permanente de clientes
            while (true) {
                // Tratamos de acepta a algún cliente
                try {
                    System.out.println("---------------------------------------------------------------------------------");
                    aceptarConexion();
                    System.out.println("Cliente conectado desde " + skt_cliente.getInetAddress() + ":" + skt_cliente.getPort());

                    flujoSalidaSkt(); // Creamos el flujo de escritura del socket
                    flujoEntradaSkt(); // Creamos el flujo de lectura del socket
                    System.out.println("Flujos sobre el socket creados correctamente.");

                    // Recibimos el comando del cliente
                    try {
                        int comando = recibirComando(); // El primer comando siempre será de solicitud de lista

                        // Escuchamos comandos mientras el cliente no cierre conexión
                        do {
                            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");

                            switch (comando) {
                                case 0: // En caso de que sea una solicitud de lista de archivos
                                    enviarListaArchs();
                                    break;
                                case 1: // En caso de que el cliente envíe un archivo
                                    recibirArchivos();
                                    break;
                                case 2: // En caso de que el cliente solicite archivos
                                    enviarArchivos();
                                    break;
                                case 3: // En caso de que el cliente elimine algún archivo
                                    eliminar();
                                    break;
                                default:
                                    break;
                            }
                            comando = recibirComando();

                        } while (comando != 255);
                        cerrarConexion();

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error al recibir el comando del cliente");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error al establecer la conexión con el cliente " + skt_cliente.getInetAddress() + "y/o al hacer los flujos");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Apertura faliida en: " + direccion + ":" + puerto);
        }
    }

    // Establece la dirección de la carpeta donde se encuentran los archivos
    public static void establecerCarpeta() {
        // Buscamos la carpeta dentro del directorio del proyecto
        File f = new File("");
        dir_absoluta = f.getAbsolutePath() + "\\archivos\\";
        dir_relativa = dir_absoluta;
    }

    // Creamos la conexión del servidor por el puerto
    public static void conectar() throws Exception {
        skt_servidor = new ServerSocket(puerto);
        skt_servidor.setReuseAddress(true);
    }

    // Aceptamos la conexión con algún cliente
    public static void aceptarConexion() throws Exception {
        skt_cliente = skt_servidor.accept();
    }

    // Creamos el flujo de escritura del socket
    public static void flujoSalidaSkt() throws Exception {
        dos_socket = new DataOutputStream(skt_cliente.getOutputStream());
    }

    // Creamos el flujo de lectura del socket
    public static void flujoEntradaSkt() throws Exception {
        dis_socket = new DataInputStream(skt_cliente.getInputStream());
    }

    // Recibimos el comando del cliente
    public static int recibirComando() throws Exception {
        return dis_socket.read();
    }

    // Para enviar la lista de archivos de la ruta relativa
    public static void enviarListaArchs() {
        try {
            // Recibimos el resto de la petición
            String ruta = dis_socket.readUTF(); // Leemos la ruta relativa que solicita el cliente

            // Ubicamos la carpeta
            dir_relativa = dir_absoluta + ruta + "\\";
            File carpeta = new File(dir_relativa);
            System.out.println(carpeta.getAbsolutePath());
            
            // Leemos la cantidad de archivos que tiene
            int n_archs = carpeta.listFiles().length;

            // Enviamos la respuesta
            dos_socket.write(n_archs);
            for (File arch : carpeta.listFiles()) {
                // Enviamos el nombre del archivo
                dos_socket.writeUTF(arch.getName());
                // Enviamos el tamaño del archivo
                dos_socket.writeLong(arch.length());
                // Enviamos la última modificación del archivo
                dos_socket.writeUTF(obtenerUltimaMod(arch));
            }
            System.out.println("Lista de archivos enviada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar lista de archivos");
        }
    }

    // Recibimos cada archivo enviado por el cliente
    public static void recibirArchivos() {
        try {
            // Leemos metadatos del archivo
            String nombre = dis_socket.readUTF();
            long tam = dis_socket.readLong();
            File arch = new File(dir_relativa + "\\" + nombre);

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

    // Enviamos un archivo comprimido con los archivos al servidor
    public static void enviarArchivos() throws Exception {
        // Recibimos el resto de la petición
        int n_archs = dis_socket.read(); // Leemos el número de archivos
        File files[] = new File[n_archs];

        for (int i = 0; i < n_archs; i++) {
            String nombre = dis_socket.readUTF();
            files[i] = new File(dir_relativa + "\\" + nombre + "\\"); // Obtenemos el archivo con cada nombre recibido
            System.out.println(files[i].getAbsolutePath());
        }

        // Comprimimos los archivos/carpetas en uno
        File arch = Archivo.comprimir(files);

        // Obteniendo datos del archivo
        long tam = arch.length();
        String ruta = arch.getAbsolutePath();

        // Creamos el flujo de escritura
        DataInputStream dis = new DataInputStream(new FileInputStream(ruta));

        // Enviando metadatos del archivo
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
        arch.delete();
    }

    // Eliminamos un archivo/directorio dependiendo de la petición
    public static void eliminar() throws Exception {
        // Recibimos el resto de la petición
        int n_archs = dis_socket.read(); // Leemos el número de archivos

        // Obteniendo y eliminando archivos
        for (int i = 0; i < n_archs; i++) {
            String nombre = dis_socket.readUTF();
            File arch = new File(dir_relativa + "\\" + nombre + "\\"); // Obtenemos el archivo con cada nombre recibido
            
            // Mandamos a eliminar el archivo o carpeta
            eliminarArchivoCarpeta(arch);
        }

    }

    // Elimina archivos o directorios del disto
    private static void eliminarArchivoCarpeta(File archivo) {
        if (!archivo.exists()){
            return;
        }

        if (archivo.isDirectory()) {
            for (File f : archivo.listFiles()) {
                eliminarArchivoCarpeta(f);
            }
        }
        archivo.delete();
    }

    // Finalizamos la sesión con el cliente
    public static void cerrarConexion() {
        try {
            dos_socket.close();
            dis_socket.close();

            skt_cliente.close();
            System.out.println("Sesión finalizada");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No se pudo finalizar la sesión correctamente");
        }
    }

    // Función que obtene la última modificación de un archivo
    public static String obtenerUltimaMod(File arch) {
        long lastModified = arch.lastModified();
        String pattern = "yyyy-MM-dd hh:mm aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date lastModifiedDate = new Date(lastModified);
        return simpleDateFormat.format(lastModifiedDate);
    }

}

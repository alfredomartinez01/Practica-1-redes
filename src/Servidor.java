
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
                        do{                            
                            switch (comando) {
                                case 0: // En caso de que sea una solicitud de lista de archivos
                                    enviarListaArchs();
                                    break;
                                case 1: // En caso de que el cliente envíe un archivo
                                    recibirArchivo();
                                    break;
                                default:
                                    break;
                            }
                            comando = recibirComando();
                            System.out.println(comando);
                            
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
            dir_relativa = dis_socket.readUTF(); // Leemos la ruta relativa que solicita el cliente

            // Ubicamos la carpeta
            String ruta = dir_absoluta + "\\" + dir_relativa;
            File carpeta = new File(ruta);

            // Leemos la cantidad de archivos que tiene
            int n_archs = carpeta.list().length;

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
    public static void recibirArchivo(){
        try {
            // Leemos metadatos del archivo
            String nombre = dis_socket.readUTF();
            long tam = dis_socket.readLong();
            File arch = new File(dir_absoluta + "\\" + nombre);
            
            // Creamos el flujo de salida al archivo
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(arch.getAbsolutePath()));
            
            // Recibiendo archivo
            int recibidos;
            for(long escritos = 0; escritos < tam; ){
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
    
    // Finalizamos la sesión con el cliente
    public static void cerrarConexion(){
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

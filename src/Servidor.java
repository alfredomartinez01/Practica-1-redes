import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servidor que recibe y envía los archivos solicitados por el cliente
 */
public class Servidor {
    
    static int puerto = 8000;
    static String direccion = "192.168.0.109";
    
    public static void main(String[] args){
      try{
            // Creamos un folder provicional de prueba
            File f = new File("");
            String ruta = f.getAbsolutePath();
            String carpeta="archivos";
            String ruta_archivos = ruta+"\\"+carpeta+"\\";
            File f2 = new File(ruta_archivos);
            f2.mkdirs();
            f2.setWritable(true, false);
          
          
            /**
                  * Argumentos para envío y recepción de datos con el servidor
                  *  0: recibiendo archivo
                  *  1: recibiendo directorio
            */
            ServerSocket skt_server = conectar(puerto);
            skt_server.setReuseAddress(true);
            System.out.println("Servidor escuchando en: " + direccion + ":" + puerto);
            
            // Comenzamos la escucha permanente de clientes
            while(true){
                System.out.println("---------------------------------------------------------------------------------");
                Socket skt_cliente = skt_server.accept();
                System.out.println("Cliente conectado desde " + skt_cliente.getInetAddress() + ":" + skt_cliente.getPort());
                
                DataInputStream dis = new DataInputStream(skt_cliente.getInputStream());
                int n_elementos = dis.read(); // Leemos el número de elementos enviados por cliente
                
                for(int e_recibidos = 0; e_recibidos < n_elementos; e_recibidos++){
                    int comando = dis.read(); // Leemos el comando envíado por el cliente
                    
                    switch(comando){
                        // Recepción de archivo
                        case 0:
                            reciveFile(dis, ruta_archivos); // Recibimos y creamos el archivo
                            break;

                        // Recepción de directorio
                        case 1:
                            reciveDirectory(dis, ruta_archivos);
                            break;
                    }
                }             
                dis.close();
                skt_cliente.close();
            }
            
            
          
      }catch(Exception e){
          e.printStackTrace();
          System.out.println("Apertura faliida en: " + direccion + ":" + puerto);
      }  
    }
    
    // Abrimos un puerto del servidor
    public static ServerSocket conectar(int puerto) throws IOException{
        return new ServerSocket(puerto);
    }
    
    // Recibimos cada archivo enviado por el cliente
    public static void reciveFile(DataInputStream dis, String ruta){
        try {
            // Leemos metadatos del archivo
            String nombre = dis.readUTF();
            long tam = dis.readLong();
            
            // Creamos el flujo de salida al archivo
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta + nombre));
            
            // Recibiendo archivo
            int recibidos;
            for(long escritos = 0; escritos < tam; ){
                byte[] b = new byte[1500]; // Recibimos un paquete de 1500 bytes
                recibidos = dis.read(b);
                
                dos.write(b, 0, recibidos); // Leemos desde el byte 0 hasta el número de leídos del socket
                dos.flush();
                escritos += recibidos;
                System.out.println("Recibidos: " + escritos + " tam " + tam);
            }            
            System.out.println("Archivo: " + ruta + nombre + " recibido correctamente.");
            
            
        } catch (Exception e) {
            System.out.println("No se pudo recibir el archivo del cliente.");
            e.printStackTrace();
        }
    }
    
    // Recibimos cada carpeta enviada por el cliente

    private static void reciveDirectory(DataInputStream dis, String ruta) {
        try {
            // Leemos metadatos de la carpeta
            String nombre = dis.readUTF();
            int n_archivos = dis.read();
            
            // Creamos el directorio
            ruta = ruta + "\\" + nombre + "\\";
            File directorio = new File(ruta);
            directorio.mkdirs();
            directorio.setWritable(true);
            
            // Recibiendo cada archivo del directorio
            for(int recibidos = 0; recibidos < n_archivos; recibidos++){
                reciveFile(dis, ruta);
            }
        
        } catch (Exception e) {
            System.out.println("No se pudo recibir el directorio del cliente.");
            e.printStackTrace();
        }
    
    }
    
}

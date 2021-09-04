
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PruebaS {
    
    static int puerto = 8000;
    static String direccion = "192.168.1.86";
    
    public static void main(String[] args){
      try{
            // Creamos un folder provicional de prueba
            File f = new File("");
            String ruta = f.getAbsolutePath();
          
            ServerSocket skt_server = conectar(puerto);
            skt_server.setReuseAddress(true);
            System.out.println("Servidor escuchando en: " + direccion + ":" + puerto);
            
            // Comenzamos la escucha permanente de clientes
            while(true){
                System.out.println("---------------------------------------------------------------------------------");
                Socket skt_cliente = skt_server.accept();
                System.out.println("Cliente conectado desde " + skt_cliente.getInetAddress() + ":" + skt_cliente.getPort());
                
                DataInputStream dis = new DataInputStream(skt_cliente.getInputStream());
                File comprimido = reciveFile(dis, ruta);
                descomprimir(comprimido);
                           
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
    
    // Descomprimimos el archivo
    public static boolean descomprimir(File arch){
        
        return false;
    }
    
    // Recibimos cada archivo enviado por el cliente
    public static File reciveFile(DataInputStream dis, String ruta){
        try {
            // Leemos metadatos del archivo
            String nombre = dis.readUTF();
            long tam = dis.readLong();
            File arch = new File(ruta + "\\" + nombre);
            
            // Creamos el flujo de salida al archivo
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(arch.getAbsolutePath()));
            
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
            
            return arch;
            
        } catch (Exception e) {
            System.out.println("No se pudo recibir el archivo del cliente.");
            e.printStackTrace();
            return null;
        }
    }
}   

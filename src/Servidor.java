
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Servidor {
    
    static int puerto = 8000;
    static String direccion = "192.168.0.109";
    
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
                reciveFile(dis, ruta);
                //descomprimir(comprimido);
                           
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
    public static boolean descomprimir(File arch, byte[] datos){
        try {
            // Creamos los flujos para trabajar el zip
            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(datos));
            
            ZipEntry salida;
            
            // Recorremos cada archivo
            while (null != (salida = zis.getNextEntry())) {
                
                System.out.println("Nombre del Archivo: "+salida.getName());	

                // Creamos flujo de escritura del archivo
                File arch_temp;
                
                if(salida.isDirectory()){
                    
                    arch_temp = new File(arch.getName() + "\\" + salida.getName()); // Abrimos el directorio
                    if(!arch_temp.isDirectory()) arch_temp.mkdir(); // Creamos el directorio si no está creado
                    
                    
                } else {
                    // Creamos el flujo del archivo
                    arch_temp = new File(arch.getName() + "\\" + salida.getName());                    
                    FileOutputStream fos = new FileOutputStream(arch_temp.getAbsolutePath());
                    
                    int leer;
                    byte[] buffer = new byte[1024];

                    while (0 < (leer = zis.read(buffer))) {
                            fos.write(buffer, 0, leer);
                    }
                        
                    fos.close();
                } 
                zis.closeEntry();     
            }
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Recibimos cada archivo enviado por el cliente
    public static void reciveFile(DataInputStream dis, String ruta){
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
                
                escritos += recibidos;
                System.out.println("Recibidos: " + escritos + " tam " + tam);
            }            
            System.out.println("Archivo: " + ruta + nombre + " recibido correctamente.");
            
            
        } catch (Exception e) {
            System.out.println("No se pudo recibir el archivo del cliente.");
            e.printStackTrace();
        }
    }
}   

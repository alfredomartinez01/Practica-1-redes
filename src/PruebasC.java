
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;


public class PruebasC {
    public static void main(String[] args){
        int puerto = 8000;
        String direccion = "192.168.0.109";
        
        try {
            Socket skt_cliente = conectar(puerto, direccion); // La direccion dependerá del la IP del servidor
            System.out.println("Conexión establecida en: " + direccion + ":" + puerto);
            File[] seleccion = getChoice();
            
            
             // Creamos el flujo de escritura
            DataOutputStream dos = new DataOutputStream(skt_cliente.getOutputStream());
            //dos.write(seleccion.length); // Enviamos el número de elementos
            
            for(File arch : seleccion){
                if(arch.isDirectory()){
                    dos.write(1); // Indicamos el envío de un directorio
                    
                } else{
                    //dos.write(0); // Indicamos el envío de un archivo
                    
                    String nombre = arch.getName();
                    long tam = arch.length();
                    String ruta = arch.getAbsolutePath();

                    try {  
                        // Creamos el flujo de escritura
                        DataInputStream dis = new DataInputStream(new FileInputStream(ruta));

                        // Enviando metadatos del archivo
                        dos.writeUTF(nombre); // Indicamos el nombre
                        dos.writeLong(tam); // Indicando el tamaño en bytes
                        //dos.flush();
                        
                        // Enviando archivo
                        int leidos;
                        for(long enviados = 0; enviados < tam; ){
                            byte[] b = new byte[1500]; // Enviamos en paquete de 1500 bytes
                            leidos = dis.read(b);
                            
                            dos.write(b, 0, leidos); // Leemos desde el byte 0 hasta el número de leídos del archivo
                            dos.flush();
                            enviados += leidos;
                            System.out.println("Enviados: " + enviados + " tam " + tam);
                        }

                        System.out.println("Archivo: " + ruta + " enviado correctamente.");              

                    } catch (Exception e) {
                        System.out.println("No se pudo enviar el archivo: " + ruta);
                        e.printStackTrace();
                    }
                }
            }
            dos.close();
            skt_cliente.close();
            
            
        } catch (IOException ex) {
            Logger.getLogger(PruebasC.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Conexión fallida en: " + direccion + ":" + puerto);
        }
             
    }
    // Conectamos al servidor
    public static Socket conectar(int puerto, String direccion) throws IOException{
        return new Socket(direccion, puerto);
    }
    // Obtenemos lo que seleccionó para subir
    public static File[] getChoice(){
        // Lanzamos el file chooser para elegir carpetas o directorios
        JFileChooser f_chooser = new JFileChooser();
        f_chooser.setMultiSelectionEnabled(true);
        f_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int seleccion = f_chooser.showOpenDialog(null);
        
        File[] archs = null;
        if(seleccion == JFileChooser.APPROVE_OPTION){
            archs = f_chooser.getSelectedFiles();
        }
        
        return archs;
    }
    
}

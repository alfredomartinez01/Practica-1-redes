
/**
 * Cliente que envía y recibe los archivos que solicita al servidor
 */

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class Cliente {
    
    static int puerto = 8000;
    static String direccion = "192.168.0.109";
    
    public static void main(String[] args){
        
        try {            
            System.out.println("Opciones:"
                                + "1.- Subir archivos o carpetas"
                                + "2.- Bajar archivos o carpetas");
            
            
            /**
                * Argumentos para envío y recepción de datos con el servidor
                *  0: enviando archivo
                *  1: enviando directorio
            */
            
            /* En caso de que se vaya enviar */
            
            // Conectamos al servidor
            Socket skt_cliente = conectar(puerto, direccion); // La direccion dependerá del la IP del servidor
            System.out.println("Conexión establecida en: " + direccion + ":" + puerto);
            File[] seleccion = getChoice();
            
            for(File arch : seleccion){
                if(arch.isDirectory()){
                    sendDirectory(arch, skt_cliente);
                } else{
                    sendFile(arch, skt_cliente, null, null);
                }
            }
            skt_cliente.close();
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Conexión faliida en: " + direccion + ":" + puerto);
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
    
    // Enviamos un archivo
    public static void sendFile(File arch, Socket skt_cliente, DataOutputStream out, DataInputStream in){
        String nombre = arch.getName();
        long tam = arch.length();
        String ruta = arch.getAbsolutePath();
        
        try {  
            // Creamos los flujos
            DataOutputStream dos;
            DataInputStream dis;
            // Checamos si tenemos flujos previos o creamos uno nuevo para el envío del archivo
            dos = (out != null) ?  out : new DataOutputStream(skt_cliente.getOutputStream());
            dis = (in != null) ? in : new DataInputStream(new FileInputStream(ruta));
            
            // Enviando metadatos del archivo
            dos.write(0); // Indicamos el envío de un archivo
            dos.writeUTF(nombre); // Indicamos el nombre
            dos.writeLong(tam); // Indicando el tamaño en bytes
            dos.flush();
            
            // Enviando archivo
            int leidos;
            for(long enviados = 0; enviados < tam; ){
                byte[] b = new byte[1500]; // Enviamos en paquete de 1500 bytes
                leidos = dis.read(b);
                
                dos.write(b, 0, leidos); // Leemos desde el byte 0 hasta el número de leídos del archivo
                dos.flush();
                enviados += leidos;
            }
            System.out.println("Archivo: " + ruta + " enviado correctamente.");
            
            if(out == null){
                dos.close();
            }
            if(in == null){
                dis.close();
            }        
            
        } catch (IOException e) {
            System.out.println("No se pudo enviar el archivo: " + ruta);
            e.printStackTrace();
        }
        
    }
    
    // Enviamos un directorio
    public static void sendDirectory(File direct, Socket skt_cliente){
        String nombre = direct.getName();
        String ruta = direct.getAbsolutePath();
        
        try {
            // Creamos los flujos
            DataOutputStream dos = new DataOutputStream(skt_cliente.getOutputStream());
            
            // Enviando metadatos de la carpeta
            dos.write(1);
            dos.writeUTF(nombre);
            
            // Enviando cada uno de los archivos del directorio
            for(File arch : new File(ruta).listFiles()){
                sendFile(arch, skt_cliente, dos, null);
            }
            
            System.out.println("Directorio: " + ruta + "\\ enviada correctamente.");            
            dos.close();
            
        } catch (IOException e) {
            System.out.println("No se pudo enviar el directorio: " + ruta + "\\");
            e.printStackTrace();
        }        
    }
}

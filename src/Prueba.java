
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kit_5
 */
public class Prueba {
    public static void main(String[] args){
        
        // Creamos un folder provicional de prueba
        File f = new File("");
        String ruta = f.getAbsolutePath();
        File comprimido = new File( ruta + "\\" + "Google Drive Simulation - P1.zip" );
        descomprimir(comprimido);        
    }
    
    // Descomprimimos el archivo
    public static boolean descomprimir(File arch){
        try {
            // Creamos los flujos para trabajar el zip
            ZipInputStream zis = new ZipInputStream(new FileInputStream(arch.getName()));
            ZipEntry salida;
            
            // Recorremos cada archivo
            while (null != (salida = zis.getNextEntry())) {
                
                System.out.println("Nombre del Archivo: "+salida.getName());	

                // Creamos flujo de escritura del archivo
                File arch_temp;
                
                if(salida.isDirectory()){
                    
                    arch_temp = new File(arch.getParent() + "\\" + salida.getName()); // Abrimos el directorio
                    if(!arch_temp.isDirectory()) arch_temp.mkdir(); // Creamos el directorio si no está creado
                    
                    
                } else {
                    // Creamos el flujo del archivo
                    arch_temp = new File(arch.getParent() + "\\" + salida.getName());                    
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
}

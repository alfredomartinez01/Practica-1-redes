
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Archivo {
    private String nombre;
    private long tam;
    private String ultima_mod;
    private File file;
    
    public Archivo(File archivo){
        this.file = archivo;
    }
    
    public Archivo(){
        
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getTam() {
        return tam;
    }

    public File getFile() {
        return file;
    }

    public void setTam(long tam) {
        this.tam = tam;
    }
    
    public String getUltima_mod() {
        return ultima_mod;
    }

    public void setUltima_mod(String ultima_mod) {
        this.ultima_mod = ultima_mod;
    }
    
    // Comprimir archivos
    public static File comprimir(File archs[]) {
        String ruta = archs[0].getParentFile().getAbsolutePath(); // carpeta del proyecto
        System.out.println(ruta);
        
        try {
            ZipOutputStream zous = new ZipOutputStream(new FileOutputStream(ruta + "\\" + "ArchivoComprimido.zip")); // carpeta del proyecto/ArchivoComprimido.zip
                        
            System.out.println("Comprimiendo...");
            
            // Recorrer todos los archivos y carptetas seleccionadas
            for (int i = 0; i < archs.length; i++) {                
                compresor(zous, archs[i], archs[i].getName()); // Comprimimos cada elemento, el tercer argumento representa la carpeta donde están los seleccionados
            }
            
            zous.close();
            
            return  new File(ruta + "\\" + "ArchivoComprimido.zip");

        } catch (Exception e) {
            System.out.println("Error al comprimir");
            e.printStackTrace();
            return null;
        }
    }
    
    // Descomprimimos el archivo
    public static void descomprimir(File arch){
        
        try {
            // Creamos los flujos para trabajar el zip
            ZipInputStream zis = new ZipInputStream(new FileInputStream(arch.getAbsolutePath()));
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
            zis.close();            
            //this.file.delete();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Compresor archivo/carpeta
    private static void compresor(ZipOutputStream zous, File arch, String base) throws IOException {
        // NOTA: base será la ruta relativa a la carpeta donde se encuentran los archivos/carpetas seleccionados
                
        ZipEntry entrada = null;
        FileInputStream fis = null;
        
        if (arch.isDirectory()) {

            entrada = new ZipEntry(base + "/");
            zous.putNextEntry(entrada);
            
            base = base.length() == 0 ? "" : base + "/";
            
            File[] a_internos = arch.listFiles();
            for (File a_interno : a_internos) {
                compresor(zous, a_interno, base + a_interno.getName());
        }

        } else {
            
            // Creamos flujo de entrada desde el archivo
            fis = new FileInputStream(arch);
            
            entrada = new ZipEntry(base);
            zous.putNextEntry(entrada);

            int leer;
            byte[] b = new byte[1500];
            while (0 < (leer = fis.read(b))) {
                zous.write(b, 0, leer);
            }
            
            fis.close();
        }
        zous.closeEntry();
    }    
}

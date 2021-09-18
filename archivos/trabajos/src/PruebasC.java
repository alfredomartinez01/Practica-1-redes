
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

public class PruebasC {

    public static void main(String[] args) {

        int puerto = 8000;
        String direccion = "192.168.0.109"; // Dirección IP 

        try {
            Socket skt_cliente = conectar(puerto, direccion); // La direccion dependerá del la IP del servidor
            System.out.println("Conexión establecida en: " + direccion + ":" + puerto);
            File[] seleccion = getChoice();

            // Compresión de archivos            
            File comprimido = comprimir(seleccion);

            // Creamos el flujo de escritura
            DataOutputStream dos = new DataOutputStream(skt_cliente.getOutputStream());
            sendFile(comprimido, dos);
            
            dos.close();
            skt_cliente.close();
            
            System.out.println("Borrando archivo...");  
            comprimido.delete();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Conexión fallida en: " + direccion + ":" + puerto);
        }

    }

    // Conectamos al servidor
    public static Socket conectar(int puerto, String direccion) throws IOException {
        return new Socket(direccion, puerto);
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
            
            return new File(ruta + "\\" + "ArchivoComprimido.zip");

        } catch (Exception e) {
            System.out.println("Error al comprimir");
            e.printStackTrace();
        }

        return null;
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

    // Enviamos un archivo
    public static void sendFile(File arch, DataOutputStream dos) {
        String nombre = arch.getName();
        long tam = arch.length();
        String ruta = arch.getAbsolutePath();

        try {
            // Creamos el flujo de escritura
            DataInputStream dis = new DataInputStream(new FileInputStream(ruta));

            // Enviando metadatos del archivo
            dos.writeUTF(nombre); // Indicamos el nombre
            dos.writeLong(tam); // Indicando el tamaño en bytes
            dos.flush();

            // Enviando archivo
            int leidos;
            for (long enviados = 0; enviados < tam;) {
                byte[] b = new byte[1500]; // Enviamos en paquete de 1500 bytes
                leidos = dis.read(b);

                dos.write(b, 0, leidos); // Leemos desde el byte 0 hasta el número de leídos del archivo
                dos.flush();
                enviados += leidos;
                System.out.println("Enviados: " + enviados + " tam " + tam);
            }
            
            dis.close();
            System.out.println("Archivo: " + ruta + " enviado correctamente.");
                        

        } catch (Exception e) {
            System.out.println("No se pudo enviar el archivo: " + ruta);
            e.printStackTrace();
        }

    }
}
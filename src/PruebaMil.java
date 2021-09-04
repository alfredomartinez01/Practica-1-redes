
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PruebaMil {

    
 public static void zip(String inputFileName) throws Exception {
        String zipFileName = "E: \\ test.zip"; // Nombre de archivo después del empaque
        //System.out.println(zipFileName);
        zip(zipFileName, new File(inputFileName));

    }

    private static void zip (String zipFileName, File inputFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                zipFileName));
        zip(out, inputFile, "");
        //System.out.println("zip done");
        out.close();
    }

    private static void zip(ZipOutputStream out, File f, String base) throws Exception {
        if (f.isDirectory()) {
            
            File[] fl = f.listFiles();
            // Descargas
            // Descargas/asas.pdf
            
            out.putNextEntry(new ZipEntry(base + "/"));
            
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
            
        } else {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(f);
            int b;
            //System.out.println(base);
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            in.close();
        }
    }

    public static void main(String[] temp) {
        try {
            zip("E: \\ ftl"); // La carpeta que desea comprimir
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

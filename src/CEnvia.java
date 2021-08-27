import java.net.*;
import java.io.*;
import javax.swing.JFileChooser;
/**
 *
 * @author axele
 */
public class CEnvia {
    public static void main(String[] args){
        try{
            int pto = 8000;
            String dir = "127.0.0.1";
            Socket cl = new Socket(dir,pto);
            System.out.println("Conexion con servidor establecida.. lanzando FileChooser..");
            JFileChooser jf = new JFileChooser();
            jf.setMultiSelectionEnabled(true);
            int r = jf.showOpenDialog(null);
            if(r==JFileChooser.APPROVE_OPTION){
                File f = jf.getSelectedFile();
                //File[] f = jf.getSelectedFiles();
                 File ff= new File("");
                 String z = ff.getAbsolutePath()+"\\";
                 System.out.println("Ruta:"+z);
                 File ff2 = new File(z);
                File[] archivos = ff2.listFiles();
                System.out.println("Hay "+archivos.length+"archivos");
            for(int i=0;i<archivos.length;i++){
                String xx = archivos[i].getAbsolutePath();
                xx = (archivos[i].isDirectory())? xx+ "/":xx;
                System.out.println(xx); 
            }//
                String nombre = f.getName();
                String path = f.getAbsolutePath();
                long tam = f.length();
                System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(path));
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();
                long enviados = 0;
                int l=0,porcentaje=0;
                while(enviados<tam){
                    byte[] b = new byte[1500];
                    l=dis.read(b);
                    System.out.println("enviados: "+l);
                    dos.write(b,0,l);// dos.write(b);
                    dos.flush();
                    enviados = enviados + l;
                    porcentaje = (int)((enviados*100)/tam);
                    System.out.print("\rEnviado el "+porcentaje+" % del archivo");
                }//while
                System.out.println("\nArchivo enviado..");
                dis.close();
                dos.close();
                cl.close();
            }//if
        }catch(Exception e){
            e.printStackTrace();
        }//catch
    }//main
}

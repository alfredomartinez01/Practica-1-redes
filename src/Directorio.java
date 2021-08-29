import java.io.File;
import java.util.ArrayList;

public class Directorio {
    private String nombre;
    private File directorio;
    private ArrayList<Archivo> archivos = null;
    private long tam;
    
    public Directorio(String ruta){
        directorio = new File(ruta);
        nombre = directorio.getName();
        archivos = getFiles(ruta); //Obtenemos los archivos del directorio y sumamos los tamaños
    }
    
    public ArrayList<Archivo> getFiles(String ruta_directorio){
        archivos = new ArrayList<Archivo>();
        tam = 0;
        
        for(File arch : new File(ruta_directorio).listFiles()){
            // Inicializamos y agregamos cada archivo al arreglo
            Archivo archivo = new Archivo(arch);
            archivos.add(archivo); 
            // Sumamos los tamaños
            tam += archivo.tam;
        }
        return archivos;
    }
    
    
    public class Archivo{
        File archivo;
        private String nombre;
        private long tam;

        public Archivo(File archivo){
            this.archivo = archivo;
            this.nombre = archivo.getName();
            this.tam = archivo.length();
        }
    }
}

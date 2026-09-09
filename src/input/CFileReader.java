package input;


import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class CFileReader{

     public char[] readFile() {
        char[] charArray = null;

        // 1. Crear el diálogo nativo (Usa un Frame nulo como dueño)
        FileDialog fileDialog = new FileDialog((Frame) null, "Selecciona el archivo de codigo c (.c o .txt)", FileDialog.LOAD);
        
        // 2. Hacerlo visible (bloquea la ejecución hasta que el usuario elija o cancele)
        fileDialog.setVisible(true);

        // 3. Obtener el archivo seleccionado
        String directory = fileDialog.getDirectory();
        String filename = fileDialog.getFile();

        // Si el usuario canceló, filename será null
        if (filename != null) {
            File selectedFile = new File(directory, filename);
            Path path = Path.of(selectedFile.getAbsolutePath());

            System.out.println("Archivo seleccionado: " + path.toString());

            try {
                String textFile = Files.readString(path);
                charArray = textFile.toCharArray(); 
                for (char c : charArray) {
                    System.out.print(c);
                }
                return charArray;      

            } catch (IOException e) {
                System.out.println("Ocurrió un error al leer el archivo: " + e.getMessage());
            }    
        } else {
            System.out.println("Operación cancelada por el usuario.");
        }  
        
        return charArray;
    }

}
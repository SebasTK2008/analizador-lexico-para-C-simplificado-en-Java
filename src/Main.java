

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;

public class Main {
        public static void main(String[] args) {
            
        JFileChooser selector = new JFileChooser();
 
        int resultado = selector.showOpenDialog(null);

        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            // Obtener el archivo seleccionado
            File selectedArchive = selector.getSelectedFile();
            
             Path path = Path.of(selectedArchive.getAbsolutePath());

              JOptionPane.showMessageDialog(null, "Archivo seleccionado: " + path.toString());

            try{

                String textFile = Files.readString(path);
                System.out.println(textFile);
                char[] charArray = textFile.toCharArray(); 
                for (char c : charArray) {
                    System.out.print(c);
                }      

            } catch (IOException e) {
                System.out.println("Ocurrió un error al leer el archivo: " + e.getMessage());
            }    
         
            
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada por el usuario.");
        }
    
           
            
            








            
    
        }
}

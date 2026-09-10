import input.CFileReader;
import java.util.ArrayList;
import lexer.Scanner;

public class Main {
        public static void main(String[] args) {

            //crea un objeto de la clase que permite leer el archivo de texto
            CFileReader reader = new CFileReader();
            char[] charArray = reader.readFile();   //charArray es un arreglo con todos los caracteres del archivo .c

            Scanner scanner = new Scanner(charArray);
            ArrayList<String> lexemas = scanner.getLexemas();

            for (String lexema : lexemas) {
                System.out.print(lexema+" ");
            }
            
        }
}

import input.CFileReader;
import lexer.Scanner;

public class Main {
        public static void main(String[] args) {

            //crea un objeto de la clase que permite leer el archivo de texto
            CFileReader reader = new CFileReader();
            char[] charArray = reader.readFile();   //charArray es un arreglo con todos los caracteres del archivo .c

            if (charArray != null) {
                Scanner scanner = new Scanner(charArray);
                scanner.getLexema();
            }
   
        }
}

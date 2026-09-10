package lexer;

public enum Alfhabet { //un enum que almacena los alfabetos que se usaran para clasificar un caracter en un lexema.
    UNDERSCORE, // este es _
    LETTER,  //estas son las letras [a-z A-Z]
    DIGIT, // digitos [0-9]
    SIMBOL,  //aqui serian todos los simbolos que no se pueden llegar a repetir: ( ) { } [ ] ; + - * / ,
    EQUAL,   //el = se puede repetir entonces tiene su propio alfabeto y lo mismo con < > | y &
    LESS_THAN, 
    GREATER_THAN, 
    NEGATION,
    OR,
    AND,
    WHITE_SPACE // espacio en blanco, tabulacion o salto de linea
}
   

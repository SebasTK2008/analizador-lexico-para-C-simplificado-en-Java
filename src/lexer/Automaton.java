package lexer;

import java.util.EnumSet;
import java.util.Set;

//esta clase contiene la tabla de transiciones del automata que se diseñó junto con los estados de aceptación y el metodo que clasifica un caracter en un alfabeto.
public class Automaton {

    //orden de columnas: UNDERSCORE, LETTER, DIGIT, SIMBOL, EQUAL, LESS_THAN, GREATER_THAN,
    //                    NEGATION, OR, AND, WHITE_SPACE, HASH, CARET, DOT, TILDE, MINUS
    public final Status [][] table={
        //START
        {Status.IN_IDENTIFIER, Status.IN_IDENTIFIER, Status.IN_NUMBER, Status.IN_SIMBOL, Status.IN_EQUAL, Status.IN_LESS_THAN, Status.IN_GREATER_THAN, Status.IN_NEGATION, Status.IN_OR, Status.IN_AND, Status.START, Status.IN_SIMBOL, Status.IN_SIMBOL, Status.IN_SIMBOL, Status.IN_SIMBOL, Status.IN_MINUS},
        //IN_IDENTIFIER
        {Status.IN_IDENTIFIER, Status.IN_IDENTIFIER, Status.IN_IDENTIFIER, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //IN_LESS_THAN (<= o <<)
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_LESS_THAN, Status.IN_LESS_THAN, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //IN_GREATER_THAN (>= o >>)
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_GREATER_THAN, Status.STOP, Status.IN_GREATER_THAN, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //IN_NEGATION (!=)
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_NEGATION, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //IN_OR (||)
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_OR, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //IN_AND (&&)
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_AND, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //IN_NUMBER
        {Status.STOP, Status.STOP, Status.IN_NUMBER, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //IN_EQUAL (==)
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_EQUAL, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //IN_SIMBOL (simbolos de un solo caracter: { } [ ] ( ) ; + * / , # ^ . ~)
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //IN_MINUS (- o ->)
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_ARROW, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //IN_ARROW (->)
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        //STOP (fila de relleno, nunca se usa como origen de una transicion)
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP}
   };

   //este metodo clasifica un caracter en un alfabeto, fue necesario porque los alfabetos podian contener diferentes caracteres como el de letras, numeros o simbolos.
   public Alphabet classify(char c) {
        if (c == '_') return Alphabet.UNDERSCORE;  
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) return Alphabet.LETTER;
        if (c >= '0' && c <= '9') return Alphabet.DIGIT;
        if (c == '=') return Alphabet.EQUAL;
        if (c == '<') return Alphabet.LESS_THAN;
        if (c == '>') return Alphabet.GREATER_THAN;
        if (c == '!') return Alphabet.NEGATION;
        if (c == '|') return Alphabet.OR;
        if (c == '&') return Alphabet.AND;
        if (c == '#') return Alphabet.HASH;
        if (c == '^') return Alphabet.CARET;
        if (c == '.') return Alphabet.DOT;
        if (c == '~') return Alphabet.TILDE;
        if (c == '-') return Alphabet.MINUS;
        if (c == '{' || c == '}' || c == '[' || c == ']' || c == '(' || c == ')' || c == ';' || c == '+' || c == '*' || c == '/' || c == ',') {
            return Alphabet.SIMBOL;
        }
        if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f' || c == 0x000B) { // 0x000B es equivalente al \v, que es un salto de linea vertical.
            return Alphabet.WHITE_SPACE;
        }
        return Alphabet.INVALID;
    }

    //aqui se definieron los estados que son de aceptacion de la tabla de transiciones.
    public final Set<Status> aceptance = EnumSet.of(
        Status.IN_IDENTIFIER, Status.IN_LESS_THAN, Status.IN_GREATER_THAN,
        Status.IN_NEGATION, Status.IN_OR, Status.IN_AND,
        Status.IN_NUMBER, Status.IN_EQUAL, Status.IN_SIMBOL,
        Status.IN_MINUS, Status.IN_ARROW
    );

    public boolean isAceptance(Status estado) {
        return aceptance.contains(estado);
    }
}
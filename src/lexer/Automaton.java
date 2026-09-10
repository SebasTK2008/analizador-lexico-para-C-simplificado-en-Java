package lexer;

import java.util.EnumSet;
import java.util.Set;

//esta clase contiene la tabla de transiciones del automata que se diseñó junto con los estados de aceptación y el metodo que clasifica un caracter en un alfabeto.
public class Automaton {
    //esta es la tabla de transiciones del automata pero en matriz, donde cada fila es un estado y cada columna es un alfabeto, y el valor de la celda es el siguiente estado.
    public final Status [][] table={
        {Status.IN_IDENTIFIER, Status.IN_IDENTIFIER, Status.IN_NUMBER, Status.IN_SIMBOL, Status.IN_EQUAL, Status.IN_LESS_THAN, Status.IN_GREATER_THAN, Status.IN_NEGATION, Status.IN_OR, Status.IN_AND, Status.START},
        {Status.IN_IDENTIFIER, Status.IN_IDENTIFIER, Status.IN_IDENTIFIER, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_LESS_THAN,  Status.IN_LESS_THAN, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_GREATER_THAN,  Status.STOP, Status.IN_GREATER_THAN, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_NEGATION,  Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP,  Status.STOP, Status.STOP, Status.STOP, Status.IN_OR, Status.STOP, Status.STOP},
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP,  Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_AND, Status.STOP},
        {Status.STOP, Status.STOP, Status.IN_NUMBER, Status.STOP, Status.STOP,  Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.IN_EQUAL,  Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP,  Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP},        
        {Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP,  Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP, Status.STOP}
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
        if (c=='{'||c=='}'||c=='['||c==']'||c=='('||c==')'||c==';'||c=='+'||c=='-'||c=='*'||c=='/'||c==',') {
            return Alphabet.SIMBOL;
        }
        return Alphabet.WHITE_SPACE;  
    }

    //aqui se definieron los estados que son de aceptacion de la tabla de transiciones.
    public final Set<Status> aceptance = EnumSet.of(
        Status.IN_IDENTIFIER, Status.IN_LESS_THAN, Status.IN_GREATER_THAN,
        Status.IN_NEGATION, Status.IN_OR, Status.IN_AND,
        Status.IN_NUMBER, Status.IN_EQUAL, Status.IN_SIMBOL
    );

    public boolean isAceptance(Status estado) {
        return aceptance.contains(estado);
    }
}

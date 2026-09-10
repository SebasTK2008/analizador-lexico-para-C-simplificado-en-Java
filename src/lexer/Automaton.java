package lexer;
import java.util.EnumSet;
import java.util.Set;

public class Automaton { //esta clase contiene la tabla de transiciones del automata que se diseñó junton con los estados de aceptacion y el metodo que clasifica un caracter en un alfabeto.
    
    private Status status;
    private Alfhabet alfhabet;

    //esta es la tabla de transiciones del automata pero en matriz, donde cada fila es un estado y cada columna es un alfabeto, y el valor de la celda es el siguiente estado.
    public final Status [][] table={
        {status.IN_IDENTIFIER, status.IN_IDENTIFIER, status.IN_NUMBER, status.IN_SIMBOL, status.IN_EQUAL, status.IN_LESS_THAN, status.IN_GREATER_THAN, status.IN_NEGATION, status.IN_OR, status.IN_AND, status.START},
        {status.IN_IDENTIFIER, status.IN_IDENTIFIER, status.IN_IDENTIFIER, status.STOP, status.STOP, status.STOP, status.STOP, status.STOP, status.STOP, status.STOP, status.STOP},
        {status.STOP, status.STOP, status.STOP, status.STOP, status.IN_LESS_THAN,  status.IN_LESS_THAN, status.STOP, status.STOP, status.STOP, status.STOP, status.STOP},
        {status.STOP, status.STOP, status.STOP, status.STOP, status.IN_GREATER_THAN,  status.STOP, status.IN_GREATER_THAN, status.STOP, status.STOP, status.STOP, status.STOP},
        {status.STOP, status.STOP, status.STOP, status.STOP, status.IN_NEGATION,  status.STOP, status.STOP, status.STOP, status.STOP, status.STOP, status.STOP},
        {status.STOP, status.STOP, status.STOP, status.STOP, status.STOP,  status.STOP, status.STOP, status.STOP, status.IN_OR, status.STOP, status.STOP},
        {status.STOP, status.STOP, status.STOP, status.STOP, status.STOP,  status.STOP, status.STOP, status.STOP, status.STOP, status.IN_AND, status.STOP},
        {status.STOP, status.STOP, status.IN_NUMBER, status.STOP, status.STOP,  status.STOP, status.STOP, status.STOP, status.STOP, status.STOP, status.STOP},
        {status.STOP, status.STOP, status.STOP, status.STOP, status.IN_EQUAL,  status.STOP, status.STOP, status.STOP, status.STOP, status.STOP, status.STOP},
        {status.STOP, status.STOP, status.STOP, status.STOP, status.STOP,  status.STOP, status.STOP, status.STOP, status.STOP, status.STOP, status.STOP},        
        {status.STOP, status.STOP, status.STOP, status.STOP, status.STOP,  status.STOP, status.STOP, status.STOP, status.STOP, status.STOP, status.STOP}
   };

   //este metodo clasifica un caracter en un alfabeto, fue necesario porque los alfabetos podian contener diferentes caracteres como el de letras, numeros o simbolos.
   public Alfhabet classify(char c) {
        if (c == '_') return Alfhabet.UNDERSCORE;  
        if (Character.isLetter(c)) return Alfhabet.LETTER;
        if (Character.isDigit(c)) return Alfhabet.DIGIT;
        if (c == '=') return Alfhabet.EQUAL;
        if (c == '<') return Alfhabet.LESS_THAN;
        if (c == '>') return Alfhabet.GREATER_THAN;
        if (c == '!') return Alfhabet.NEGATION;
        if (c == '|') return Alfhabet.OR;
        if (c == '&') return Alfhabet.AND;
        if (c=='{'||c=='}'||c=='['||c==']'||c=='('||c==')'||c==';'||c=='+'||c=='-'||c=='*'||c=='/'||c==',') {
            return Alfhabet.SIMBOL;
        }
        return Alfhabet.WHITE_SPACE;  
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

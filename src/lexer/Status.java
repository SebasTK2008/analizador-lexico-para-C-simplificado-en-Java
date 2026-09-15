package lexer;

/*
    Enum que contiene los estados del automata.
    el estado START es el estado inicial y el estado STOP es el estado de error/terminacion de lexema.
*/
public enum Status {
    START,
    IN_IDENTIFIER, //el automata entra a este estado si comienza con _ o alguna letra. Luego se clasifica si ese lexema es un identificador o una palabra reservada.
    /* estos estados son para poder manejar los simbolos que se repiten como <, >, =, !, | y &.
    Por ejemplo si el automata entra a IN_LESS_THAN y luego recibe un = entonces el lexema es <= */
    IN_LESS_THAN,
    IN_GREATER_THAN,
    IN_NEGATION,
    IN_OR,
    IN_AND,
    IN_NUMBER,
    IN_EQUAL,
    IN_SIMBOL,
    IN_MINUS, //estado al que se llega tras leer un '-'. Si el siguiente caracter es '>' se forma el operador "->".
    IN_ARROW, //estado de aceptacion que representa el lexema "->" ya completo (no se extiende mas).
    STOP
}
package lexer;

//este enum representa el alfabeto de entrada del automata. Cada caracter del archivo .c se clasifica
//en uno de estos elementos antes de ser usado en la tabla de transiciones.
public enum Alphabet {
    UNDERSCORE,   // _
    LETTER,       // [a-z A-Z]
    DIGIT,        // [0-9]
    SIMBOL,       // simbolos que no se repiten: { } [ ] ( ) ; + * / ,
    EQUAL,        // = (se puede repetir junto a otros simbolos formando ==, <=, >=, !=)
    LESS_THAN,    // 
    GREATER_THAN, // >
    NEGATION,     // !
    OR,           // |
    AND,          // &
    WHITE_SPACE,  // espacio, tabulacion, salto de linea, etc.
    HASH,         // # (directivas de preprocesamiento: #include, #define, etc.)
    CARET,        // ^ (operador logico bit a bit XOR)
    DOT,          // . (operador de acceso, ej: stdio.h)
    TILDE,        // ~ (operador logico bit a bit NOT)
    MINUS,        // - (se separa del resto de simbolos porque puede formar "->")
    INVALID
}
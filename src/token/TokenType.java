package token;

public enum TokenType {
    //palabras reservadas del lenguaje C simplificado (seccion 2.1 del enunciado)
    INT,
    MAIN,
    VOID,
    BREAK,
    DO,
    ELSE,
    IF,
    WHILE,
    RETURN,
    READ,   // scanf
    WRITE,  // printf

    //palabras reservadas adicionales del lenguaje C completo (tabla de la seccion 3.3)
    AUTO,
    DOUBLE,
    STRUCT,
    LONG,
    SWITCH,
    CASE,
    ENUM,
    REGISTER,
    TYPEDEF,
    CHAR,
    EXTERN,
    UNION,
    CONST,
    FLOAT,
    SHORT,
    UNSIGNED,
    CONTINUE,
    FOR,
    SIGNED,
    DEFAULT,
    GOTO,
    SIZEOF,
    VOLATILE,
    STATIC,

    //directivas de preprocesamiento
    HASH,           // #
    PREPROC_INCLUDE,
    PREPROC_DEFINE,
    PREPROC_IF,
    PREPROC_ELSE,
    PREPROC_ELIF,
    PREPROC_ENDIF,
    PREPROC_ERROR,
    PREPROC_IFDEF,
    PREPROC_IFNDEF,
    PREPROC_MESSAGE,
    PREPROC_UNDEF,

    //simbolos especiales de agrupacion
    LBRACE,
    RBRACE,
    LSQUARE,
    RSQUARE,
    LPAR,
    RPAR,
    SEMI,

    //operadores aritmeticos
    PLUS,
    MINUS,
    MUL_OP,
    DIV_OP,

    //operadores logicos bit a bit / logicos
    AND_OP,
    OR_OP,
    NOT_OP,
    XOR_OP, // ^
    BW_NOT, // ~

    //asignacion y relacionales
    ASSIGN,
    LT,
    GT,
    SHL_OP,
    SHR_OP,
    EQ,
    NOTEQ,
    LTEQ,
    GTEQ,
    ANDAND,
    OROR,

    //otros operadores
    COMMA,
    DOT,    // .
    ARROW,  // ->

    //literales
    INT_NUM,
    ID,
    STRING,   // "..."
    CHAR_LIT  // '...'
}
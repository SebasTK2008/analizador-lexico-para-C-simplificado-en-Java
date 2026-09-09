package lexer;

public enum Status {
    START,
    IN_NUMBER,
    IN_IDENTIFIER,
    IN_STRING,
    IN_COMMENT
}

package lexer;

import token.Token;
import token.TokenType;
import java.util.Map;

public class Scanner {

    private Status status;
    private char[] buffer;
    private Map<String, TokenType> reservedWords;
    private Map<String, TokenType> specialSymbols;

    public Scanner(char[] buffer) {
        this.buffer = buffer;
        status = Status.START;
        initializeReservedWords();
        initializeSpecialSymbols();
    }

    public Token[] classifyAllTokens(String[] lexemas) {
        Token[] tokens = new Token[lexemas.length];
        for (int i = 0; i < lexemas.length; i++) {
            tokens[i] = classifyToken(lexemas[i]);
        }
        return tokens;
    }

    public Token classifyToken(String lexema){
        if (reservedWords.containsKey(lexema)) {
            return new Token(reservedWords.get(lexema), lexema);
        } 
        if (specialSymbols.containsKey(lexema)) {
            return new Token(specialSymbols.get(lexema), lexema);
        }
        if (isInteger(lexema)) {
            return new Token(TokenType.INT_NUM, lexema);
        }
        if (isIdentifier(lexema)) {
            return new Token(TokenType.ID, lexema);
        }

        throw new IllegalArgumentException("Lexema no reconocido: " + lexema);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLexema(){
        return "";
    }

    private void initializeReservedWords() {
        reservedWords = Map.ofEntries(
                Map.entry("int", TokenType.INT),
                Map.entry("main", TokenType.MAIN),
                Map.entry("void", TokenType.VOID),
                Map.entry("break", TokenType.BREAK),
                Map.entry("do", TokenType.DO),
                Map.entry("else", TokenType.ELSE),
                Map.entry("if", TokenType.IF),
                Map.entry("while", TokenType.WHILE),
                Map.entry("return", TokenType.RETURN),
                Map.entry("read", TokenType.READ),
                Map.entry("write", TokenType.WRITE)
        );
    }

    private void initializeSpecialSymbols() {
        specialSymbols = Map.ofEntries(
                Map.entry("{", TokenType.LBRACE),
                Map.entry("}", TokenType.RBRACE),
                Map.entry("[", TokenType.LSQUARE),
                Map.entry("]", TokenType.RSQUARE),
                Map.entry("(", TokenType.LPAR),
                Map.entry(")", TokenType.RPAR),
                Map.entry(";", TokenType.SEMI),
                Map.entry("+", TokenType.PLUS),
                Map.entry("-", TokenType.MINUS),
                Map.entry("*", TokenType.MUL_OP),
                Map.entry("/", TokenType.DIV_OP),
                Map.entry("&", TokenType.AND_OP),
                Map.entry("|", TokenType.OR_OP),
                Map.entry("!", TokenType.NOT_OP),
                Map.entry("=", TokenType.ASSIGN),
                Map.entry("<", TokenType.LT),
                Map.entry(">", TokenType.GT),
                Map.entry("<<", TokenType.SHL_OP),
                Map.entry(">>", TokenType.SHR_OP),
                Map.entry("==", TokenType.EQ),
                Map.entry("!=", TokenType.NOTEQ),
                Map.entry("<=", TokenType.LTEQ),
                Map.entry(">=", TokenType.GTEQ),
                Map.entry("&&", TokenType.ANDAND),
                Map.entry("||", TokenType.OROR),
                Map.entry(",", TokenType.COMMA)
        );
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isIdentifier(String lexema) {
        if (lexema.isEmpty()) {
            return false;
        }
        char firstChar = lexema.charAt(0);
        if (!isLetter(firstChar) && firstChar != '_') {
            return false;
        }
        for (int i = 1; i < lexema.length(); i++) {
            char c = lexema.charAt(i);
            if (!isLetter(c) && !isDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }

    private boolean isInteger(String lexema) {
        if (lexema.isEmpty()) {
            return false;
        }
        for (int i = 0; i < lexema.length(); i++) {
            char c = lexema.charAt(i);
            if (!isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}

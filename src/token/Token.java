package token;

public class Token {
    private TokenType tipo;
    private String lexema;

    public Token(TokenType tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }

    public TokenType getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    @Override 
    public String toString() {
        return "Token: " + tipo + " " + "\"" + lexema + "\"";
    }
}
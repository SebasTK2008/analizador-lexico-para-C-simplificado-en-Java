package lexer;

import token.Token;
import token.TokenType;
import java.util.Map;
import java.util.ArrayList;

public class Scanner { //esta clase recorre el arreglo de caracteres, obtiene un lexema, lo guarda en una lista de lexemas en donde luego cada lexema sera clasificado por tipo.

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

    public ArrayList<Token> classifyAllTokens(ArrayList<String> lexemas) {
        ArrayList<Token> tokens = new ArrayList<>();
        for (int i = 0; i < lexemas.size(); i++) {
            tokens.add(classifyToken(lexemas.get(i)));
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


    //metodo que recorre el arreglo de caracteres y retorna una lista con todos los lexemas encontrados gracias al automata.
    public ArrayList<String> getLexemas() {
        status = Status.START; //reinicia el estado del automata al comienzo de la lectura del arreglo de caracteres.
        Automaton automaton = new Automaton();
        StringBuilder lexema = new StringBuilder(); //se uso stringbuilder para ir creando el lexema a medida que se recorre el arreglo de caracteres.
        ArrayList<String> lexemas = new ArrayList<>(); 

        for (char c : buffer) {
            Alphabet alphabet = automaton.classify(c);  //aqui toma un caracter y lo clasifica en un elementeo del alfabeto.

            if (alphabet == Alphabet.WHITE_SPACE) {  //aqui clasifico si el caracter es un espacio en blanco.

                if (status != Status.START) {  //luego, si el espacio en blanco no esta al comienzo si no despues entonces se guarda el lexema.
                    
                    if (automaton.isAceptance(status)) { 
                        lexemas.add(lexema.toString());
                    }
                    lexema.setLength(0);  //si el espacio en blanco esta al comienzo entonces no se hace append y se reinicia el lexema.
                }
                status = Status.START;  //y si el espacio en blanco esta al comienzo entonces se reinicia el estado del automata al caracter despues del espacio.
                continue; 
            }

            Status nextStatus = automaton.table[status.ordinal()][alphabet.ordinal()];  //aqui buscamos a que celda corresponde el estado actual y el alfabeto del caracter, y obtenemos el siguiente estado.
                                                                                        //esta viene siendo la transicion del automata.

            if (nextStatus == Status.STOP) { //si el siguiente estado es STOP entonces significa que el lexema ya termino y se guarda en la lista de lexemas.
                
                if (automaton.isAceptance(status)) {
                    lexemas.add(lexema.toString());
                }
                lexema.setLength(0); //luego se reinicia el lexema.
                status = automaton.table[Status.START.ordinal()][alphabet.ordinal()]; //y el estado tambien se reinicia comensando desde el start nuevamente.
                lexema.append(c); //y como ya se reinicio, se comienza a leer desde el caracter actual que no pudo ser clasificado en el lexema anterior.
            } else {  //si el siguiente estado no es STOP entonces significa que el lexema sigue y se hace append del caracter al lexema.
                lexema.append(c);
                status = nextStatus;
            }
        }

        if (automaton.isAceptance(status) && lexema.length() > 0) { //al final del recorrido del arreglo de caracteres, si el estado actual es de aceptacion y el lexema no esta vacio, entonces se guarda el lexema en la lista de lexemas.
            lexemas.add(lexema.toString());
        }

        return lexemas;
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
                Map.entry("scanf", TokenType.READ),
                Map.entry("printf", TokenType.WRITE)
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

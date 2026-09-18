package lexer;

import java.util.ArrayList;
import java.util.Map;
import token.Token;
import token.TokenType;

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
        //los literales de cadena y de caracter se detectan primero porque su contenido
        //puede incluir cualquier caracter y no deben buscarse en los demas mapas.
        if (isStringLiteral(lexema)) {
            return new Token(TokenType.STRING, lexema);
        }
        if (isCharLiteral(lexema)) {
            return new Token(TokenType.CHAR_LIT, lexema);
        }
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

        int i = 0;
        while (i < buffer.length) {
            char c = buffer[i];

            //manejo especial de literales de cadena ("..."), ya que su contenido puede incluir
            //cualquier caracter (incluso los que el alfabeto general no reconoce) y secuencias
            //de escape como \n, \t, \", \\, etc.
            if (c == '"') {
                if (status != Status.START) { //si habia un lexema en construccion se cierra antes de empezar la cadena.
                    if (automaton.isAceptance(status)) {
                        lexemas.add(lexema.toString());
                    }
                    lexema.setLength(0);
                    status = Status.START;
                }
                i = readQuotedLiteral(buffer, i, '"', lexemas);
                continue;
            }

            //manejo especial de literales de caracter ('...'), misma razon que las cadenas.
            if (c == '\'') {
                if (status != Status.START) {
                    if (automaton.isAceptance(status)) {
                        lexemas.add(lexema.toString());
                    }
                    lexema.setLength(0);
                    status = Status.START;
                }
                i = readQuotedLiteral(buffer, i, '\'', lexemas);
                continue;
            }

            Alphabet alphabet = automaton.classify(c);  //aqui toma un caracter y lo clasifica en un elementeo del alfabeto.

            if (alphabet == Alphabet.INVALID) {
                throw new IllegalArgumentException("Caracter no reconocido '" + c + "' en la posición " + i);
            }  

            if (alphabet == Alphabet.WHITE_SPACE) {  //aqui clasifico si el caracter es un espacio en blanco.

                if (status != Status.START) {  //luego, si el espacio en blanco no esta al comienzo si no despues entonces se guarda el lexema.
                    
                    if (automaton.isAceptance(status)) { 
                        lexemas.add(lexema.toString());
                    }
                    lexema.setLength(0);  //si el espacio en blanco esta al comienzo entonces no se hace append y se reinicia el lexema.
                }
                status = Status.START;  //y si el espacio en blanco esta al comienzo entonces se reinicia el estado del automata al caracter despues del espacio.
                i++;
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
            i++;
        }

        if (automaton.isAceptance(status) && lexema.length() > 0) { //al final del recorrido del arreglo de caracteres, si el estado actual es de aceptacion y el lexema no esta vacio, entonces se guarda el lexema en la lista de lexemas.
            lexemas.add(lexema.toString());
        }

        return lexemas;
    }

    //lee un literal delimitado por comillas (dobles o simples) a partir de la posicion 'start' (donde esta la comilla de apertura),
    //respetando las secuencias de escape (\n, \t, \r, \0, \\, \v, \f, \a, \", \'), y agrega el lexema completo (con comillas incluidas)
    //a la lista de lexemas. Retorna el indice siguiente a la comilla de cierre.
    private int readQuotedLiteral(char[] buffer, int start, char quote, ArrayList<String> lexemas) {
        StringBuilder literal = new StringBuilder();
        literal.append(quote);
        int i = start + 1;
        boolean closed = false;

        while (i < buffer.length) {
            char sc = buffer[i];
            if (sc == '\\' && i + 1 < buffer.length) { //secuencia de escape: se toma el backslash y el caracter siguiente como una unidad.
                literal.append(sc).append(buffer[i + 1]);
                i += 2;
                continue;
            }
            if (sc == quote) {
                literal.append(sc);
                i++;
                closed = true;
                break;
            }
            literal.append(sc);
            i++;
        }

        if (!closed) {
            String tipo = (quote == '"') ? "Cadena de texto" : "Caracter literal";
            throw new IllegalArgumentException(tipo + " sin cerrar a partir de la posición " + start);
        }

        lexemas.add(literal.toString());
        return i;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLexema(){
        return "";
    }

    private void initializeReservedWords() {
        reservedWords = Map.ofEntries(
                //palabras reservadas del lenguaje C simplificado
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
                Map.entry("printf", TokenType.WRITE),

                //palabras reservadas adicionales del lenguaje C completo
                Map.entry("auto", TokenType.AUTO),
                Map.entry("double", TokenType.DOUBLE),
                Map.entry("struct", TokenType.STRUCT),
                Map.entry("long", TokenType.LONG),
                Map.entry("switch", TokenType.SWITCH),
                Map.entry("case", TokenType.CASE),
                Map.entry("enum", TokenType.ENUM),
                Map.entry("register", TokenType.REGISTER),
                Map.entry("typedef", TokenType.TYPEDEF),
                Map.entry("char", TokenType.CHAR),
                Map.entry("extern", TokenType.EXTERN),
                Map.entry("union", TokenType.UNION),
                Map.entry("const", TokenType.CONST),
                Map.entry("float", TokenType.FLOAT),
                Map.entry("short", TokenType.SHORT),
                Map.entry("unsigned", TokenType.UNSIGNED),
                Map.entry("continue", TokenType.CONTINUE),
                Map.entry("for", TokenType.FOR),
                Map.entry("signed", TokenType.SIGNED),
                Map.entry("default", TokenType.DEFAULT),
                Map.entry("goto", TokenType.GOTO),
                Map.entry("sizeof", TokenType.SIZEOF),
                Map.entry("volatile", TokenType.VOLATILE),
                Map.entry("static", TokenType.STATIC),

                //directivas de preprocesamiento (el simbolo # se clasifica aparte como HASH)
                Map.entry("include", TokenType.PREPROC_INCLUDE),
                Map.entry("define", TokenType.PREPROC_DEFINE),
                Map.entry("elif", TokenType.PREPROC_ELIF),
                Map.entry("endif", TokenType.PREPROC_ENDIF),
                Map.entry("error", TokenType.PREPROC_ERROR),
                Map.entry("ifdef", TokenType.PREPROC_IFDEF),
                Map.entry("ifndef", TokenType.PREPROC_IFNDEF),
                Map.entry("message", TokenType.PREPROC_MESSAGE),
                Map.entry("undef", TokenType.PREPROC_UNDEF)
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
                Map.entry("^", TokenType.XOR_OP),
                Map.entry("~", TokenType.BW_NOT),
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
                Map.entry(",", TokenType.COMMA),
                Map.entry(".", TokenType.DOT),
                Map.entry("->", TokenType.ARROW),
                Map.entry("#", TokenType.HASH)
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

    private boolean isStringLiteral(String lexema) {
        return lexema.length() >= 2 && lexema.charAt(0) == '"' && lexema.charAt(lexema.length() - 1) == '"';
    }

    private boolean isCharLiteral(String lexema) {
        return lexema.length() >= 2 && lexema.charAt(0) == '\'' && lexema.charAt(lexema.length() - 1) == '\'';
    }
}
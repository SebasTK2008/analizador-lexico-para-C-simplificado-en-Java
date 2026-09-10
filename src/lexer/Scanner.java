package lexer;

import java.util.ArrayList;

public class Scanner { //esta clase recorre el arreglo de caracteres, obtiene un lexema, lo guarda en una lista de lexemas en donde luego cada lexema sera clasificado por tipo.

    private Status status;
    private char[] buffer;

    public Scanner(char[] buffer) {
        this.buffer = buffer;
        this.status = Status.START;
    }


    public void classifyToken(String lexema){
        

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
    
}

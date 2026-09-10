package lexer;

public class Scanner {

    private Status status;
    private char[] buffer;

    public Scanner(char[] buffer) {
        this.buffer = buffer;
        this.status = Status.START;
    }


    public void classifyToken(){


    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void tokenize(){
        
    }

    
}

package interpreter.exceptions;

import java.util.LinkedList;
import java.util.List;

public abstract class SetlException extends RuntimeException {

    private LinkedList<String> trace;

    public SetlException(String msg) {
        super(msg);
        trace = new LinkedList<String>();
        trace.add(msg);
    }

    public void addToTrace(String msg) {
        trace.addFirst(msg);
    }

    public List<String> getTrace(){
        return trace;
    }
}

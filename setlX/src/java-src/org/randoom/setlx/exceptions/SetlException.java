package org.randoom.setlx.exceptions;

import java.util.LinkedList;
import java.util.List;

public abstract class SetlException extends Exception {

    private static final long serialVersionUID = -3764480484946122585L;
    private LinkedList<String> trace;

    public SetlException(String msg) {
        super(msg);
        trace = new LinkedList<String>();
        trace.add(msg);
    }

    public void addToTrace(String msg) {
        trace.addFirst(msg);
    }

    public List<String> getTrace() {
        return trace;
    }
}


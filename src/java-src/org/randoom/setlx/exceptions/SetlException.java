package org.randoom.setlx.exceptions;

import java.util.ArrayList;
import java.util.List;

public abstract class SetlException extends Exception {

    private static final long           serialVersionUID = -3764480484946122585L;
    private              List<String>   trace;

    public SetlException(String msg) {
        super(msg);
        trace = new ArrayList<String>();
        trace.add(msg);
    }

    public void addToTrace(String msg) {
        trace.add(msg);
    }

    public List<String> getTrace() {
        return trace;
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.utilities.Environment;

// printErr(value, ...)          : same as print, but prints into stderr

public class PD_printErr extends PD_print {
    public final static PreDefinedFunction DEFINITION = new PD_printErr();

    private PD_printErr() {
        super("printErr");
    }

    protected void print(String txt) {
        Environment.errWrite(txt);
    }

    protected void printEndl() {
        Environment.errWriteLn();
    }
}


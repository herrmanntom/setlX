package org.randoom.setlx.functions;

// printErr(value, ...)    : same as print, but prints into stderr

public class PD_printErr extends PD_print {
    public final static PreDefinedFunction DEFINITION = new PD_printErr();

    private PD_printErr() {
        super("printErr");
    }

    protected void print(String txt) {
        System.err.print(txt);
    }
}


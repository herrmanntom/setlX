package org.randoom.setlx.functions;

import org.randoom.setlx.utilities.Environment;

// nPrint(value, ...)            : same as print, but does not add a new line

public class PD_nPrint extends PD_print {
    public final static PreDefinedFunction DEFINITION = new PD_nPrint();

    private PD_nPrint() {
        super("nPrint");
    }

    protected void print(String txt) {
        Environment.outWrite(txt);
    }

    protected void printEndl() { /* prints NO newline at the end */ }
}


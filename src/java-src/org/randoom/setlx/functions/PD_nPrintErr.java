package org.randoom.setlx.functions;

import org.randoom.setlx.utilities.Environment;

// nPrintErr(value, ...)         : same as nPrint, but prints into stderr

public class PD_nPrintErr extends PD_print {
    public final static PreDefinedFunction DEFINITION = new PD_nPrintErr();

    private PD_nPrintErr() {
        super("nPrintErr");
    }

    protected void print(String txt) {
        Environment.errWrite(txt);
    }

    protected void printEndl() { /* prints NO newline at the end */ }
}


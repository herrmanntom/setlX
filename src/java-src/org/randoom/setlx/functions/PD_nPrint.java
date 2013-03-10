package org.randoom.setlx.functions;

import org.randoom.setlx.utilities.State;

// nPrint(value, ...)            : same as print, but does not add a new line

public class PD_nPrint extends PD_print {
    public final static PreDefinedFunction DEFINITION = new PD_nPrint();

    private PD_nPrint() {
        super();
    }

    @Override
    protected void print(final State state, final String txt) {
        state.outWrite(txt);
    }

    @Override
    protected void printEndl(final State state) { /* prints NO newline at the end */ }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.utilities.State;

/**
 *  nPrintErr([value], ...) : Same as nPrint, but prints into stderr.
 */
public class PD_nPrintErr extends PD_print {
    /** Definition of the PreDefinedProcedure `nPrintErr'. */
    public final static PreDefinedProcedure DEFINITION = new PD_nPrintErr();

    private PD_nPrintErr() {
        super();
    }

    @Override
    protected void print(final State state, final String txt) {
        state.errWrite(txt);
    }

    @Override
    protected void printEndl(final State state) { /* prints NO newline at the end */ }
}


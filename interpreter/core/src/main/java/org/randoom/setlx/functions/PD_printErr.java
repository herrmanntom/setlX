package org.randoom.setlx.functions;

import org.randoom.setlx.utilities.State;

/**
 *  printErr([value], ...) : Same as print, but prints into stderr.
 */
public class PD_printErr extends PD_print {
    /** Definition of the PreDefinedProcedure `printErr'. */
    public final static PreDefinedProcedure DEFINITION = new PD_printErr();

    private PD_printErr() {
        super();
    }

    @Override
    protected void print(final State state, final String txt) {
        state.errWrite(txt);
    }

    @Override
    protected void printEndl(final State state) {
        state.errWriteLn();
    }
}


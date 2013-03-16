package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// dbgHelp()                     : DEBUG: print debugger commands and their explanation

public class PD_dbgHelp extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_dbgHelp();

    private PD_dbgHelp() {
        super();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        final String message = "Debugger Commands:\n" +
                               "  trace(boolean); // enable/disable output of assignments\n" +
                               "  setBreak(\"id\"); // set breakpoint in function bound to \"id\"\n" +
                               "  rmBreak(\"id\");  // remove breakpoint in function bound to \"id\"\n" +
                               "  lsBreak();      // list breakpoints\n" +
                               "  step();         // execute next statement\n" +
                               "  uStep();        // halt before evaluating next expression\n" +
                               "  fStep();        // continue execution of next function until it returns\n" +
                               "  finish();       // continue execution of current function until it returns\n" +
                               "  finishLoop();   // continue execution of current loop until it finishes\n" +
                               "  resume();       // resume normal execution\n" +
                               "  reset();        // stop execution and return to interactive prompt\n";

        state.errWriteLn(message);
        return new SetlError("dbgHelp");
    }
}


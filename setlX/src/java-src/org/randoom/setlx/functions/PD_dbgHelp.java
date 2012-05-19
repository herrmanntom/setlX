package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// dbgHelp()                     : DEBUG: print debugger commands and their explanation

public class PD_dbgHelp extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_dbgHelp();

    private PD_dbgHelp() {
        super("dbgHelp");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        String message  = "Debugger Commands:\n" +
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

        Environment.errWriteLn(message);
        return Om.OM.hide();
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// lsBreak()                     : DEBUG: list breakpoints

public class PD_lsBreak extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_lsBreak();

    private PD_lsBreak() {
        super("lsBreak");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        final String head    = "Debugger Breakpoints:\n";
        String       message = head;

        for (String point : Environment.getAllBreakpoints()) {
            message += "  " + point + "\n";
        }

        if (message.equals(head)) {
            message += " no breakpoints set\n";
        }

        Environment.errWriteLn(message);

        return Om.OM;
    }
}


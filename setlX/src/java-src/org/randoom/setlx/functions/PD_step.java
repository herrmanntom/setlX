package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.State;

import java.util.List;

// step()                        : DEBUG: execute next statement

public class PD_step extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_step();

    private PD_step() {
        super("step");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        DebugPrompt.stopPrompt();
        return Om.OM.hide();
    }
}


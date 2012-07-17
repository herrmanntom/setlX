package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// setBreak("id")                : DEBUG: set breakpoint in first statement of function bound to "id"

public class PD_setBreak extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_setBreak();

    private PD_setBreak() {
        super("setBreak");
        addParameter("id");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        Value   id  = args.get(0);
        if ( ! (id instanceof SetlString)) {
            throw new IncompatibleTypeException("Id-argument '" + id + "' is not a string.");
        }

        Environment.setBreakpoint(((SetlString) id).getUnquotedString());

        return SetlBoolean.TRUE;
    }
}


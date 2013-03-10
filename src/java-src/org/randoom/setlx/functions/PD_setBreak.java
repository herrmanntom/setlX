package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// setBreak("id")                : DEBUG: set breakpoint in first statement of function bound to "id"

public class PD_setBreak extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_setBreak();

    private PD_setBreak() {
        super();
        addParameter("id");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value   id  = args.get(0);
        if ( ! (id instanceof SetlString)) {
            throw new IncompatibleTypeException("Id-argument '" + id + "' is not a string.");
        }

        state.setBreakpoint(((SetlString) id).getUnquotedString());

        return SetlBoolean.TRUE;
    }
}


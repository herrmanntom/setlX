package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// rmBreak("id")                 : DEBUG: removes breakpoint in function bound to "id"

public class PD_rmBreak extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_rmBreak();

    private PD_rmBreak() {
        super("rmBreak");
        addParameter("id");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value   id  = args.get(0);
        if ( ! (id instanceof SetlString)) {
            throw new IncompatibleTypeException("Id-argument '" + id + "' is not a string.");
        }

        if (id.equals(new SetlString("*"))) {
            state.removeAllBreakpoints();
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.valueOf(state.removeBreakpoint(((SetlString) id).getUnquotedString()));
        }
    }
}


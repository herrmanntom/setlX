package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.State;

import java.util.List;

// rmBreak("id")                 : DEBUG: removes breakpoint in function bound to "id"

public class PD_rmBreak extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_rmBreak();

    private PD_rmBreak() {
        super("rmBreak");
        addParameter("id");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        Value   id  = args.get(0);
        if ( ! (id instanceof SetlString)) {
            throw new IncompatibleTypeException("Id-argument '" + id + "' is not a string.");
        }

        if (id.equals(new SetlString("*"))) {
            Environment.removeAllBreakpoints();
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.valueOf(Environment.removeBreakpoint(((SetlString) id).getUnquotedString()));
        }
    }
}


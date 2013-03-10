package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.utilities.State;

import java.util.List;

// trim(string)                  : return a trimmed string

public class PD_trim extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_trim();

    private PD_trim() {
        super();
        addParameter("string");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value string  = args.get(0);
        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "String-argument '" + string + "' is not a string."
            );
        }
        return new SetlString(string.getUnquotedString().trim());
    }

}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.utilities.State;

import java.util.List;

// endsWith(string, suffix)      : return true if string ends with prefix

public class PD_endsWith extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_endsWith();

    private PD_endsWith() {
        super();
        addParameter("string");
        addParameter("suffix");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value string = args.get(0);
        final Value suffix = args.get(1);
        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "String-argument '" + string + "' is not a string."
            );
        }

        if ( ! (suffix instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Suffix-argument '" + suffix + "' is not a string."
            );
        }

        if (string.getUnquotedString().endsWith(suffix.getUnquotedString())) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

}

package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * endsWith(string, suffix) : Return true if string ends with suffix.
 */
public class PD_endsWith extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `endsWith'. */
    public final static PreDefinedProcedure DEFINITION = new PD_endsWith();

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
                "String-argument '" + string.toString(state) + "' is not a string."
            );
        }

        if ( ! (suffix instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Suffix-argument '" + suffix.toString(state) + "' is not a string."
            );
        }

        if (string.getUnquotedString(state).endsWith(suffix.getUnquotedString(state))) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

}

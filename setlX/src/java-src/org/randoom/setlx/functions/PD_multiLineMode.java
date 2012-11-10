package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.State;

import java.util.List;

// multiLineMode(toggle)         : only accept input after additional new line

public class PD_multiLineMode extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_multiLineMode();

    private PD_multiLineMode() {
        super("multiLineMode");
        addParameter("toggle");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        Value   toggle  = args.get(0);
        if ( ! (toggle instanceof SetlBoolean)) {
            throw new IncompatibleTypeException(
                "Toggle-argument '" + toggle + "' is not a Boolean value."
            );
        }

        if (toggle == SetlBoolean.TRUE) {
            Environment.setMultiLineMode(true);
        } else /* if (toggle == SetlBoolean.FALSE) */ {
            Environment.setMultiLineMode(false);
        }

        // everything seems fine
        return toggle;
    }
}


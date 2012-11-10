package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;

import java.util.List;

// loadLibrary(name)             : loads SetlX library code file and executes it

public class PD_loadLibrary extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_loadLibrary();

    private PD_loadLibrary() {
        super("loadLibrary");
        addParameter("name");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value   nameArg = args.get(0);
        if ( ! (nameArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Name-argument '" + nameArg + "' is not a string."
            );
        }

        // get string of name to be parsed
        String  name    = nameArg.getUnquotedString();

        // parse the file
        ParseSetlX.resetErrorCount();
        Block   blk     = ParseSetlX.parseLibrary(name);

        // execute the contents
        blk.execute(state);

        // everything is good
        return SetlBoolean.TRUE;
    }
}


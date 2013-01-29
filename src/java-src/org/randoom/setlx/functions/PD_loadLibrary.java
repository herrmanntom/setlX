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

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value   nameArg = args.get(0);
        if ( ! (nameArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Name-argument '" + nameArg + "' is not a string."
            );
        }

        // get string of name to be parsed
        final String  name    = nameArg.getUnquotedString();

        // parse the file
        state.resetParserErrorCount();
        final Block   blk     = ParseSetlX.parseLibrary(state, name);

        // execute the contents
        blk.exec(state);

        // everything is good
        return SetlBoolean.TRUE;
    }
}


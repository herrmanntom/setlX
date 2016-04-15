package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * loadLibrary(name) : Loads SetlX library code file and executes it.
 */
public class PD_loadLibrary extends PreDefinedProcedure {

    private final static ParameterDefinition NAME       = createParameter("name");

    /** Definition of the PreDefinedProcedure `loadLibrary'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_loadLibrary();

    private PD_loadLibrary() {
        super();
        addParameter(NAME);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value nameArg = args.get(NAME);
        if ( ! (nameArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Name-argument '" + nameArg.toString(state) + "' is not a string."
            );
        }

        // get string of name to be parsed
        final String name = nameArg.getUnquotedString(state);

        // parse the file
        state.resetParserErrorCount();
        final Block  blk  = ParseSetlX.parseLibrary(state, name);

        // execute the contents
        blk.execute(state);

        // everything is good
        return SetlBoolean.TRUE;
    }
}


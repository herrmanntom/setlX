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
 * load(path_to_setlX_file) : Loads SetlX source code file and executes it.
 */
public class PD_load extends PreDefinedProcedure {

    private final static ParameterDefinition PATH_TO_SETLX_FILE = createParameter("pathToSetlXfile");

    /** Definition of the PreDefinedProcedure `load'. */
    public  final static PreDefinedProcedure DEFINITION         = new PD_load();

    private PD_load() {
        super();
        addParameter(PATH_TO_SETLX_FILE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value filePath = args.get(PATH_TO_SETLX_FILE);
        if ( ! (filePath instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Path-argument '" + filePath.toString(state) + "' is not a string."
            );
        }

        // get string of file path to be parsed
        final String file = filePath.getUnquotedString(state);

        // parse the file
        state.resetParserErrorCount();
        final Block  blk  = ParseSetlX.parseFile(state, file);

        // execute the contents
        blk.execute(state);

        // everything is good
        return SetlBoolean.TRUE;
    }
}


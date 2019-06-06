package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.io.File;
import java.util.HashMap;

/**
 * makeDirectory(directoryPath)  : Creates directory including parents, if possible.
 */
public class PD_makeDirectory extends PreDefinedProcedure {

    private final static ParameterDefinition DIRECTORY_PATH = createParameter("directoryPath");

    public  final static PreDefinedProcedure DEFINITION = new PD_makeDirectory();

    private PD_makeDirectory() {
        super();
        addParameter(DIRECTORY_PATH);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value dirArg = args.get(DIRECTORY_PATH);
        if (dirArg.isString() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("DirectoryPath-argument '" + dirArg.toString(state) + "' is not a string.");
        }

        File file = new File(dirArg.getUnquotedString(state));

        if (file.mkdirs()) {
            return SetlBoolean.TRUE;
        }
        return SetlBoolean.FALSE;
    }
}


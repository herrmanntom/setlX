package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.io.File;
import java.util.HashMap;

/**
 * existsFile(fileName) : check if indicated file exists and returns corresponding boolean value.
 */
public class PD_existsFile extends PreDefinedProcedure {

    private final static ParameterDefinition FILE_NAME            = createParameter("fileName");

    public  final static PreDefinedProcedure DEFINITION           = new PD_existsFile();

    private PD_existsFile() {
        super();
        addParameter(FILE_NAME);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value fileArg = args.get(FILE_NAME);
        if ( ! (fileArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "FileName-argument '" + fileArg.toString(state) + "' is not a string."
            );
        }

        return SetlBoolean.valueOf(
                new File(
                        fileArg.getUnquotedString(state)
                ).exists()
        );
    }
}


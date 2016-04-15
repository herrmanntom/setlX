package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.io.File;
import java.util.HashMap;

/**
 * deleteFile(fileName) : delete a file, return true on success
 */
public class PD_deleteFile extends PreDefinedProcedure {

    private final static ParameterDefinition FILE_NAME  = createParameter("fileName");

    /** Definition of the PreDefinedProcedure `deleteFile'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_deleteFile();

    private PD_deleteFile() {
        super();
        addParameter(FILE_NAME);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
        final Value filePath = args.get(FILE_NAME);
        if ( ! (filePath instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "FileName-argument '" + filePath.toString(state) + "' is not a string."
            );
        }

        final String fileName = filePath.getUnquotedString(state);

        final File   file     = new File(fileName);

        return SetlBoolean.valueOf(file.delete());
    }
}


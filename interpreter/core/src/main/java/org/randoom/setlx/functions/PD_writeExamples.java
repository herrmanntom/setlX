package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.files.EncodedExampleFiles;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.EncodedFilesWriter;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;
import java.util.List;

/**
 * writeExamples(directoryPath)  : (Re)writes the example files of this version into the specified directory.
 */
public class PD_writeExamples extends PreDefinedProcedure {

    private final static ParameterDefinition DIRECTORY_NAME = createParameter("directoryName");

    /** Definition of the PreDefinedProcedure `writeExamples'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_writeExamples();

    /**
     * Create a new writeExamples function.
     */
    private PD_writeExamples() {
        super();
        addParameter(DIRECTORY_NAME);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value  directoryArg = args.get(DIRECTORY_NAME);
        if (directoryArg.isString() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("DirectoryName-argument '" + directoryArg.toString(state) + "' is not a string.");
        }

        String result = "Wrote example files:\n";
        List<String> exampleFiles = EncodedFilesWriter.write(
                state,
                directoryArg.getUnquotedString(state),
                null,
                EncodedExampleFiles.getBase64EncodedFiles()
        );
        for (int i = 0; i < exampleFiles.size(); i++) {
            if (i > 0) {
                result += "\n";
            }
            result += exampleFiles.get(i);
        }

        return new SetlString(result);
    }


}


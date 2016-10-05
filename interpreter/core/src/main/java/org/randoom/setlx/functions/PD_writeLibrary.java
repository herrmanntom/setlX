package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.EncodedLibraryFiles;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;
import java.util.List;

/**
 * writeLibrary()  : (Re)writes the library files of this version into the library path.
 */
public class PD_writeLibrary extends PreDefinedProcedure {

    /** Definition of the PreDefinedProcedure `writeLibrary'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_writeLibrary();

    /**
     * Create a new writeLibrary function.
     */
    private PD_writeLibrary() {
        super();
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        String result = "Wrote library files:\n";
        List<String> libraryFiles = EncodedLibraryFiles.write(state);
        for (int i = 0; i < libraryFiles.size(); i++) {
            if (i > 0) {
                result += "\n";
            }
            result += libraryFiles.get(i);
        }

        return new SetlString(result);
    }


}


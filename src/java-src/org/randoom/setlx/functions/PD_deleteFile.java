package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.io.File;
import java.util.List;

// deleteFile(fileName)          : delete a file, return true on success

public class PD_deleteFile extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_deleteFile();

    private PD_deleteFile() {
        super("deleteFile");
        addParameter("fileName");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value     filePath    = args.get(0);
        if ( ! (filePath instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "FileName-argument '" + filePath + "' is not a string."
            );
        }

        final String    fileName    = filePath.getUnquotedString();

        final File      file        = new File(fileName);

        return SetlBoolean.valueOf(file.delete());
    }
}


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

// load(path)                    : loads SetlX source code file and executes it

public class PD_load extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_load();

    private PD_load() {
        super();
        addParameter("path_to_setlX_file");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value   filePath            = args.get(0);
        if ( ! (filePath instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Path-argument '" + filePath + "' is not a string."
            );
        }

        // get string of file path to be parsed
        final String  file    = filePath.getUnquotedString();

        // parse the file
        state.resetParserErrorCount();
        final Block   blk     = ParseSetlX.parseFile(state, file);

        // execute the contents
        blk.exec(state);

        // everything is good
        return SetlBoolean.TRUE;
    }
}


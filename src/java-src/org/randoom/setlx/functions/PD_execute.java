package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;

import java.util.List;

// execute(stmnts)               : execute a String of SetlX statements

public class PD_execute extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_execute();

    private PD_execute() {
        super();
        addParameter("setlX_statements");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value   stmntArg = args.get(0);
        if ( ! (stmntArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Statement-argument '" + stmntArg + "' is not a string."
            );
        }

        // get statement string to be parsed
        final String  stmntStr = stmntArg.getUnquotedString();

        // parse statements
        state.resetParserErrorCount();
        final Block   blk      = ParseSetlX.parseStringToBlock(state, stmntStr);

        // execute the contents
        blk.execute(state);

        // everything seems fine
        return SetlBoolean.TRUE;
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * execute(setlX_statements) : Execute a String of SetlX statements.
 */
public class PD_execute extends PreDefinedProcedure {

    private final static ParameterDefinition SETLX_STATEMENTS = createParameter("setlX_statements");

    /** Definition of the PreDefinedProcedure `execute'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_execute();

    private PD_execute() {
        super();
        addParameter(SETLX_STATEMENTS);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value stmntArg = args.get(SETLX_STATEMENTS);
        if ( ! (stmntArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Statement-argument '" + stmntArg.toString(state) + "' is not a string."
            );
        }

        // get statement string to be parsed
        final String stmntStr = stmntArg.getUnquotedString(state);

        // parse statements
        state.resetParserErrorCount();
        final Block  blk      = ParseSetlX.parseStringToBlock(state, stmntStr);

        // execute the contents
        blk.execute(state);

        // everything seems fine
        return SetlBoolean.TRUE;
    }
}


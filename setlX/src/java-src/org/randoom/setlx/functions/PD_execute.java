package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParseSetlX;

import java.util.List;

// execute(stmnts)            : execute a String of SetlX statements

public class PD_execute extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_execute();

    private PD_execute() {
        super("execute");
        addParameter("setlX_statements");
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value   stmntArg = args.get(0);
        if ( ! (stmntArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Statement-argument '" + stmntArg + "' is not a string."
            );
        }

        // get statement string to be parsed
        String  stmntStr = stmntArg.getUnquotedString();

        // parse statements
        ParseSetlX.resetErrorCount();
        Block   blk      = ParseSetlX.parseStringToBlock(stmntStr);

        // execute the contents
        blk.execute();

        // everything seems fine
        return SetlBoolean.TRUE;
    }
}


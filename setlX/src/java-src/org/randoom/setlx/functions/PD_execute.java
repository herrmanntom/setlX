package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.ParseSetlX;

import java.util.List;

// execute(stmnts [, output]) : execute a String of SetlX statements, if `output' is true results of statements are printed when in interactive mode

public class PD_execute extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_execute();

    private PD_execute() {
        super("execute");
        addParameter("setlX_statements");
        addParameter("output");           // optional parameter
        allowFewerParameters();
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        if (args.size() < 1) {
            throw new IncorrectNumberOfParametersException(
                "Procedure 'execute' is defined with either one or two parameters."
            );
        }
        Value   stmntArg = args.get(0);
        if ( ! (stmntArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Statement-argument '" + stmntArg + "' is not a string."
            );
        }

        // check and get optional 2nd parameter
        Value   doNotDisableOutput  = SetlBoolean.FALSE;
        if (args.size() == 2) {
            doNotDisableOutput  = args.get(1);
            if ( ! (doNotDisableOutput instanceof SetlBoolean)) {
                throw new IncompatibleTypeException(
                    "do-not-disable-output-argument '" + doNotDisableOutput + "' is not a Boolean value."
                );
            }
        }

        // get statement string to be parsed
        String  stmntStr = stmntArg.getUnquotedString();

        // parse statements
        ParseSetlX.resetErrorCount();
        Block   blk      = ParseSetlX.parseStringToBlock(stmntStr);

        // execute the contents
        boolean printAfterEval = Environment.isPrintAfterEval();
        try {
            if (doNotDisableOutput == SetlBoolean.FALSE) {
                Environment.setPrintAfterEval(false);
            }
            blk.execute();
        } finally {
            Environment.setPrintAfterEval(printAfterEval);
        }

        // newline to visually separate result
        if (printAfterEval && doNotDisableOutput == SetlBoolean.TRUE) {
            System.out.println();
        }

        // everything seems fine
        return SetlBoolean.TRUE;
    }
}


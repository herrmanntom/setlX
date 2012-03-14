package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.IncorrectNumberOfParametersException;
import interpreter.exceptions.SetlException;
import interpreter.statements.Block;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlError;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

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
            throw new IncorrectNumberOfParametersException("Procedure 'execute' is defined with either one or two parameters.");
        }
        Value   stmntArg = args.get(0);
        if ( ! (stmntArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Statement-argument '" + stmntArg + "' is not a string.");
        }

        // check and get optional 2nd parameter
        Value   doNotDisableOutput  = SetlBoolean.FALSE;
        if (args.size() == 2) {
            doNotDisableOutput  = args.get(1);
            if ( ! (doNotDisableOutput instanceof SetlBoolean)) {
                throw new IncompatibleTypeException("do-not-disable-output-argument '" + doNotDisableOutput + "' is not a Boolean value.");
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


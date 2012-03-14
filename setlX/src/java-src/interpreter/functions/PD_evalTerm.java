package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.IncorrectNumberOfParametersException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.statements.Statement;
import interpreter.types.Om;
import interpreter.types.SetlBoolean;
import interpreter.types.Value;
import interpreter.utilities.CodeFragment;
import interpreter.utilities.Environment;
import interpreter.utilities.TermConverter;

import java.util.List;

// evalTerm(term [, output]) : execute a term which represents SetlX statements and/or expressions, if `output' is true results of statements are printed when in interactive mode

public class PD_evalTerm extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_evalTerm();

    private PD_evalTerm() {
        super("evalTerm");
        addParameter("term");
        addParameter("output"); // optional parameter
        allowFewerParameters();
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        if (args.size() < 1) {
            throw new IncorrectNumberOfParametersException("Procedure 'evalTerm' is defined with either one or two parameters.");
        }
        Value   termArg             = args.get(0);

        // check and get optional 2nd parameter
        Value   doNotDisableOutput  = SetlBoolean.FALSE;
        if (args.size() == 2) {
            doNotDisableOutput  = args.get(1);
            if ( ! (doNotDisableOutput instanceof SetlBoolean)) {
                throw new IncompatibleTypeException("do-not-disable-output-argument '" + doNotDisableOutput + "' is not a Boolean value.");
            }
        }

        // get code to be executed
        CodeFragment    fragment    = TermConverter.valueToCodeFragment(termArg, false);

        // Value to be returned
        Value           result      = Om.OM;

        // execute the contents
        boolean printAfterEval = Environment.isPrintAfterEval();
        try {
            if (doNotDisableOutput == SetlBoolean.FALSE) {
                Environment.setPrintAfterEval(false);
            }

            if (fragment instanceof Expr) {
                result  = ((Expr) fragment).eval();
            } else /* if (fragment instanceof Statement) */ {
                ((Statement) fragment).execute();

                // newline to visually separate result
                if (printAfterEval && doNotDisableOutput == SetlBoolean.TRUE) {
                    System.out.println();
                }
            }
        } finally {
            Environment.setPrintAfterEval(printAfterEval);
        }

        // everything seems fine
        return result;
    }
}


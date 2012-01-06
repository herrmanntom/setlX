package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NonCatchableInSetlXException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlError;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

import java.util.List;

// eval(expr)              : evaluate a String of a SetlX expression, returns value of Error-type on parser or evaluation failure

public class PD_eval extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_eval();

    private PD_eval() {
        super("eval");
        addParameter("setlX_expr");
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value   exprArg = args.get(0);
        if ( ! (exprArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Expression-argument '" + exprArg + "' is not a string.");
        }
        // enable string interpretation ($-signs, escaped quotes etc)
        boolean interprete = Environment.isInterpreteStrings();
        Environment.setInterpreteStrings(true);

        // get expression string to be parsed
        String  exprStr = exprArg.toString();

        // reset string interpretation
        Environment.setInterpreteStrings(interprete);

        // strip out double quotes
        exprStr         = exprStr.substring(1, exprStr.length() - 1);

        try {
            // parse expr
            Expr expr = ParseSetlX.parseStringToExpr(exprStr);

            // eval and return result
            return expr.eval();
        } catch (NonCatchableInSetlXException ncisxe) {
            // rethrow these exceptions to 'ignore' them here
            throw ncisxe;
        } catch (SetlException se) {
            return new SetlError(se);
        }
    }
}


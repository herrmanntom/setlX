package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.ParseSetlX;

import java.util.List;

public class PD_eval extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_eval();

    private PD_eval() {
        super("eval");
        addParameter("setlX_expr");
        doNotChangeEnvironment();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value   exprArg = args.get(0);
        if ( ! (exprArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Expression-argument '" + exprArg + "' is not a string.");
        }
        String  exprStr = exprArg.toString();
        // strip out double quotes
        exprStr         = exprStr.substring(1, exprStr.length() - 1);
        // parse expr
        Expr exp = ParseSetlX.parseStringToExpr(exprStr);
        // eval and return result
        return exp.eval();
    }
}


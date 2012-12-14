package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;

import java.util.List;

// eval(expr)                    : evaluate a String of a SetlX expression

public class PD_eval extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_eval();

    private PD_eval() {
        super("eval");
        addParameter("setlX_expr");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value   exprArg = args.get(0);
        if ( ! (exprArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Expression-argument '" + exprArg + "' is not a string.");
        }
        // get expression string to be parsed
        String  exprStr = exprArg.getUnquotedString();

        ParseSetlX.resetErrorCount();
        Expr expr = ParseSetlX.parseStringToExpr(exprStr);

        // eval and return result
        return expr.eval(state);
    }
}


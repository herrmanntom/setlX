package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlError;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.ParseSetlX;

import java.util.List;

// parse(expr)             : parse SetlX expression into a term
public class PD_parse extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_parse();

    private PD_parse() {
        super("parse");
        addParameter("setlX_expr");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value   exprArg = args.get(0);
        if ( ! (exprArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Expression-argument '" + exprArg + "' is not a string.");
        }
        // get expression string to be parsed
        String  exprStr = exprArg.getUnquotedString();

        // parse expr
        ParseSetlX.resetErrorCount();
        Expr expr = ParseSetlX.parseStringToExpr(exprStr);

        // return term of result
        return expr.toTerm();
    }
}


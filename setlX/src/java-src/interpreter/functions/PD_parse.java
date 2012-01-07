package interpreter.functions;

import interpreter.exceptions.CatchableInSetlXException;
import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlError;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

import java.util.List;

// parse(expr)             : parse SetlX expression into a term, returns value of Error-type on parser failure

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

            // return term of result
            return expr.toTerm();
        } catch (CatchableInSetlXException cisxe) {
            return new SetlError(cisxe);
        }
    }
}


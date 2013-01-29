package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;

import java.util.List;

// parse(expr)             : parse SetlX expression into a term
public class PD_parse extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_parse();

    private PD_parse() {
        super("parse");
        addParameter("setlX_expr");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value   exprArg = args.get(0);
        if ( ! (exprArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Expression-argument '" + exprArg + "' is not a string.");
        }
        // get expression string to be parsed
        final String  exprStr = exprArg.getUnquotedString();

        // parse expression
        state.resetParserErrorCount();
        final Expr expr = ParseSetlX.parseStringToExpr(state, exprStr);

        // return term of result
        return expr.toTerm(state);
    }
}


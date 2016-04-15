package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * eval(setlX_expression) : evaluate a String of a SetlX expression
 */
public class PD_eval extends PreDefinedProcedure {

    private final static ParameterDefinition SETLX_EXPRESSION = createParameter("setlX_expression");

    /** Definition of the PreDefinedProcedure `eval'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_eval();

    private PD_eval() {
        super();
        addParameter(SETLX_EXPRESSION);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value exprArg = args.get(SETLX_EXPRESSION);
        if ( ! (exprArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Expression-argument '" + exprArg.toString(state) + "' is not a string.");
        }
        // get expression string to be parsed
        final String exprStr = exprArg.getUnquotedString(state);

        state.resetParserErrorCount();
        final OperatorExpression expr = ParseSetlX.parseStringToExpr(state, exprStr);

        // eval and return result
        return expr.evaluate(state);
    }
}


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
 * parse(setlX_expression) : Parse SetlX expression into a term.
 */
public class PD_parse extends PreDefinedProcedure {

    private final static ParameterDefinition SETLX_EXPRESSION = createParameter("setlX_expression");

    /** Definition of the PreDefinedProcedure `parse'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_parse();

    private PD_parse() {
        super();
        addParameter(SETLX_EXPRESSION);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value   exprArg = args.get(SETLX_EXPRESSION);
        if ( ! (exprArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Expression-argument '" + exprArg.toString(state) + "' is not a string.");
        }
        // get expression string to be parsed
        final String  exprStr = exprArg.getUnquotedString(state);

        // parse expression
        state.resetParserErrorCount();
        final OperatorExpression expr = ParseSetlX.parseStringToExpr(state, exprStr);

        // return term of result
        return expr.toTerm(state);
    }
}


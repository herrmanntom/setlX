package org.randoom.setlx.parameters;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

/**
 * This class represents a list parameter of a function definition.
 */
public class ListParameter extends ParameterDefinition {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(ListParameter.class);

    /**
     * Create a new parameter definition.
     *
     * @param var         Variable to bind to.
     * @param defaultExpr Expression to compute default value.
     */
    public ListParameter(final String var, final OperatorExpression defaultExpr) {
        super(var, defaultExpr);
    }

    /**
     * Create a new parameter definition.
     *
     * @param var  Variable to bind to.
     */
    public ListParameter(final String var) {
        super(var);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("*");
        super.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        return createTerm(state, FUNCTIONAL_CHARACTER);
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(ListParameter.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


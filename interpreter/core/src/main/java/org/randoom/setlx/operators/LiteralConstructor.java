package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Wrapper Expression for SetlX Literals, which parses escape sequences at runtime.
 */
public class LiteralConstructor extends AZeroOperator {
    private static final String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(LiteralConstructor.class);

    private final String     originalLiteral;
    private final SetlString runtimeString;

    /**
     * Constructor, which parses escape sequences in the literal to create.
     *
     * @param originalLiteral String read by the parser.
     */
    public LiteralConstructor(final String originalLiteral) {
        this(originalLiteral, SetlString.parseLiteral(originalLiteral));
    }

    private LiteralConstructor(final String originalLiteral, final SetlString runtimeString) {
        this.originalLiteral = originalLiteral;
        this.runtimeString   = runtimeString;
    }

    @Override
    public SetlString evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth)  {
        return runtimeString;
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        return true;
    }

    /* string operations */

    @Override
    public void appendOperatorSign(final State state, final StringBuilder sb, List<String> expressions) {
        sb.append(originalLiteral);
    }

    /* term operations */

    @Override
    public Term modifyTerm(final State state, Term term, ArrayDeque<Value> termFragments) {
        term.addMember(state, runtimeString);
        return term;
    }

    /**
     * Append the operator represented by a term to the supplied operator stack.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @param operatorStack            Operator to append to.
     * @throws TermConversionException If term is malformed.
     */
    public static void appendToOperatorStack(final State state, final Term term, FragmentList<AOperator> operatorStack) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlString)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlString runtimeString   = (SetlString) term.firstMember();
            final String     originalLiteral = "'" + runtimeString.getEscapedLiteral() + "'";
            operatorStack.add(new LiteralConstructor(originalLiteral, runtimeString));
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == LiteralConstructor.class) {
            return originalLiteral.compareTo(((LiteralConstructor) other).originalLiteral);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(LiteralConstructor.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == LiteralConstructor.class) {
            return originalLiteral.equals(((LiteralConstructor) obj).originalLiteral);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + originalLiteral.hashCode();
    }
}


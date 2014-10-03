package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Wrapper Expression for SetlX Literals, which parses escape sequences at runtime.
 */
public class LiteralConstructor extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(LiteralConstructor.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

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
    public SetlString eval(final State state) {
        return runtimeString;
    }

    @Override
    protected SetlString evaluate(final State state) {
        return runtimeString;
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) { /* nothing to collect */ }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(originalLiteral);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result  = new Term(FUNCTIONAL_CHARACTER, 1);

        result.addMember(state, runtimeString);

        return result;
    }

    /**
     * Convert a term representing a LiteralConstructor into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting LiteralConstructor Expression.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static LiteralConstructor termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlString)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlString runtimeString   = (SetlString) term.firstMember();
            final String     originalLiteral = "'" + runtimeString.getEscapedLiteral() + "'";
            return new LiteralConstructor(originalLiteral, runtimeString);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final Expr other) {
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
    public int hashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + originalLiteral.hashCode();
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


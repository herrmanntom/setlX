package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Implementation of the power operator (**).
 *
 * grammar rule:
 * power
 *     : factor ('**' prefixOperation)?
 *     ;
 *
 * implemented here as:
 *       ======       ===============
 *        lhs            exponent
 */
public class Power extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Power.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 2000;

    private final Expr lhs;
    private final Expr exponent;

    public Power(final Expr lhs, final Expr exponent) {
        this.lhs      = lhs;
        this.exponent = exponent;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return lhs.eval(state).power(state, exponent.eval(state));
    }

    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        lhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        exponent.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendString(state, sb, tabs);
        sb.append(" ** ");
        exponent.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, lhs.toTerm(state));
        result.addMember(state, exponent.toTerm(state));
        return result;
    }

    public static Power termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs        = TermConverter.valueToExpr(PRECEDENCE, true , term.firstMember());
            final Expr exponent   = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
            return new Power(lhs, exponent);
        }
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }

    public static String functionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


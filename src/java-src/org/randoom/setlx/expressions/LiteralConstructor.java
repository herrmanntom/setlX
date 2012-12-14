package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;

import java.util.List;

public class LiteralConstructor extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^literalConstructor";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

    private String     mOriginalLiteral;
    private SetlString mRuntimeString;

    public LiteralConstructor(final String originalLiteral) {
        this(originalLiteral, SetlString.newLiteral(originalLiteral));
    }

    private LiteralConstructor(final String originalLiteral, final SetlString runtimeString) {
        mOriginalLiteral = originalLiteral;
        mRuntimeString   = runtimeString;
    }

    public SetlString eval(final State state) {
        return mRuntimeString;
    }

    protected SetlString evaluate(final State state) {
        return mRuntimeString;
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) { /* nothing to collect */ }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append(mOriginalLiteral);
    }

    /* term operations */

    public Term toTerm(final State state) {
        final Term result  = new Term(FUNCTIONAL_CHARACTER, 1);

        result.addMember(mRuntimeString);

        return result;
    }

    public static LiteralConstructor termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlString)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlString runtimeString   = (SetlString) term.firstMember();
            final String     originalLiteral = "'" + runtimeString.getUnquotedString() + "'";
            return new LiteralConstructor(originalLiteral, runtimeString);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


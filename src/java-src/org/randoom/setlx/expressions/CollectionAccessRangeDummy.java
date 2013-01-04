package org.randoom.setlx.expressions;

import org.randoom.setlx.types.RangeDummy;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;

import java.util.List;

/*
grammar rule:
collectionAccessParams
    : (expr '..')=> expr '..' expr?
    | [...]
    ;

this class implements a range token inside the parameters of a CollectionAccess:
                         ====
*/

public class CollectionAccessRangeDummy extends Expr {
    // functional character used in terms (MUST be classname starting with lower case letter!)
    private final static String                     FUNCTIONAL_CHARACTER = "^collectionAccessRangeDummy";
    // precedence level in SetlX-grammar
    private final static int                        PRECEDENCE           = 9999;

    public  final static CollectionAccessRangeDummy CARD                 = new CollectionAccessRangeDummy();

    private CollectionAccessRangeDummy() {}

    @Override
    protected RangeDummy evaluate(final State state) {
        return RangeDummy.RD;
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) { /* nothing to collect */ }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(" .. ");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    public static CollectionAccessRangeDummy termToExpr(final Term term) {
        return CARD;
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


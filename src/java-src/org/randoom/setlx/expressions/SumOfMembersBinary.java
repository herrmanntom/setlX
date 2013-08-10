package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Class implementing the binary +/ operator.
 *
 * grammar rule:
 * reduce
 *     : prefixOperation ('+/' prefixOperation | [..])*
 *     | [...]
 *     ;
 *
 * implemented here as:
 *       ===============       ===============
 *           neutral             collection
 */
public class SumOfMembersBinary extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(SumOfMembersBinary.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1800;

    private final Expr neutral;
    private final Expr collection;

    public SumOfMembersBinary(final Expr neutral, final Expr collection) {
        this.neutral    = neutral;
        this.collection = collection;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return collection.eval(state).sumOfMembers(state, neutral.eval(state));
    }

    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        collection.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        Value value = null;
        if (collection.isReplaceable()) {
            try {
                value = collection.eval(new State());
            } catch (final Throwable t) {
                value = null;
            }
        }
        if (value == null || ! (value instanceof CollectionValue) || ((CollectionValue) value).size() == 0) {
            neutral.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        neutral.appendBracketedExpr(state, sb, tabs, PRECEDENCE, false);
        sb.append(" +/ ");
        collection.appendBracketedExpr(state, sb, tabs, PRECEDENCE, false);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, neutral.toTerm(state));
        result.addMember(state, collection.toTerm(state));
        return result;
    }

    public static SumOfMembersBinary termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr neutral    = TermConverter.valueToExpr(term.firstMember());
            final Expr collection = TermConverter.valueToExpr(term.lastMember());
            return new SumOfMembersBinary(neutral, collection);
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


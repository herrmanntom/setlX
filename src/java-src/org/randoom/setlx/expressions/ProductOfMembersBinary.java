package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * This class implements the expression representing the binary version of the
 * ProductOfMembers operator.
 */
/// grammar rule:
/// reduce
///     : factor ([..] | '*/' factor)*
///     ;
///
/// implemented here as:
///       ======              ======
///      mNeutral           mCollection
///
public class ProductOfMembersBinary extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(ProductOfMembersBinary.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1800;

    private final Expr neutral;
    private final Expr collection;

    public ProductOfMembersBinary(final Expr neutral, final Expr collection) {
        this.neutral    = neutral;
        this.collection = collection;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return collection.eval(state).productOfMembers(state, neutral.eval(state));
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        collection.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        Value value = null;
        if (collection.isReplaceable()) {
            try {
                value = collection.eval(state);
            } catch (final Throwable t) {
                value = null;
            }
        }
        if (value == null || ! (value instanceof CollectionValue) || ((CollectionValue) value).size() == 0) {
            neutral.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        neutral.appendBracketedExpr(state, sb, tabs, PRECEDENCE, false);
        sb.append(" */ ");
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

    /**
     * Convert a term representing a ProductOfMembersBinary expression into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static ProductOfMembersBinary termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr neutral    = TermConverter.valueToExpr(state, term.firstMember());
            final Expr collection = TermConverter.valueToExpr(state, term.lastMember());
            return new ProductOfMembersBinary(neutral, collection);
        }
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    public static String functionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


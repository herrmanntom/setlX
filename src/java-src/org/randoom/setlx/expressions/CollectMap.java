package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Expression that collects specific members of a collection value.
 *
 * grammar rule:
 * call
 *     : variable ('(' callParameters ')')? ('[' collectionAccessParams ']' | '{' anyExpr '}')*
 *     ;
 *
 * implemented here as:
 *       ==================================                                       =======
 *                    lhs                                                           arg
 */
public class CollectMap extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(CollectMap.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private final Expr lhs;      // left hand side (Variable, other CollectMap, CollectionAccess, etc)
    private final Expr arg;      // argument

    /**
     * Create a new CollectMap expression.
     *
     * @param lhs Left hand side to evaluate before collecting on its result.
     * @param arg Expression to evaluate as argument to collect.
     */
    public CollectMap(final Expr lhs, final Expr arg) {
        this.lhs = lhs;
        this.arg = arg;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        final Value lhs = this.lhs.eval(state);
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                "Left hand side \"" + this.lhs + "\" is undefined."
            );
        }
        return lhs.collectMap(state, arg.eval(state).clone());
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        lhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        arg.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendString(state, sb, tabs);
        sb.append("{");
        arg.appendString(state, sb, tabs);
        sb.append("}");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, lhs.toTerm(state));
        result.addMember(state, arg.toTerm(state));
        return result;
    }

    @Override
    public Term toTermQuoted(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, lhs.toTermQuoted(state));
        result.addMember(state, arg.eval(state).toTerm(state));
        return result;
    }

    /**
     * Convert a term representing a CollectMap expression into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting CollectMap expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static CollectMap termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(state, term.firstMember());
            final Expr arg = TermConverter.valueToExpr(state, term.lastMember());
            return new CollectMap(lhs, arg);
        }
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


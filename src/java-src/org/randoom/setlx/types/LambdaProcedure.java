package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.Return;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a lambda procedure.
 *
 * grammar rule:
 * lambdaProcedure
 *     : lambdaParameters    '|->' sum
 *     ;
 *
 * implemented here as:
 *       ----------------          ===
 *    parameters (inherited)       expr
 */
public class LambdaProcedure extends Procedure {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(LambdaProcedure.class);

    private final Expr expr; // expression in the body of the definition; used directly only for toString() and toTerm()

    /**
     * Create new lambda definition.
     *
     * @param state      Current state of the running setlX program.
     * @param parameters List of parameters.
     * @param expr       lambda-expression.
     */
    public LambdaProcedure(final State state, final List<ParameterDef> parameters, final Expr expr) {
        super(parameters, new Block(state, 1));
        this.expr = expr;
        statements.add(new Return(expr));
    }
    private LambdaProcedure(
        final List<ParameterDef> parameters,
        final Block              statements,
        final Expr               expr
    ) {
        super(parameters, statements);
        this.expr = expr;
    }

    @Override
    public LambdaProcedure clone() {
        if (object != null) {
            return new LambdaProcedure(parameters, statements, expr);
        } else {
            return this;
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        object = null;
        if (parameters.size() == 1) {
            parameters.get(0).appendString(state, sb, 0);
        } else {
            sb.append("[");
            final Iterator<ParameterDef> iter = parameters.iterator();
            while (iter.hasNext()) {
                iter.next().appendString(state, sb, 0);
                if (iter.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }
        sb.append(" |-> ");
        expr.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        object = null;
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList paramList = new SetlList(parameters.size());
        for (final ParameterDef param: parameters) {
            paramList.addMember(state, param.toTerm(state));
        }
        result.addMember(state, paramList);

        result.addMember(state, expr.toTerm(state));

        return result;
    }

    /**
     * Convert a term representing a LambdaProcedure into such a procedure.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting LambdaProcedure.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static LambdaProcedure termToValue(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || term.firstMember().getClass() != SetlList.class) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList           paramList  = (SetlList) term.firstMember();
            final List<ParameterDef> parameters = new ArrayList<ParameterDef>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(state, v));
            }
            final Expr               expr       = TermConverter.valueToExpr(state, term.lastMember());
            return new LambdaProcedure(state, parameters, expr);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final Value other) {
        object = null;
        if (this == other) {
            return 0;
        } else if (other.getClass() == LambdaProcedure.class) {
            final LambdaProcedure lambdaProcedure = (LambdaProcedure) other;
            int cmp = Integer.valueOf(parameters.size()).compareTo(lambdaProcedure.parameters.size());
            if (cmp != 0) {
                return cmp;
            }
            for (int index = 0; index < parameters.size(); ++index) {
                cmp = parameters.get(index).compareTo(lambdaProcedure.parameters.get(index));
                if (cmp != 0) {
                    return cmp;
                }
            }
            if (expr == lambdaProcedure.expr) {
                return 0;
            }
            // TODO do without toString
            return expr.toString().compareTo(lambdaProcedure.expr.toString());
        } else {
            return this.compareToOrdering() - other.compareToOrdering();
        }
    }

    @Override
    public int compareToOrdering() {
        object = null;
        return COMPARE_TO_ORDERING_PROCEDURE_LAMBDA;
    }

    @Override
    public boolean equalTo(final Object other) {
        object = null;
        if (this == other) {
            return true;
        } else if (other.getClass() == LambdaProcedure.class) {
            final LambdaProcedure lambdaProcedure = (LambdaProcedure) other;
            if (parameters.size() == lambdaProcedure.parameters.size()) {
                for (int index = 0; index < parameters.size(); ++index) {
                    if ( ! parameters.get(index).equalTo(lambdaProcedure.parameters.get(index))) {
                        return false;
                    }
                }
                if (expr == lambdaProcedure.expr) {
                    return true;
                }
                // TODO do without toString
                return expr.toString().equals(lambdaProcedure.expr.toString());
            }
        }
        return false;
    }

    private final static int initHashCode = LambdaProcedure.class.hashCode();

    @Override
    public int hashCode() {
        object = null;
        return initHashCode + parameters.size();
    }

    /**
     * Get the functional character of this value type used in terms.
     *
     * @return Functional character of this value type.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.List;

/**
 * Implementation of the return statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'return' anyExpr? ';'
 *     ;
 *
 * implemented here as:
 *                =======
 *                result
 */
public class Return extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Return.class);

    private final OperatorExpression result;

    /**
     * Create a new return statement.
     *
     * @param result Expression to evaluate before returning.
     */
    public Return(final OperatorExpression result) {
        this.result = result;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        if (result != null) {
            return ReturnMessage.createMessage(result.evaluate(state));
        } else {
            return ReturnMessage.OM;
        }
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        return result == null || result.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("return");
        if (result != null){
            sb.append(" ");
            result.appendString(state, sb, 0);
        }
        sb.append(";");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        if (this.result != null) {
            result.addMember(state, this.result.toTerm(state));
        } else {
            result.addMember(state, SetlString.NIL);
        }
        return result;
    }

    /**
     * Convert a term representing an return statement into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting return statement.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Return termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            OperatorExpression expr = null;
            if (! term.firstMember().equals(SetlString.NIL)) {
                expr = OperatorExpression.createFromTerm(state, term.firstMember());
            }
            return new Return(expr);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Return.class) {
            final OperatorExpression otherResult = ((Return) other).result;
            if (result != null) {
                if (otherResult != null) {
                    return result.compareTo(otherResult);
                } else {
                    return 1;
                }
            } else if (otherResult != null) {
                return -1;
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Return.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == Return.class) {
            final OperatorExpression otherResult = ((Return) obj).result;
            if (result != null && otherResult != null) {
                return result.equals(otherResult);
            } else if (result == null && otherResult == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = (int) COMPARE_TO_ORDER_CONSTANT;
        if (result != null) {
            hash += result.hashCode();
        }
        return hash;
    }
}


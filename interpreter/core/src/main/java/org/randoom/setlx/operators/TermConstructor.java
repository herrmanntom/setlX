package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.Iterator;
import java.util.List;

/**
 * A operator that puts a Term on the stack.
 */
public class TermConstructor extends AZeroOperator {

    private final String fChar;                          // functional character of the term
    private final FragmentList<OperatorExpression> args; // list of arguments

    /**
     * Constructor.
     *
     * @param fChar Functional character of the term.
     * @param args  List of arguments.
     */
    public TermConstructor(final String fChar, final FragmentList<OperatorExpression> args) {
        this.fChar = fChar;
        this.args = unify(args);
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        boolean allowOptimization = true;
        for (final OperatorExpression arg: args) {
            allowOptimization = arg.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && allowOptimization;
        }
        return allowOptimization;
    }

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        final Term result = new Term(fChar, args.size());

        for (final OperatorExpression arg: args) {
            result.addMember(state, arg.evaluate(state).toTerm(state)); // evaluate arguments at runtime
        }

        return result;
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(fChar);
        sb.append("(");

        final Iterator<OperatorExpression> iter = args.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append(")");
    }

    @Override
    public Value modifyTerm(State state, Term term) throws SetlException {
        final Term result = new Term(fChar, args.size());

        for (final OperatorExpression arg: args) {
            result.addMember(state, arg.toTerm(state)); // do not evaluate here
        }

        return result;
    }

    /**
     * Convert a term representing a TermConstructor expression into such an expression.
     *
     * @param state Current state of the running setlX program.
     * @param term  Term to convert.
     * @return      Resulting expression of this conversion.
     */
    public static TermConstructor termToExpr(final State state, final Term term) {
        final String functionalCharacter = term.getFunctionalCharacter();
        final FragmentList<OperatorExpression> args = new FragmentList<OperatorExpression>(term.size());
        for (final Value v : term) {
            args.add(TermConverter.valueToExpr(state, v));
        }
        return new TermConstructor(functionalCharacter, args);
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(TermConstructor.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == TermConstructor.class) {
            TermConstructor otr = (TermConstructor) other;
            if (fChar == otr.fChar && args == otr.args) {
                return 0; // clone
            }
            int cmp = fChar.compareTo(otr.fChar);
            if (cmp != 0) {
                return cmp;
            }
            return args.compareTo(otr.args);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == TermConstructor.class) {
            TermConstructor other = (TermConstructor) obj;
            if (fChar == other.fChar && args == other.args) {
                return true; // clone
            } else if (fChar.equals(other.fChar)) {
                return args.equals(other.args);
            }
            return false;
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + fChar.hashCode();
        return hash * 31 + args.hashCode();
    }
}

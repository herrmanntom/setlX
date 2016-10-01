package org.randoom.setlx.operators;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.assignments.AssignableVariable;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Operator that gets a variable from the current scope and puts it on the stack.
 */
public class Variable extends AZeroOperator {
    private final static String FUNCTIONAL_CHARACTER          = TermUtilities.generateFunctionalCharacter(Variable.class);

    private final String id;

    /**
     * Create a new Variable expression.
     *
     * @param id ID/name of this variable.
     */
    public Variable(final String id) {
        this.id = id;
    }

    @Override
    public AAssignableExpression convertToAssignableExpression(AAssignableExpression assignable) throws UndefinedOperationException {
        if (assignable == null) {
            return new AssignableVariable(id);
        } else {
            throw new UndefinedOperationException("Expression cannot be converted");
        }
    }

    /**
     * @return ID/name of this variable.
     */
    public String getId() {
        return id;
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        if (boundVariables.contains(id)) {
            usedVariables.add(id);
        } else {
            unboundVariables.add(id);
        }
        return false;
    }

    @Override
    public Value evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        return state.findValue(id);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb, List<String> expressions) {
        sb.append(id);
    }

    @Override
    public Value modifyTerm(State state, Term term, ArrayDeque<Value> termFragments) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(state, new SetlString(id));
        return result;
    }

    /**
     * Append the operator represented by a term to the supplied operator stack.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @param operatorStack            Operator to append to.
     * @throws TermConversionException If term is malformed.
     */
    public static void appendToOperatorStack(final State state, final Term term, FragmentList<AOperator> operatorStack) throws TermConversionException {
        if (term.size() == 1 && term.firstMember().getClass() == SetlString.class) {
            final String id = term.firstMember().getUnquotedString(state);
            operatorStack.add(new Variable(id));
        } else {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Variable.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Variable.class) {
            final Variable otr = (Variable) other;
            return id.compareTo(otr.id);
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
        return this == obj || obj.getClass() == Variable.class && id.equals(((Variable) obj).id);
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + id.hashCode();
    }

    /**
     * @return functional character internally used in terms.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}

package org.randoom.setlx.operators;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.assignments.AssignableVariable;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.List;

/**
 * Operator that gets a variable from the current scope and puts it on the stack.
 */
public class Variable extends AZeroOperator {
    // This functional character is used internally
    private final static String FUNCTIONAL_CHARACTER          = TermUtilities.getPrefixOfInternalFunctionalCharacters() + "Variable";
    // this one is used externally (e.g. during toString)
    private final static String FUNCTIONAL_CHARACTER_EXTERNAL = TermUtilities.generateFunctionalCharacter(Variable.class);
    /* both are equal during matching and compare. However while terms with the
     * internal one always bind anything, terms with the external one only match
     * and do not bind.
     *
     * This is done to create a difference between the cases used in
     *      match(term) {
     *          case 'variable(x): foo2(); // matches only variables
     *          case x           : foo1(); // `x'.toTerm() results in 'Variable("x"); matches everything and binds it to x
     *      }
     */

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
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        return state.findValue(id);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(id);
    }

    @Override
    public Value modifyTerm(State state, Term term) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(state, new SetlString(id));
        return result;
    }

    @Override
    public Value buildQuotedTerm(State state, Stack<Value> termFragments) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER_EXTERNAL, 1);
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
        } else if (term.size() == 1 && term.firstMember().getClass() == Term.class && ((Term) term.firstMember()).getFunctionalCharacter().equals(FUNCTIONAL_CHARACTER_EXTERNAL)) {
            TermConstructor.appendToOperatorStack(state, term, operatorStack);
        } else {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER_EXTERNAL);
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
     * Get the functional character internally used in terms.
     *
     * @return functional character internally used in terms.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Get the functional character used externally (e.g. during toString).
     *
     * @return functional character used externally (e.g. during toString).
     */
    public static String getFunctionalCharacterExternal() {
        return FUNCTIONAL_CHARACTER_EXTERNAL;
    }
}

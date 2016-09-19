package org.randoom.setlx.types;

import org.randoom.setlx.assignments.AssignableVariable;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.parameters.ParameterList;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.SetlHashMap;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;
import org.randoom.setlx.utilities.VariableScope;
import org.randoom.setlx.utilities.WriteBackAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a function definition, where closures are explicitly enabled.
 *
 * grammar rule:
 * procedure
 *     : 'closure' '(' procedureParameters ')' '{' block '}'
 *     ;
 *
 * implemented here as:
 *                     ===================         =====
 *                          parameters           statements
 */
public class Closure extends Procedure {
    // functional character used in terms
    private   final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Closure.class);

    /**
     * Variables and values used in closure.
     */
    protected       SetlHashMap<Value> closure;

    /**
     * Create new closure definition.
     *
     * @param parameters List of parameters.
     * @param statements Statements in the body of the procedure.
     */
    public Closure(final ParameterList parameters, final Block statements) {
        this(parameters, statements, null);
    }

    /**
     * Create new procedure definition, which replicates the complete internal
     * state of another procedure.
     *
     * @param parameters procedure parameters
     * @param statements statements in the body of the procedure
     * @param closure    Attached closure variables.
     */
    protected Closure(final ParameterList parameters, final Block statements, final SetlHashMap<Value> closure) {
        super(parameters, statements);
        if (closure != null) {
            this.closure = new SetlHashMap<Value>(closure);
        } else {
            this.closure = null;
        }
    }

    /**
     * Create a separate instance of this procedure.
     *
     * Note: Only to be used by ProcedureConstructor.
     *
     * @return Copy of this procedure definition.
     */
    public Closure createCopy() {
        return new Closure(parameters, statements);
    }

    @Override
    public Closure clone() {
        if (closure != null || object != null) {
            return new Closure(parameters, statements, closure);
        } else {
            return this;
        }
    }

    /**
     * Attach closure variables and their values.
     *
     * @param closure Closure variables to attach.
     */
    public void setClosure(final SetlHashMap<Value> closure) {
        this.closure = closure;
    }

    @Override
    public boolean collectVariablesAndOptimize (
            final State        state,
            final List<String> boundVariables,
            final List<String> unboundVariables,
            final List<String> usedVariables
    ) {
        /* first collect and optimize the inside */
        final List<String> innerBoundVariables   = new ArrayList<String>();
        final List<String> innerUnboundVariables = new ArrayList<String>();
        final List<String> innerUsedVariables    = new ArrayList<String>();

        // add all parameters to bound
        parameters.collectVariablesAndOptimize(state, innerBoundVariables, innerBoundVariables, innerBoundVariables);

        statements.collectVariablesAndOptimize(state, innerBoundVariables, innerUnboundVariables, innerUsedVariables);

        /* compute variables as seen by the outside */

        // upon defining this procedure, all variables which are unbound inside
        // will be read to create the closure for this procedure
        for (final String var : innerUnboundVariables) {
            if (boundVariables.contains(var)) {
                usedVariables.add(var);
            } else {
                unboundVariables.add(var);
            }
        }
        return false;
    }

    @Override
    protected Value callAfterEval(final State state, final FragmentList<OperatorExpression> args, final List<Value> values, final SetlObject object) throws SetlException {
        // save old scope
        final VariableScope oldScope = state.getScope();
        // create new scope used for the function call
        final VariableScope newScope = oldScope.createFunctionsOnlyLinkedScope();
        state.setScope(newScope);

        // link members of surrounding object
        if (object != null) {
            newScope.linkToThisObject(object);
        }

        // assign closure contents
        if (closure != null) {
            for (final Map.Entry<String, Value> entry : closure.entrySet()) {
                final Value value = entry.getValue();
                new AssignableVariable(entry.getKey()).assignUnclonedCheckUpTo(state, value, oldScope, true, FUNCTIONAL_CHARACTER);
            }
        }

        // put arguments into inner scope
        final boolean rwParameters = parameters.putParameterValuesIntoScope(state, values, FUNCTIONAL_CHARACTER);

        // get rid of value-list to potentially free some memory
        values.clear();

        // results of call to procedure
        ReturnMessage  result = null;
        WriteBackAgent wba    = null;

        try {

            // execute, e.g. perform actual procedure call
            result = statements.execute(state);

            // extract 'rw' arguments from environment and store them into WriteBackAgent
            if (rwParameters) {
                wba = parameters.extractRwParametersFromScope(state, args);
            }

            // read closure variables and update their current state
            if (closure != null) {
                for (final Map.Entry<String, Value> entry : closure.entrySet()) {
                    entry.setValue(state.findValue(entry.getKey()));
                }
            }

        } finally { // make sure scope is always reset
            // restore old scope
            state.setScope(oldScope);

            // write values in WriteBackAgent into restored scope
            if (wba != null) {
                wba.writeBack(state, FUNCTIONAL_CHARACTER);
            }
        }

        if (result != null) {
            return result.getPayload();
        } else {
            return Om.OM;
        }
    }

    /* string and char operations */

    @Override
    protected void appendStringWithoutStatements(final State state, final StringBuilder sb) {
        object = null;
        sb.append("closure(");
        parameters.appendString(state, sb);
        sb.append(")");
    }

    @Override
    protected void appendBeforeStatements(State state, StringBuilder sb, int tabs) {
        if (closure != null && closure.size() > 0) {
            sb.append("/* ");
            closure.appendString(state, sb, tabs);
            sb.append("; */ ");
        }
    }
/* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        object = null;
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, parameters.toTerm(state));

        result.addMember(state, statements.toTerm(state));

        return result;
    }

    /**
     * Convert a term representing a Closure into such a procedure.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting Closure.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Closure termToValue(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || term.firstMember().getClass() != SetlList.class) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList      paramList  = (SetlList) term.firstMember();
            final ParameterList parameters = new ParameterList(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDefinition.valueToParameterDef(state, v));
            }
            final Block              block      = TermUtilities.valueToBlock(state, term.lastMember());
            return new Closure(parameters, block);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        object = null;
        if (this == other) {
            return 0;
        } else if (other.getClass() == Closure.class) {
            final Closure otherClosure = (Closure) other;
            int cmp = parameters.compareTo(otherClosure.parameters);
            if (cmp != 0) {
                return cmp;
            }
            return statements.compareTo(otherClosure.statements);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Closure.class);

    @Override
    public long compareToOrdering() {
        object = null;
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        object = null;
        if (this == other) {
            return true;
        } else if (other.getClass() == Closure.class) {
            final Closure otherClosure = (Closure) other;
            if (parameters.equals(otherClosure.parameters)) {
                return statements.equalTo(otherClosure.statements);
            }
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return (((int) COMPARE_TO_ORDER_CONSTANT) + parameters.hashCode()) * 31 + statements.size();
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.parameters.ParameterList;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a function definition.
 *
 * grammar rule:
 * procedure
 *     : 'procedure' '(' procedureParameters ')' '{' block '}'
 *     ;
 *
 * implemented here as:
 *                       ===================         =====
 *                            parameters           statements
 */
public class Procedure extends ImmutableValue {
    // functional character used in terms
    private   final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Procedure.class);

    /**
     * List of parameters.
     */
    protected final ParameterList parameters;
    /**
     * Statements in the body of the procedure.
     */
    protected final Block         statements;
    /**
     * Surrounding object for next call.
     */
    protected       SetlObject    object;

    /**
     * Create new procedure definition.
     *
     * @param parameters procedure parameters
     * @param statements statements in the body of the procedure
     */
    public Procedure(final ParameterList parameters, final Block statements) {
        this.parameters = parameters;
        this.statements = statements;
        this.object     = null;
    }

    @Override
    public Procedure clone() {
        if (object != null) {
            return new Procedure(parameters, statements);
        } else {
            return this;
        }
    }

    /**
     * Attach surrounding object, when this procedure is part of an object/class
     * definition.
     *
     * @param object Object to attach.
     */
    public void addSurroundingObject(final SetlObject object) {
        this.object = object;
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        /* collect and optimize the inside */
        final List<String> innerBoundVariables   = new ArrayList<>();
        final List<String> innerUnboundVariables = new ArrayList<>();
        final List<String> innerUsedVariables    = new ArrayList<>();

        // add all parameters to bound
        parameters.collectVariablesAndOptimize(state, innerBoundVariables, innerBoundVariables, innerBoundVariables);

        statements.collectVariablesAndOptimize(state, innerBoundVariables, innerUnboundVariables, innerUsedVariables);
        return false;
    }

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isProcedure() {
        object = null;
        return SetlBoolean.TRUE;
    }

    /* function call */

    @Override
    public Value call(final State state, List<Value> argumentValues, final FragmentList<OperatorExpression> arguments, final Value listValue, final OperatorExpression listArg) throws SetlException {
        final SetlObject object = this.object;
        this.object = null;

        SetlList listArguments = null;
        if (listValue != null) {
            if (listValue.getClass() != SetlList.class) {
                StringBuilder error = new StringBuilder();
                error.append("List argument '");
                listValue.appendString(state, error, 0);
                error.append("' is not a list.");
                throw new UndefinedOperationException(error.toString());
            }
            listArguments = (SetlList) listValue;
        }

        int nArguments = argumentValues.size();
        if (listArguments != null) {
            nArguments += listArguments.size();
        }

        if (! parameters.isAssignableWithThisManyActualArguments(nArguments)) {
            final StringBuilder error = new StringBuilder();
            error.append("'");
            appendStringWithoutStatements(state, error);
            error.append("'");
            parameters.appendIncorrectNumberOfParametersErrorMessage(error, nArguments);
            throw new IncorrectNumberOfParametersException(error.toString());
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<>(nArguments);
        values.addAll(argumentValues);
        if (listArguments != null) {
            for (Value listArgument : listArguments) {
                values.add(listArgument);
            }
        }

        return callAfterEval(state, arguments, values, object);
    }

    /**
     * Perform the actual function call using the statement in this definition,
     * after all parameters where evaluated.
     *
     * @param state          Current state of the running setlX program.
     * @param args           Expressions used to set the procedures parameters.
     * @param values         Results of the evaluated parameters.
     * @param object         Surrounding object for this call.
     * @return               Return value of this function call.
     * @throws SetlException Thrown in case of some (user-) error.
     */
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

        // put arguments into inner scope
        final boolean rwParameters = parameters.putParameterValuesIntoScope(state, values, FUNCTIONAL_CHARACTER);

        // get rid of value-list to potentially free some memory
        values.clear();

        // results of call to procedure
        ReturnMessage   result = null;
        WriteBackAgent  wba    = null;

        try {

            // execute, e.g. perform actual procedure call
            result = statements.execute(state);

            // extract 'rw' arguments from environment and store them into WriteBackAgent
            if (rwParameters) {
                wba = parameters.extractRwParametersFromScope(state, args);
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
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        final String endl      = state.getEndl();
        appendStringWithoutStatements(state, sb);
        sb.append(" ");
        sb.append("{");
        sb.append(endl);
        appendBeforeStatements(state, sb, tabs + 1);
        statements.appendString(state, sb, tabs + 1, /* brackets = */ false);
        sb.append(endl);
        state.appendLineStart(sb, tabs);
        sb.append("}");
    }

    /**
     * Appends a string representation of this Procedure to the given
     * StringBuilder object, but does not append the statements inside the procedure.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString(State)
     *
     * @param state Current state of the running setlX program.
     * @param sb    StringBuilder to append to.
     */
    protected void appendStringWithoutStatements(final State state, final StringBuilder sb) {
        object = null;
        sb.append("procedure(");
        parameters.appendString(state, sb);
        sb.append(")");
    }


    /**
     * Appends something before printing the statements of this Procedure to the given
     * StringBuilder object.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString(State)
     *
     * @param state Current state of the running setlX program.
     * @param sb    StringBuilder to append to.
     * @param tabs  Number of tabs to use as indentation for statements.
     */
    protected void appendBeforeStatements(State state, StringBuilder sb, int tabs) {
        // append nothing by default
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
     * Convert a term representing a Procedure into such a procedure.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting Procedure.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Procedure termToValue(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final ParameterList parameters = ParameterList.termFragmentToParameterList(state, term.firstMember());
            final Block         block      = TermUtilities.valueToBlock(state, term.lastMember());
            return new Procedure(parameters, block);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        object = null;
        if (this == other) {
            return 0;
        } else if (other.getClass() == Procedure.class) {
            final Procedure otherProcedure = (Procedure) other;
            int cmp = parameters.compareTo(otherProcedure.parameters);
            if (cmp != 0) {
                return cmp;
            }
            return statements.compareTo(otherProcedure.statements);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Procedure.class);

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
        } else if (other.getClass() == Procedure.class) {
            final Procedure procedure = (Procedure) other;
            if (parameters.equals(procedure.parameters)) {
                return statements.equalTo(procedure.statements);
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


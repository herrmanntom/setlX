package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operators.ValueOperator;
import org.randoom.setlx.parameters.ListParameter;
import org.randoom.setlx.parameters.Parameter;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.parameters.ParameterList;
import org.randoom.setlx.parameters.ReadWriteParameter;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.*;
import org.randoom.setlx.utilities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for all procedures, which can be loaded at runtime by setlX.
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class PreDefinedProcedure extends Procedure {
    // functional characters used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(PreDefinedProcedure.class);

    private String name;

    /**
     * Initialize a new predefined procedure.
     *
     * Note: This class is abstract - no object will be created using this
     *       constructor directly.
     */
    protected PreDefinedProcedure() {
        super(new ParameterList(), new Block());
        this.name         = null;
    }

    /**
     * Get the name of this procedure, as shown in the interpreter.
     *
     * @return Name of this procedure.
     */
    public final String getName() {
        if (name == null) {
            name = this.getClass().getSimpleName().substring(3);
        }
        return name;
    }

    /**
     * Set the name of this procedure.
     *
     * Only to be used by MathFunction.java & MathFunction2.java !
     * Name of `normal' PreDefinedProcedures is determined automatically for the class name.
     *
     * @param name Name to set.
     */
    protected final void setName(final String name) {
        this.name = name;
    }

    /**
     * Create a parameter for this definition.
     *
     * @param param Parameter name.
     * @return new ParameterDef
     */
    protected static Parameter createParameter(final String param) {
        return new Parameter(param);
    }

    /**
     * Create a read-write parameter for this definition.
     *
     * @param param Parameter name.
     * @return new ParameterDef
     */
    protected static ReadWriteParameter createRwParameter(final String param) {
        return new ReadWriteParameter(param);
    }

    /**
     * Create an optional parameter for this definition.
     *
     * @param param        Parameter name.
     * @param defaultValue Value to use as default.
     * @return new ParameterDef
     */
    protected static Parameter createOptionalParameter(final String param, final Value defaultValue) {
        return new Parameter(param, new OperatorExpression(new ValueOperator(defaultValue)));
    }

    /**
     * Create a list-parameter for this definition.
     *
     * @param param Parameter name.
     * @return new ParameterDef
     */
    protected static ListParameter createListParameter(final String param) {
        return new ListParameter(param);
    }

    /**
     * Add parameters to this definition.
     *
     * @param parameter Parameter to add.
     */
    protected final void addParameter(final ParameterDefinition parameter) {
        parameters.add(parameter);
    }

    /**
     * Function to be implemented by specific predefined procedures.
     *
     * @param state          Current state of the running setlX program.
     * @param args           Values of the call-parameters.
     * @return               Resulting value of the call.
     * @throws SetlException Can be thrown in case of some (user-) error.
     */
    protected abstract Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException;

    // this function is called from within SetlX
    @Override
    public final Value call(final State state, List<Value> argumentValues, final FragmentList<OperatorExpression> arguments, final Value listValue, final OperatorExpression listArg) throws SetlException {
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
            error.append(getName());
            error.append("(");
            parameters.appendString(state, error);
            error.append(")'");
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

        // assign parameters
        HashMap<ParameterDefinition, Value> assignments = parameters.putParameterValuesIntoMap(state, values);

        // call predefined function (which may add writeBack-values to List)
        final Value result  = this.execute(state, assignments);

        // extract 'rw' arguments from writeBackVars list and store them into WriteBackAgent
        final WriteBackAgent wba = parameters.extractRwParametersFromMap(assignments, arguments);
        if (wba != null) {
            // assign variables
            wba.writeBack(state, FUNCTIONAL_CHARACTER);
        }

        return result;
    }

    /* string and char operations */

    @Override
    public final void appendString(final State state, final StringBuilder sb, final int tabs) {
        final String endl = state.getEndl();
        sb.append("procedure(");
        parameters.appendString(state, sb);
        sb.append(") {");
        sb.append(endl);
        state.appendLineStart(sb, tabs + 1);
        sb.append("/* predefined procedure `");
        sb.append(getName());
        sb.append("' */");
        sb.append(endl);
        state.appendLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    @Override
    public final Value toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER);

        result.addMember(state, new SetlString(getName()));

        return result;
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        object = null;
        if (this == other) {
            return 0;
        } else if (other instanceof PreDefinedProcedure) {
            return getName().compareTo(((PreDefinedProcedure) other).getName());
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(PreDefinedProcedure.class);

    @Override
    public long compareToOrdering() {
        object = null;
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof PreDefinedProcedure) {
            return getName().equals(((PreDefinedProcedure) other).getName());
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + getName().hashCode();
    }
}


package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A list of parameter definitions.
 */
public class ParameterList extends FragmentList<ParameterDef> {
    private int     rwParameters;
    private int     numberOfParametersWithOutDefault;
    private boolean isLastParameterOfTypeList;

    /**
     * Create a new Parameter list
     */
    public ParameterList() {
        this(4);
    }
    /**
     * Create a new Parameter list
     *
     * @param initialCapacity initial capacity of the list
     */
    public ParameterList(int initialCapacity) {
        super(initialCapacity);
        rwParameters                     = 0;
        numberOfParametersWithOutDefault = 0;
        isLastParameterOfTypeList        = false;
    }

    /**
     * Appends the specified ParameterDef to the end of this list.
     *
     * @param element Parameter definition to append.
     */
    @Override
    public void add(ParameterDef element) {
        isLastParameterOfTypeList = false;
        if (element.getType() == ParameterDef.ParameterType.READ_WRITE) {
            ++rwParameters;
        } else if (element.getType() == ParameterDef.ParameterType.LIST) {
            isLastParameterOfTypeList = true;
        }
        if (! element.hasDefaultValue()) {
            ++numberOfParametersWithOutDefault;
        }
        super.add(element);
    }

    /**
     * Check if this lists contains exactly one parameter.
     * @return True if this lists contains exactly one parameter.
     */
    public boolean hasSizeOfOne() {
        return fragmentList.size() == 1;
    }

    /**
     * Gather all bound and unbound variables in this fragment and its siblings.
     * Optimizes this fragment, if this can be safely done.
     *
     * @param state            Current state of the running setlX program.
     * @param boundVariables   Variables "assigned" in this fragment.
     * @param unboundVariables Variables not present in bound when used.
     * @param usedVariables    Variables present in bound when used.
     */
    public void collectVariablesAndOptimize (
            final State        state,
            final List<String> boundVariables,
            final List<String> unboundVariables,
            final List<String> usedVariables
    ) {
        for (final ParameterDef def : fragmentList) {
            def.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
    }

    /**
     * Check if this given number of arguments are sufficient to be assigned to this parameter list.
     *
     * @param numberOfArguments Number of arguments.
     * @return True if number of arguments is sufficient.
     */
    public boolean isAssignableWithThisManyActualArguments(int numberOfArguments) {
        return numberOfArguments >= getMinimumNumberOfParameters() && numberOfArguments <= getMaximumNumberOfParameters();
    }

    /**
     * Add error message, because number of arguments is incorrect.
     *
     * @param error                                 Message builder to append to.
     * @param numberOfArguments                     Number of arguments.
     * @throws IncorrectNumberOfParametersException if number of arguments is sufficient.
     */
    public void appendIncorrectNumberOfParametersErrorMessage(final StringBuilder error, int numberOfArguments) throws IncorrectNumberOfParametersException {
        int minimumNumberOfParameters = getMinimumNumberOfParameters();
        int maximumNumberOfParameters = getMaximumNumberOfParameters();
        if (minimumNumberOfParameters == maximumNumberOfParameters && minimumNumberOfParameters != numberOfArguments) {
            error.append(" is defined with ");
            error.append(minimumNumberOfParameters);
            error.append(" instead of ");
            error.append(numberOfArguments);
            error.append(" parameters.");
        } else if (numberOfArguments < minimumNumberOfParameters) {
            error.append(" is defined with at least ");
            error.append(minimumNumberOfParameters);
            error.append(" instead of ");
            error.append(numberOfArguments);
            error.append(" parameters.");
        } else if (numberOfArguments > maximumNumberOfParameters) {
            error.append(" is defined with at most ");
            error.append(minimumNumberOfParameters);
            error.append(" instead of ");
            error.append(numberOfArguments);
            error.append(" parameters.");
        }
    }

    /**
     * Get the minimum amount of actual arguments that can be assigned to this parameter list.
     *
     * @return Minimum amount of actual arguments.
     */
    public int getMinimumNumberOfParameters() {
        if (isLastParameterOfTypeList) {
            return numberOfParametersWithOutDefault -1;
        } else {
            return numberOfParametersWithOutDefault;
        }
    }

    /**
     * Get the maximum amount of actual arguments that can be assigned to this parameter list.
     *
     * @return Maximum amount of actual arguments.
     */
    public int getMaximumNumberOfParameters() {
        if (isLastParameterOfTypeList) {
            return Integer.MAX_VALUE;
        } else {
            return fragmentList.size();
        }
    }

    /**
     * Assign given list of values to these parameters.
     *
     * @param state             Current state of the running setlX program.
     * @param values            List of values to assign.
     * @param assignmentContext Context description of the assignment (for debugging)
     * @return                  True if READ_WRITE parameters are present.
     * @throws SetlException    Thrown in case of some (user-) error.
     */
    public boolean putParameterValuesIntoScope(final State state, final List<Value> values, final String assignmentContext) throws SetlException {
        final int numberOfValues = values.size();
        final int size           = fragmentList.size();
        for (int i = 0; i < size; ++i) {
            final ParameterDef param = fragmentList.get(i);
                  Value        value = null;
            if (i < numberOfValues) {
                value = values.get(i);
            } else if ( ! isLastParameterOfTypeList || i != size - 1) {
                value = param.getDefaultValue(state);
            }
            if (value == null) {
                value = Om.OM;
            }
            if (param.getType() == ParameterDef.ParameterType.READ_WRITE) {
                param.assign(state, value, assignmentContext);
            } else if (param.getType() == ParameterDef.ParameterType.LIST) {
                SetlList parameters = new SetlList();
                for (int valueIndex = i; valueIndex < numberOfValues; ++valueIndex) {
                    parameters.addMember(state, values.get(valueIndex));
                }
                param.assign(state, parameters, assignmentContext);
                break;
            } else {
                param.assign(state, value.clone(), assignmentContext);
            }
        }
        return rwParameters > 0;
    }

    /**
     * Assign given list of values to these parameters and put result into a map.
     *
     * @param state             Current state of the running setlX program.
     * @param values            List of values to assign.
     * @return                  Map of parameters and their values.
     * @throws SetlException    Thrown in case of some (user-) error.
     */
    public HashMap<ParameterDef, Value> putParameterValuesIntoMap(final State state, final List<Value> values) throws SetlException {
        final HashMap<ParameterDef, Value> assignments    = new HashMap<ParameterDef, Value>();
        final int                          numberOfValues = values.size();
        final int                          size           = fragmentList.size();
        for (int i = 0; i < size; ++i) {
            final ParameterDef param = fragmentList.get(i);
                  Value        value = null;
            if (i < numberOfValues) {
                value = values.get(i);
            } else if ( ! isLastParameterOfTypeList || i != size - 1) {
                value = param.getDefaultValue(state);
            }
            if (value == null) {
                value = Om.OM;
            }
            if (param.getType() == ParameterDef.ParameterType.READ_WRITE) {
                assignments.put(param, value);
            } else if (param.getType() == ParameterDef.ParameterType.LIST) {
                SetlList parameters = new SetlList();
                for (int valueIndex = i; valueIndex < numberOfValues; ++valueIndex) {
                    parameters.addMember(state, values.get(valueIndex));
                }
                assignments.put(param, parameters);
                break;
            } else {
                assignments.put(param, value.clone());
            }
        }
        return assignments;
    }

    /**
     * Extract the values currently assigned to these parameters.
     *
     * @param state          Current state of the running setlX program.
     * @param args           Expressions used to fill parameter before execution
     * @return               WriteBackAgent containing expressions and their current values
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public WriteBackAgent extractRwParametersFromScope(final State state, final List<Expr> args) throws SetlException {
        WriteBackAgent wba = null;

        if (rwParameters > 0) {
            wba = new WriteBackAgent(rwParameters);

            final int size = Math.min(fragmentList.size(), args.size());
            for (int i = 0; i < size; ++i) {
                final ParameterDef param = fragmentList.get(i);
                if (param.getType() == ParameterDef.ParameterType.READ_WRITE) {
                    // value of parameter after execution
                    final Value postValue = param.getValue(state);
                    // expression used to fill parameter before execution
                    final Expr preExpr   = args.get(i);
                        /* if possible the WriteBackAgent will set the variable used in this
                           expression to its postExecution state in the outer environment    */
                    wba.add(preExpr, postValue);
                }
            }
        }

        return wba;
    }

    /**
     * Extract the values currently assigned to these parameters.
     *
     * @param assignments    Current assignments of parameters to values.
     * @param args           Expressions used to fill parameter before execution
     * @return               WriteBackAgent containing expressions and their current values
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public WriteBackAgent extractRwParametersFromMap(final HashMap<ParameterDef, Value> assignments, final List<Expr> args) throws SetlException {
        WriteBackAgent wba = null;

        if (rwParameters > 0) {
            wba = new WriteBackAgent(rwParameters);

            final int size = Math.min(fragmentList.size(), args.size());
            for (int i = 0; i < size; ++i) {
                final ParameterDef param = fragmentList.get(i);
                if (param.getType() == ParameterDef.ParameterType.READ_WRITE) {
                    // value of parameter after execution
                    final Value postValue = assignments.get(param);
                    // expression used to fill parameter before execution
                    final Expr preExpr   = args.get(i);
                        /* if possible the WriteBackAgent will set the variable used in this
                           expression to its postExecution state in the outer environment    */
                    wba.add(preExpr, postValue);
                }
            }
        }

        return wba;
    }

    /**
     * Appends a string representation of this code fragment to the given
     * StringBuilder object.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString(State)
     *
     * @param state Current state of the running setlX program.
     * @param sb    StringBuilder to append to.
     */
    public void appendString(State state, StringBuilder sb) {
        final Iterator<ParameterDef> iterator = fragmentList.iterator();
        while (iterator.hasNext()) {
            iterator.next().appendString(state, sb, 0);
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
    }
    /**
     * Returns a string representation of this code fragment.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString()
     *
     * @param state Current state of the running setlX program.
     * @return      String representation.
     */
    public Value toTerm(final State state) throws SetlException {
        final SetlList paramList = new SetlList(fragmentList.size());
        for (final ParameterDef param: fragmentList) {
            paramList.addMember(state, param.toTerm(state));
        }
        return paramList;
    }

    /**
     * Convert a term fragment representing a parameter list into such an object.
     *
     * @param state                    Current state of the running setlX program.
     * @param termFragment             Term fragment to convert.
     * @return                         Resulting ParameterList.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static ParameterList termFragmentToParameterList(final State state, final Value termFragment) throws TermConversionException {
        if (termFragment.getClass() != SetlList.class) {
            throw new TermConversionException("malformed parameter list");
        } else {
            final SetlList      paramList  = (SetlList) termFragment;
            final ParameterList parameters = new ParameterList(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(state, v));
            }
            return parameters;
        }
    }
}

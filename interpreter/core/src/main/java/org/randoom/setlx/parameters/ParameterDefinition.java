package org.randoom.setlx.parameters;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ImmutableCodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * This is the base class for parameter definitions.
 */
public abstract class ParameterDefinition extends ImmutableCodeFragment {

    private final String var;
    private final OperatorExpression defaultExpr;

    /**
     * Create a new parameter definition.
     *
     * @param var         Variable to bind to.
     * @param defaultExpr Expression to compute default value.
     */
    public ParameterDefinition(final String var, final OperatorExpression defaultExpr) {
        this.var         = var;
        this.defaultExpr = defaultExpr;
    }

    /**
     * Create a new parameter definition.
     *
     * @param var  Variable to bind to.
     */
    public ParameterDefinition(final String var) {
        this(var, null);
    }

    @Override
    public final boolean collectVariablesAndOptimize (
        final State state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        if (boundVariables.contains(var)) {
            usedVariables.add(var);
        } else {
            unboundVariables.add(var);
        }
        if (defaultExpr != null) {
            defaultExpr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
        return false;
    }

    /**
     * Assign a value to this parameters variable.
     *
     * @param state          Current state of the running setlX program.
     * @param v              Value to set variable to.
     * @param context        Context description of the assignment for trace.
     * @throws SetlException Thrown in case of redefining a class.
     */
    public final void assign(final State state, final Value v, final String context) throws SetlException {
        state.putValue(var, v, context);
    }

    /**
     * Get value currently assigned to this parameters variable.
     *
     * @param state          Current state of the running setlX program.
     * @return               Value of this parameters variable in current scope.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public final Value getValue(final State state) throws SetlException {
        return state.findValue(var);
    }

    /**
     * Get default value defined for this parameter.
     *
     * @param state          Current state of the running setlX program.
     * @return               Default value of this parameter.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public final Value getDefaultValue(final State state) throws SetlException {
        if (defaultExpr != null) {
            return defaultExpr.evaluate(state);
        } else {
            return null;
        }
    }

    /**
     * Check if a default value is defined for this parameter.
     *
     * @return True, if a default value is defined for this parameter.
     */
    public final boolean hasDefaultValue() {
        return defaultExpr != null;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(var);
        if (defaultExpr != null) {
            sb.append(" := ");
            defaultExpr.appendString(state, sb, 0);
        }
    }

    /* term operations */

    /*package*/ Term createTerm(final State state, final String functionalCharacter) throws SetlException {
        final Term term = new Term(functionalCharacter, 2);
        term.addMember(state, new SetlString(var));
        if (defaultExpr != null) {
            term.addMember(state, defaultExpr.toTerm(state));
        } else {
            term.addMember(state, SetlString.NIL);
        }
        return term;
    }

    /**
     * Convert a term representing a ParameterDef into such an object.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting ParameterDef.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static ParameterDefinition valueToParameterDef(final State state, final Value value) throws TermConversionException {
        if (value.getClass() != Term.class) {
            throw new TermConversionException("malformed parameter definition");
        }
        final Term   term = (Term) value;
        final String fc   = term.getFunctionalCharacter();
        if (term.size() == 2 && term.firstMember().getClass() == SetlString.class) {
            final String var = term.firstMember().getUnquotedString(state);
            OperatorExpression defaultExpr = null;
            if (! term.lastMember().equals(SetlString.NIL)) {
                defaultExpr = OperatorExpression.createFromTerm(state, term.lastMember());
            }
            if (fc.equals(Parameter.getFunctionalCharacter())) {
                return new Parameter(var, defaultExpr);
            } else if (fc.equals(ReadWriteParameter.getFunctionalCharacter())) {
                return new ReadWriteParameter(var, defaultExpr);
            } else if (fc.equals(ListParameter.getFunctionalCharacter())) {
                return new ListParameter(var, defaultExpr);
            }
        }
        throw new TermConversionException("malformed parameter definition");
    }

    @Override
    public final int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == this.getClass()) {
            ParameterDefinition otr = (ParameterDefinition) other;
            int cmp = var.compareTo(otr.var);
            if (cmp != 0) {
                return cmp;
            }
            if (defaultExpr != null) {
                if (otr.defaultExpr != null) {
                    return defaultExpr.compareTo(otr.defaultExpr);
                } else {
                    return 1;
                }
            } else if(otr.defaultExpr != null) {
                return -1;
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering()) ? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(ParameterDefinition.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == this.getClass()) {
            ParameterDefinition otr = (ParameterDefinition) obj;
            if (var.equals(otr.var)) {
                if (defaultExpr != null && otr.defaultExpr != null) {
                    return defaultExpr.equals(otr.defaultExpr);
                } else if(defaultExpr == null && otr.defaultExpr == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        return var.hashCode();
    }
}


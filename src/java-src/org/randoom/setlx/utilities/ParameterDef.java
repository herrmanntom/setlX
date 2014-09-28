package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.util.List;

/**
 * This class represents a single parameter of a function definition.
 *
 * grammar rule:
 * procedureParameter
 *     : 'rw' variable
 *     |      variable
 *     ;
 *
 * implemented here as:
 *       ==== ========
 *       type   var
 */
public class ParameterDef extends CodeFragment implements Comparable<ParameterDef> {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER      = "^parameter";
    private final static String FUNCTIONAL_CHARACTER_RW   = "^rwParameter";
    private final static String FUNCTIONAL_CHARACTER_LIST = "^listParameter";

    /**
     * Type of parameter.
     */
    public enum ParameterType {
        /**
         * Binding used as parameter will not be changed in outer scope.
         */
        READ_ONLY,
        /**
         * Binding used as parameter will be changed in outer scope, if modified
         * in inner scope.
         */
        READ_WRITE,
        /**
         * Binding of multiple parameters. Can be empty or of 'unlimited' size at runtime.
         */
        LIST
    }

    private final Variable      var;
    private final ParameterType type;
    private final Expr          defaultExpr;

    /**
     * Create a new parameter definition.
     *
     * @param var         Variable to bind to.
     * @param type        Type of parameter.
     * @param defaultExpr Expression to compute default value.
     */
    public ParameterDef(final Variable var, final ParameterType type, final Expr defaultExpr) {
        this.var         = var;
        this.type        = type;
        this.defaultExpr = defaultExpr;
    }

    /**
     * Create a new parameter definition.
     *
     * @param var  Variable to bind to.
     * @param type Type of parameter.
     */
    public ParameterDef(final Variable var, final ParameterType type) {
        this(var, type, null);
    }

    /**
     * Create a new parameter definition.
     *
     * @param var  Variable to bind to.
     */
    public ParameterDef(final Variable var) {
        this(var, ParameterType.READ_ONLY);
    }

    @Override
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        var.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        if (defaultExpr != null) {
            defaultExpr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
    }

    /**
     * Assign a value to this parameters variable.
     *
     * @param state          Current state of the running setlX program.
     * @param v              Value to set variable to.
     * @param context        Context description of the assignment for trace.
     * @throws SetlException Thrown in case of redefining a class.
     */
    public void assign(final State state, final Value v, final String context) throws SetlException {
        var.assign(state, v, context);
    }

    /**
     * Get value currently assigned to this parameters variable.
     *
     * @param state          Current state of the running setlX program.
     * @return               Value of this parameters variable in current scope.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value getValue(final State state) throws SetlException {
        return var.eval(state);
    }

    /**
     * Get default value defined for this parameter.
     *
     * @param state          Current state of the running setlX program.
     * @return               Default value of this parameter.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value getDefaultValue(final State state) throws SetlException {
        if (defaultExpr != null) {
            return defaultExpr.eval(state);
        } else {
            return null;
        }
    }

    /**
     * Check if a default value is defined for this parameter.
     *
     * @return True, if a default value is defined for this parameter.
     */
    public boolean hasDefaultValue() {
        return defaultExpr != null;
    }

    /**
     * Get variable-name used as binding for this parameter.
     *
     * @return Variable-name used as binding.
     */
    public String getVar() {
        return var.getID();
    }

    /**
     * Return type of this parameter.
     *
     * @return Type of this parameter.
     */
    public ParameterType getType() {
        return type;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        if (type == ParameterType.READ_WRITE) {
            sb.append("rw ");
        } else if (type == ParameterType.LIST) {
            sb.append("*");
        }
        var.appendString(state, sb, 0);
        if (defaultExpr != null) {
            sb.append(" := ");
            defaultExpr.appendString(state, sb, 0);
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result;
        if (type == ParameterType.READ_WRITE) {
            result = new Term(FUNCTIONAL_CHARACTER_RW, 2);
        } else if (type == ParameterType.LIST) {
            result = new Term(FUNCTIONAL_CHARACTER_LIST, 2);
        } else {
            result = new Term(FUNCTIONAL_CHARACTER, 2);
        }
        result.addMember(state, var.toTerm(state));
        if (defaultExpr != null) {
            result.addMember(state, defaultExpr.toTerm(state));
        } else {
            result.addMember(state, SetlString.NIL);
        }
        return result;
    }

    /**
     * Convert a term representing a ParameterDef into such an object.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting ParameterDef.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static ParameterDef valueToParameterDef(final State state, final Value value) throws TermConversionException {
        if (value.getClass() != Term.class) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        }
        final Term   term = (Term) value;
        final String fc   = term.getFunctionalCharacter();
        if (term.size() == 2 && term.firstMember().getClass() == Term.class) {
            final Variable var = Variable.termToExpr(state, (Term) term.firstMember());
                  Expr     defaultExpr = null;
            if (! term.lastMember().equals(SetlString.NIL)) {
                defaultExpr = TermConverter.valueToExpr(state, term.lastMember());
            }
            if (fc.equals(FUNCTIONAL_CHARACTER)) {
                return new ParameterDef(var, ParameterType.READ_ONLY, defaultExpr);
            } else if (fc.equals(FUNCTIONAL_CHARACTER_RW)) {
                return new ParameterDef(var, ParameterType.READ_WRITE, defaultExpr);
            } else if (fc.equals(FUNCTIONAL_CHARACTER_LIST)) {
                return new ParameterDef(var, ParameterType.LIST, defaultExpr);
            }
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
    }

    @Override
    public int compareTo(final ParameterDef o) {
        if (this == o) {
            return 0;
        } else {
            final int cmp = type.compareTo(o.type);
            if (cmp != 0) {
                return cmp;
            }
            return var.getID().compareTo(o.var.getID());
        }
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == ParameterDef.class) {
            return equalTo((ParameterDef) obj);
        }
        return false;
    }

    /**
     * Test if two ParameterDef are equal.
     * This operation is much faster as ( compareTo(other) == 0 ).
     *
     * @param other Other ParameterDef to compare to `this'
     * @return      True if `this' equals `other', false otherwise.
     */
    public boolean equalTo(final ParameterDef other) {
        return this == other || this.type == other.type && this.var.equals(other.var);
    }

    @Override
    public int hashCode() {
        return var.hashCode();
    }
}


package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
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
public class ParameterDef extends CodeFragment {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER    = "^parameter";
    private final static String FUNCTIONAL_CHARACTER_RW = "^rwParameter";

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
        READ_WRITE
    }

    private final Variable      var;
    private final ParameterType type;

    /**
     * Create a new parameter definition.
     *
     * @param var  Variable to bind to.
     * @param type Type of parameter.
     */
    public ParameterDef(final Variable var, final ParameterType type) {
        this.var  = var;
        this.type = type;
    }

    /**
     * Create a new parameter definition.
     *
     * @param id   Variable-name to bind to.
     * @param type Type of parameter.
     */
    public ParameterDef(final String id, final ParameterType type) {
        this(new Variable(id), type);
    }

    /**
     * Create a new parameter definition.
     *
     * @param var  Variable to bind to.
     */
    public ParameterDef(final Variable var) {
        this(var, ParameterType.READ_ONLY);
    }

    /**
     * Create a new parameter definition.
     *
     * @param id   Variable-name to bind to.
     */
    public ParameterDef(final String id) {
        this(id, ParameterType.READ_ONLY);
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        var.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
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
        }
        var.appendString(state, sb, 0);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result;
        if (type == ParameterType.READ_WRITE) {
            result = new Term(FUNCTIONAL_CHARACTER_RW);
        } else {
            result = new Term(FUNCTIONAL_CHARACTER);
        }
        result.addMember(state, var.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing a ParameterDef into such an object.
     *
     * @param value                    Term to convert.
     * @return                         Resulting ParameterDef.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static ParameterDef valueToParameterDef(final Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        }
        final Term   term   = (Term) value;
        final String fc     = term.functionalCharacter().getUnquotedString();
        if (fc.equals(FUNCTIONAL_CHARACTER) && term.size() == 1 && term.firstMember() instanceof Term) {
            final Variable var = Variable.termToExpr((Term) term.firstMember());
            return new ParameterDef(var, ParameterType.READ_ONLY);
        } else if (fc.equals(FUNCTIONAL_CHARACTER_RW) && term.size() == 1 && term.firstMember() instanceof Term) {
            final Variable var = Variable.termToExpr((Term) term.firstMember());
            return new ParameterDef(var, ParameterType.READ_WRITE);
        } else {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        }
    }
}


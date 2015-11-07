package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.operatorUtilities.Condition;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operators.AOperator;
import org.randoom.setlx.operators.CollectionAccessRangeDummy;
import org.randoom.setlx.operators.ProcedureConstructor;
import org.randoom.setlx.operators.SetListConstructor;
import org.randoom.setlx.operators.TermConstructor;
import org.randoom.setlx.operators.ValueOperator;
import org.randoom.setlx.operators.VariableIgnore;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.ExpressionStatement;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.types.CachedProcedure;
import org.randoom.setlx.types.Closure;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.LambdaClosure;
import org.randoom.setlx.types.LambdaProcedure;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Procedure;
import org.randoom.setlx.types.RangeDummy;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to convert terms into their equivalent CodeFragment at runtime.
 */
public class TermConverter {

    private final static Set<String> STATEMENT_CHECKED = new HashSet<String>();
    private final static Map<String, Method> STATEMENT_CONVERTERS = new HashMap<String, Method>();

    /**
     * Convert a term (or value) representing a setlX-Value into such a value.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting Value.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Value valueTermToValue(final State state, final Value value) throws TermConversionException {
        if (value.getClass() == Term.class) {
            final Term   term = (Term) value;
            final String fc   = term.getFunctionalCharacter();
            if (fc.length() >= 3 && fc.charAt(0) == '^') { // all internally used terms start with ^
                // special cases
                // non-generic values
                if (fc.equals(CachedProcedure.getFunctionalCharacter())) {
                    return CachedProcedure.termToValue(state, term);
                } else if (fc.equals(Closure.getFunctionalCharacter())) {
                    return Closure.termToValue(state, term);
                } else if (fc.equals(LambdaClosure.getFunctionalCharacter())) {
                    return LambdaClosure.termToValue(state, term);
                } else if (fc.equals(LambdaProcedure.getFunctionalCharacter())) {
                    return LambdaProcedure.termToValue(state, term);
                } else if (fc.equals(Procedure.getFunctionalCharacter())) {
                    return Procedure.termToValue(state, term);
                } else if (fc.equals(SetlClass.getFunctionalCharacter())) {
                    return SetlClass.termToValue(state, term);
                } else if (fc.equals(SetlObject.getFunctionalCharacter())) {
                    return SetlObject.termToValue(state, term);
                } else if (fc.equals(Om.getFunctionalCharacter())) {
                    return Om.OM;
                }
            }
            throw new TermConversionException(
                "This term does not represent a value."
            );
        } else { // `value' is in fact a (more or less) simple value
            return value;
        }
    }

    /**
     * Convert a term (or value) representing a setlX-CodeFragment into such a fragment.
     *
     * @param state          Current state of the running setlX program.
     * @param value          Term to convert.
     * @param restrictToExpr Only convert to expressions, not statements.
     * @return               Resulting CodeFragment.
     */
    public static CodeFragment valueToCodeFragment(final State state, final Value value, final boolean restrictToExpr) {
        if (value.getClass() == Term.class) {
            final Term   term = (Term) value;
            final String fc   = term.getFunctionalCharacter();
            try {
                if (fc.length() >= 3 && fc.charAt(0) == '^') { // all internally used terms start with ^
                    Method  converter   = null;
                    if ( ! restrictToExpr) {
                        synchronized (STATEMENT_CONVERTERS) {
                            converter = STATEMENT_CONVERTERS.get(fc);
                        }
                        // search via reflection, if method was not found in map
                        if (converter == null && ! STATEMENT_CHECKED.contains(fc)) {
                            // string used for method look-up
                            final String    needle           = fc.substring(1, 2).toUpperCase(Locale.US) + fc.substring(2);
                            // look it up in statement package
                            final String    packageNameStmnt = Statement.class.getPackage().getName();
                            // class which is searched
                            Class<?> clAss;
                            try {
                                clAss     = Class.forName(packageNameStmnt + '.' + needle);
                                converter = clAss.getMethod("termToStatement", State.class, Term.class);

                                synchronized (STATEMENT_CONVERTERS) {
                                    STATEMENT_CONVERTERS.put(fc, converter);
                                }
                            } catch (final Exception e1) {
                                // look-up failed, nothing more to try
                                converter   = null;
                            }
                            synchronized (STATEMENT_CHECKED) {
                                STATEMENT_CHECKED.add(fc);
                            }
                        }
                    }
                    // invoke method found
                    if (converter != null) {
                        try {
                            return (CodeFragment) converter.invoke(null, state, term);
                        } catch (final Exception e) {
                            //noinspection ConstantConditions
                            if (e instanceof TermConversionException) {
                                throw (TermConversionException) e;
                            } else { // will never happen ;-)
                                // because we know this method exists etc
                                throw new TermConversionException("Impossible error...");
                            }
                        }
                    }
                    // special cases
                    final Value specialValue = valueTermToValue(state, value);

                    if (specialValue == Om.OM) {
                        return createValueExpression(specialValue);
                    } else if (specialValue instanceof Procedure) {
                        return createOperatorExpression(new ProcedureConstructor((Procedure) specialValue));
                    } else if (specialValue.isClass() == SetlBoolean.TRUE) {
                        return createValueExpression(specialValue);
                    } else if (specialValue.isObject() == SetlBoolean.TRUE) {
                        return createValueExpression(specialValue);
                    }
                }
                // This term does not represent an CodeFragment.
                return createOperatorExpression(TermConstructor.termToExpr(state, term));
            } catch (final TermConversionException tce) {
                // some error occurred... create TermConstructor for this custom term
                return createOperatorExpression(TermConstructor.termToExpr(state, term));
            }
        } else { // `value' is in fact a (more or less) simple value
            try {
                if (value == IgnoreDummy.ID) {
                    return createOperatorExpression(VariableIgnore.VI);
                } else if (value == RangeDummy.RD) {
                    return createOperatorExpression(CollectionAccessRangeDummy.CARD);
                } else if (value.isList() == SetlBoolean.TRUE || value.isSet() == SetlBoolean.TRUE) {
                    return createOperatorExpression(SetListConstructor.valueToExpr(state, value));
                } else {
                    // not a special value
                    return createValueExpression(value);
                }
            } catch (final TermConversionException tce) {
                // some error occurred... create ValueExpr for this value
                return createValueExpression(value);
            }
        }
    }

    private static OperatorExpression createValueExpression(Value value) {
        return createOperatorExpression(new ValueOperator(value));
    }

    private static OperatorExpression createOperatorExpression(AOperator operator) {
        return new OperatorExpression(new FragmentList<AOperator>(operator));
    }

    /**
     * Convert a term (or value) representing a setlX-Expr into such an expression.
     *
     * @param state Current state of the running setlX program.
     * @param value Term to convert.
     * @return      Resulting Expr.
     */
    public static OperatorExpression valueToExpr(final State state, final Value value) {
        return (OperatorExpression) valueToCodeFragment(state, value, true);
    }

    /**
     * Convert a term (or value) representing a setlX-Expr into such an expression.
     *
     * @param state Current state of the running setlX program.
     * @param value Term to convert.
     * @return      Resulting Expr.
     * @throws TermConversionException in case the term is not of an assignable expression
     */
    public static AAssignableExpression valueToAssignableExpr(final State state, final Value value) throws TermConversionException {
        try {
            return valueToExpr(state, value).convertToAssignable();
        } catch (UndefinedOperationException e) {
            throw new TermConversionException("malformed assignable expression", e);
        }
    }

    /**
     * Convert a term (or value) representing a setlX-Condition into such a condition.
     *
     * @param state Current state of the running setlX program.
     * @param value Term to convert.
     * @return      Resulting Condition.
     */
    public static Condition valueToCondition(final State state, final Value value) {
        return new Condition(valueToExpr(state, value));
    }

    /**
     * Convert a term (or value) representing a setlX-Statement into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting Statement.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Statement valueToStatement(final State state, final Value value) throws TermConversionException {
        final CodeFragment cf = valueToCodeFragment(state, value, false);
        if (cf instanceof Statement) {
            return (Statement) cf;
        } else if (cf instanceof OperatorExpression) {
            return new ExpressionStatement((OperatorExpression) cf);
        } else {
            throw new TermConversionException(
                "This term does not represent a Statement."
            );
        }
    }

    /**
     * Convert a term (or value) representing a setlX-Statement-Block into such a block.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting Block.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Block valueToBlock(final State state, final Value value) throws TermConversionException {
        final Statement s = valueToStatement(state, value);
        if (s instanceof Block) {
            return (Block) s;
        } else { // wrap into block
            return new Block(s);
        }
    }
}


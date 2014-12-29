package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.boolExpressions.Equals;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.expressions.CollectionAccessRangeDummy;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.ProcedureConstructor;
import org.randoom.setlx.expressions.SetListConstructor;
import org.randoom.setlx.expressions.TermConstructor;
import org.randoom.setlx.expressions.ValueExpr;
import org.randoom.setlx.expressions.VariableIgnore;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.ExpressionStatement;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.types.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class to convert terms into their equivalent CodeFragment at runtime.
 */
public class TermConverter {

    private final static Map<String, Method> EXPR_CONVERTERS      = new HashMap<String, Method>();
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
                    synchronized (EXPR_CONVERTERS) {
                        // search in expr map
                        converter = EXPR_CONVERTERS.get(fc);
                        // and in statement map if allowed and nothing was found (yet)
                        if ( ! restrictToExpr && converter == null) {
                            converter = STATEMENT_CONVERTERS.get(fc);
                        }
                    }
                    // search via reflection, if method was not found in maps
                    if (converter == null) {
                        // string used for method look-up
                        final String    needle           = fc.substring(1, 2).toUpperCase(Locale.US) + fc.substring(2);
                        // look it up in [bool]expression and statement packages
                        final String    packageNameBExpr = Equals   .class.getPackage().getName();
                        final String    packageNameExpr  = Expr     .class.getPackage().getName();
                        final String    packageNameStmnt = Statement.class.getPackage().getName();
                        // class which is searched
                              Class<?>  clAss            = null;
                        try {
                            clAss     = Class.forName(packageNameBExpr + '.' + needle);
                            converter = clAss.getMethod("termToExpr", State.class, Term.class);

                            synchronized (EXPR_CONVERTERS) {
                                EXPR_CONVERTERS.put(fc, converter);
                            }
                        } catch (final Exception e1) {
                            // look-up failed, try next package
                            try {
                                clAss     = Class.forName(packageNameExpr + '.' + needle);
                                converter = clAss.getMethod("termToExpr", State.class, Term.class);

                                synchronized (EXPR_CONVERTERS) {
                                    EXPR_CONVERTERS.put(fc, converter);
                                }
                            } catch (final Exception e2) {
                                // look-up failed, try next package
                                if (restrictToExpr) {
                                    converter = null;
                                } else {
                                    try {
                                        clAss     = Class.forName(packageNameStmnt + '.' + needle);
                                        converter = clAss.getMethod("termToStatement", State.class, Term.class);

                                        synchronized (EXPR_CONVERTERS) {
                                            STATEMENT_CONVERTERS.put(fc, converter);
                                        }
                                    } catch (final Exception e3) {
                                        // look-up failed, nothing more to try
                                        converter   = null;
                                    }
                                }
                            }
                        }
                    }
                    // invoke method found
                    if (converter != null) {
                        try {
                            return (CodeFragment) converter.invoke(null, state, term);
                        } catch (final Exception e) {
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
                        return new ValueExpr(specialValue);
                    } else if (specialValue instanceof Procedure) {
                        return new ProcedureConstructor((Procedure) specialValue);
                    } else if (specialValue.isClass() == SetlBoolean.TRUE) {
                        return new ValueExpr(specialValue);
                    } else if (specialValue.isObject() == SetlBoolean.TRUE) {
                        return new ValueExpr(specialValue);
                    }
                }
                // This term does not represent an CodeFragment.
                return TermConstructor.termToExpr(state, term);
            } catch (final TermConversionException tce) {
                // some error occurred... create TermConstructor for this custom term
                return TermConstructor.termToExpr(state, term);
            }
        } else { // `value' is in fact a (more or less) simple value
            try {
                if (value == IgnoreDummy.ID) {
                    return VariableIgnore.VI;
                } else if (value == RangeDummy.RD) {
                    return CollectionAccessRangeDummy.CARD;
                } else if (value.isList() == SetlBoolean.TRUE || value.isSet() == SetlBoolean.TRUE) {
                    return SetListConstructor.valueToExpr(state, value);
                } else {
                    // not a special value
                    return new ValueExpr(value);
                }
            } catch (final TermConversionException tce) {
                // some error occurred... create ValueExpr for this value
                return new ValueExpr(value);
            }
        }
    }

    /**
     * Convert a term (or value) representing a setlX-Expr into such an expression.
     *
     * @param state Current state of the running setlX program.
     * @param value Term to convert.
     * @return      Resulting Expr.
     */
    public static Expr valueToExpr(final State state, final Value value) {
        return (Expr) valueToCodeFragment(state, value, true);
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


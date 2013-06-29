package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.boolExpressions.Equals;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.expressions.CollectionAccessRangeDummy;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.ProcedureConstructor;
import org.randoom.setlx.expressions.SetListConstructor;
import org.randoom.setlx.expressions.StringConstructor;
import org.randoom.setlx.expressions.TermConstructor;
import org.randoom.setlx.expressions.ValueExpr;
import org.randoom.setlx.expressions.VariableIgnore;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.ExpressionStatement;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.types.CachedProcedure;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.LambdaDefinition;
import org.randoom.setlx.types.Procedure;
import org.randoom.setlx.types.RangeDummy;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TermConverter {

    private static Map<String, Method> sExprConverters      = new HashMap<String, Method>();
    private static Map<String, Method> sStatementConverters = new HashMap<String, Method>();

    public static Value valueTermToValue(final Value value) throws TermConversionException {
        if (value instanceof Term) {
            final Term   term = (Term) value;
            final String fc   = term.functionalCharacter().getUnquotedString();
            if (fc.length() >= 3 && fc.charAt(0) == '^') { // all internally used terms start with ^
                // special cases
                // non-generic values
                if (fc.equals(CachedProcedure.FUNCTIONAL_CHARACTER)) {
                    return CachedProcedure.termToValue(term);
                } else if (fc.equals(LambdaDefinition.FUNCTIONAL_CHARACTER)) {
                    return LambdaDefinition.termToValue(term);
                } else if (fc.equals(Procedure.FUNCTIONAL_CHARACTER)) {
                    return Procedure.termToValue(term);
                } else if (fc.equals(SetlClass.FUNCTIONAL_CHARACTER)) {
                    return SetlClass.termToValue(term);
                } else if (fc.equals(SetlObject.FUNCTIONAL_CHARACTER)) {
                    return SetlObject.termToValue(term);
                } else if (fc.equals(Om.FUNCTIONAL_CHARACTER)) {
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

    public static CodeFragment valueToCodeFragment(final Value value, final boolean restrictToExpr) {
        if (value instanceof Term) {
            final Term    term    = (Term) value;
            final String  fc      = term.functionalCharacter().getUnquotedString();
            try {
                if (fc.length() >= 3 && fc.charAt(0) == '^') { // all internally used terms start with ^
                    Method  converter   = null;
                    synchronized (sExprConverters) {
                        // search in expr map
                        converter = sExprConverters.get(fc);
                        // and in statement map if allowed and nothing was found (yet)
                        if ( ! restrictToExpr && converter == null) {
                            converter = sStatementConverters.get(fc);
                        }
                    }
                    // search via reflection, if method was not found in maps
                    if (converter == null) {
                        // string used for method look-up
                        final String    needle              = fc.substring(1, 2).toUpperCase(Locale.US) + fc.substring(2);
                        // look it up in [bool]expression and statement packages
                        final String    packageNameBExpr    = Equals   .class.getPackage().getName();
                        final String    packageNameExpr     = Expr     .class.getPackage().getName();
                        final String    packageNameStmnt    = Statement.class.getPackage().getName();
                        // class which is searched
                              Class<?>  clAss               = null;
                        try {
                            clAss       = Class.forName(packageNameBExpr + '.' + needle);
                            converter   = clAss.getMethod("termToExpr", Term.class);

                            synchronized (sExprConverters) {
                                sExprConverters.put(fc, converter);
                            }
                        } catch (final Exception e1) {
                            // look-up failed, try next package
                            try {
                                clAss       = Class.forName(packageNameExpr + '.' + needle);
                                converter   = clAss.getMethod("termToExpr", Term.class);

                                synchronized (sExprConverters) {
                                    sExprConverters.put(fc, converter);
                                }
                            } catch (final Exception e2) {
                                // look-up failed, try next package
                                if (restrictToExpr) {
                                    converter   = null;
                                } else {
                                    try {
                                        clAss       = Class.forName(packageNameStmnt + '.' + needle);
                                        converter   = clAss.getMethod("termToStatement", Term.class);

                                        synchronized (sExprConverters) {
                                            sStatementConverters.put(fc, converter);
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
                            return (CodeFragment) converter.invoke(null, term);
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
                    final Value specialValue = valueTermToValue(value);

                    if (specialValue == Om.OM) {
                        return new ValueExpr(specialValue);
                    } else if (specialValue instanceof Procedure) {
                        return new ProcedureConstructor((Procedure) specialValue);
                    } else if (specialValue instanceof SetlClass) {
                        return new ValueExpr(specialValue);
                    } else if (specialValue instanceof SetlObject) {
                        return new ValueExpr(specialValue);
                    }
                }
                throw new TermConversionException(
                    "This term does not represent an CodeFragment."
                );
            } catch (final TermConversionException tce) {
                // create TermConstructor for this custom term
                return TermConstructor.termToExpr(term);
            }
        } else { // `value' is in fact a (more or less) simple value
            try {
                if (value == IgnoreDummy.ID) {
                    return VariableIgnore.VI;
                } else if (value == RangeDummy.RD) {
                    return CollectionAccessRangeDummy.CARD;
                } else if (value instanceof SetlList || value instanceof SetlSet) {
                    return SetListConstructor.valueToExpr(value);
                } else if (value instanceof SetlString) {
                    return StringConstructor.valueToExpr(value);
                } else {
                    throw new TermConversionException("not a special value");
                }
            } catch (final TermConversionException tce) {
                // some error occurred... create ValueExpr for this value
                return new ValueExpr(value);
            }
        }
    }

    public static Expr valueToExpr(final Value value) {
        return (Expr) valueToCodeFragment(value, true);
    }

    public static Condition valueToCondition(final Value value) {
        return new Condition(valueToExpr(value));
    }

    public static Statement valueToStatement(final Value value) throws TermConversionException {
        final CodeFragment cf = valueToCodeFragment(value, false);
        if (cf instanceof Statement) {
            return (Statement) cf;
        } else if (cf instanceof Expr) {
            return new ExpressionStatement((Expr) cf);
        } else {
            throw new TermConversionException(
                "This term does not represent a Statement."
            );
        }
    }

    public static Block valueToBlock(final Value value) throws TermConversionException {
        final Statement   s   = valueToStatement(value);
        if (s instanceof Block) {
            return (Block) s;
        } else { // wrap into block
            final Block   b   = new Block(1);
            b.add(s);
            return b;
        }
    }
}


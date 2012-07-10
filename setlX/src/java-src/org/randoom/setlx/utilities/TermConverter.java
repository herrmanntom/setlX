package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.boolExpressions.Equal;
import org.randoom.setlx.expressions.BracketedExpr;
import org.randoom.setlx.expressions.CollectionAccessRangeDummy;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.SetListConstructor;
import org.randoom.setlx.expressions.StringConstructor;
import org.randoom.setlx.expressions.TermConstructor;
import org.randoom.setlx.expressions.ValueExpr;
import org.randoom.setlx.expressions.VariableIgnore;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.ExpressionStatement;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.LambdaDefinition;
import org.randoom.setlx.types.ProcedureDefinition;
import org.randoom.setlx.types.RangeDummy;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TermConverter {

    private static Map<String, Method> sExprConverters;
    private static Map<String, Method> sStatementConverters;

    public static CodeFragment valueToCodeFragment(final Value value, final boolean restrictToExpr) {
        if (value instanceof Term) {
            if (sExprConverters == null || sStatementConverters == null) {
                sExprConverters         = new HashMap<String, Method>();
                sStatementConverters    = new HashMap<String, Method>();
            }
            final Term    term    = (Term) value;
            final String  fc      = term.functionalCharacter().getUnquotedString();
            try {
                if (fc.length() >= 3 && fc.charAt(0) == '^') { // all internally used terms start with ^
                    // search in expr map
                    Method  converter   = sExprConverters.get(fc);
                    // and in statement map if allowed and nothing was found (yet)
                    if ( ! restrictToExpr && converter == null) {
                        converter   = sStatementConverters.get(fc);
                    }
                    // search via reflection, if method was not found in maps
                    if (converter == null) {
                        // string used for method look-up
                        final String    needle              = fc.substring(1, 2).toUpperCase() + fc.substring(2);
                        // look it up in [bool]expression and statement packages
                        final String    packageNameBExpr    = Equal    .class.getPackage().getName();
                        final String    packageNameExpr     = Expr     .class.getPackage().getName();
                        final String    packageNameStmnt    = Statement.class.getPackage().getName();
                        // class which is searched
                              Class<?>  clAss               = null;
                        try {
                            clAss       = Class.forName(packageNameBExpr + '.' + needle);
                            converter   = clAss.getMethod("termToExpr", Term.class);
                            sExprConverters.put(fc, converter);
                        } catch (Exception e1) {
                            // look-up failed, try next package
                            try {
                                clAss       = Class.forName(packageNameExpr + '.' + needle);
                                converter   = clAss.getMethod("termToExpr", Term.class);
                                sExprConverters.put(fc, converter);
                            } catch (Exception e2) {
                                // look-up failed, try next package
                                if (restrictToExpr) {
                                    converter   = null;
                                } else {
                                    try {
                                        clAss       = Class.forName(packageNameStmnt + '.' + needle);
                                        converter   = clAss.getMethod("termToStatement", Term.class);
                                        sStatementConverters.put(fc, converter);
                                    } catch (Exception e3) {
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
                        } catch (Exception e) {
                            if (e instanceof TermConversionException) {
                                throw (TermConversionException) e;
                            } else { // will never happen ;-)
                                // because we know this method exists etc
                                throw new TermConversionException("Impossible error...");
                            }
                        }
                    }
                    // special cases
                    // non-generic values
                    else if (fc.equals(LambdaDefinition.FUNCTIONAL_CHARACTER)) {
                        return new ValueExpr(LambdaDefinition.termToValue(term));
                    } else if (fc.equals(ProcedureDefinition.FUNCTIONAL_CHARACTER)) {
                        return new ValueExpr(ProcedureDefinition.termToValue(term));
                    }
                    // nothing matched
                    else {
                        throw new TermConversionException(
                            "Functional character does not represent an CodeFragment."
                        );
                    }
                } else {
                    throw new TermConversionException(
                        "Functional character does not represent an CodeFragment."
                    );
                }
            } catch (TermConversionException tce) {
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
            } catch (TermConversionException tce) {
                // some error occurred... create ValueExpr for this value
                return new ValueExpr(value);
            }
        }
    }

    public static Expr valueToExpr(final int callersPrecedence, final boolean brackedEqualLevel, final Value value) {
        final Expr result         = (Expr) valueToCodeFragment(value, true);
        final int  exprPrecedence = result.precedence();
        if (brackedEqualLevel && callersPrecedence >= exprPrecedence) {
            return new BracketedExpr(result);
        } else if (callersPrecedence > exprPrecedence) {
            return new BracketedExpr(result);
        }
        return result;
    }

    public static Expr valueToExpr(final Value value) {
        return valueToExpr(0000, false, value);
    }

    public static Condition valueToCondition(final Value value) {
        return new Condition(valueToExpr(value));
    }

    public static Statement valueToStatement(final Value value) {
        final CodeFragment cf = valueToCodeFragment(value, false);
        if (cf instanceof Statement) {
            return (Statement) cf;
        } else { // must be an expression
            return new ExpressionStatement((Expr) cf);
        }
    }

    public static Block valueToBlock(final Value value) {
        final Statement   s   = valueToStatement(value);
        if (s instanceof Block) {
            return (Block) s;
        } else { // wrap into block
            Block   b   = new Block(1);
            b.add(s);
            return b;
        }
    }
}


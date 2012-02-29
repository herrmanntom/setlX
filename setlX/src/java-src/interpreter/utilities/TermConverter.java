package interpreter.utilities;

import interpreter.exceptions.TermConversionException;
import interpreter.boolExpressions.Equal;
import interpreter.expressions.Assignment;
import interpreter.expressions.CallRangeDummy;
import interpreter.expressions.Expr;
import interpreter.expressions.SetListConstructor;
import interpreter.expressions.StringConstructor;
import interpreter.expressions.TermConstructor;
import interpreter.expressions.ValueExpr;
import interpreter.expressions.VariableIgnore;
import interpreter.statements.Block;
import interpreter.statements.ExpressionStatement;
import interpreter.statements.Statement;
import interpreter.types.IgnoreDummy;
import interpreter.types.LambdaDefinition;
import interpreter.types.ProcedureDefinition;
import interpreter.types.RangeDummy;
import interpreter.types.SetlList;
import interpreter.types.SetlSet;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TermConverter {

    private static Map<String, Method> sExprConverters;
    private static Map<String, Method> sStatementConverters;

    public static CodeFragment valueToCodeFragment(Value value, boolean restrictToExpr) {
        if (value instanceof Term) {
            if (sExprConverters == null || sStatementConverters == null) {
                sExprConverters         = new HashMap<String, Method>();
                sStatementConverters    = new HashMap<String, Method>();
            }
            Term    term    = (Term) value;
            String  fc      = term.functionalCharacter().getUnquotedString();
            try {
                if (fc.charAt(0) == '\'' && fc.length() >= 3) { // all internally used terms start with single straight quote
                    // search in expr map
                    Method  converter   = sExprConverters.get(fc);
                    // and in statement map if allowed and nothing was found (yet)
                    if ( ! restrictToExpr && converter == null) {
                        converter   = sStatementConverters.get(fc);
                    }
                    // search via reflection, if method was not found in maps
                    if (converter == null) {
                        // string used for method look-up
                        String      needle              = fc.substring(1, 2).toUpperCase() + fc.substring(2);
                        // look it up in [bool]expression and statement packages
                        String      packageNameBExpr    = Equal    .class.getPackage().getName();
                        String      packageNameExpr     = Expr     .class.getPackage().getName();
                        String      packageNameStmnt    = Statement.class.getPackage().getName();
                        // class which is searched
                        Class<?>    clAss               = null;
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
                            if (false) { throw new TermConversionException(""); } // FIX: compiler is unable to determine that invoked method may throw this exception
                            return (CodeFragment) converter.invoke(null, term);
                        } catch (TermConversionException tce) {
                            throw tce;
                        } catch (Exception iae) { // this will never ever happen ;-)
                            throw new TermConversionException("Impossible error...");
                        }
                    }
                    // special cases
                    // non-generic expressions
                    else if (fc.equals(Assignment.FUNCTIONAL_CHARACTER_SUM)        ||
                             fc.equals(Assignment.FUNCTIONAL_CHARACTER_DIFFERENCE) ||
                             fc.equals(Assignment.FUNCTIONAL_CHARACTER_PRODUCT)    ||
                             fc.equals(Assignment.FUNCTIONAL_CHARACTER_DIVISION)   ||
                             fc.equals(Assignment.FUNCTIONAL_CHARACTER_MODULO)
                    ) {
                        return Assignment.termToExpr(term);
                    }
                    // non-generic values
                    else if (fc.equals(LambdaDefinition.FUNCTIONAL_CHARACTER)) {
                        return new ValueExpr(LambdaDefinition.termToValue(term));
                    } else if (fc.equals(ProcedureDefinition.FUNCTIONAL_CHARACTER)) {
                        return new ValueExpr(ProcedureDefinition.termToValue(term));
                    }
                    // nothing matched
                    else {
                        throw new TermConversionException("Functional character does not represent an CodeFragment.");
                    }
                } else {
                    throw new TermConversionException("Functional character does not represent an CodeFragment.");
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
                    return CallRangeDummy.CRD;
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

    public static Expr valueToExpr(Value value) {
        return (Expr) valueToCodeFragment(value, true);
    }

    public static Condition valueToCondition(Value value) {
        return new Condition(valueToExpr(value));
    }

    public static Statement valueToStatement(Value value) {
        CodeFragment cf = valueToCodeFragment(value, false);
        if (cf instanceof Statement) {
            return (Statement) cf;
        } else { // must be an expression
            return new ExpressionStatement((Expr) cf);
        }
    }

    public static Block valueToBlock(Value value) {
        Statement   s   = valueToStatement(value);
        if (s instanceof Block) {
            return (Block) s;
        } else { // wrap into block
            Block   b   = new Block();
            b.add(s);
            return b;
        }
    }

}


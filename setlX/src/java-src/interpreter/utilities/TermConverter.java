package interpreter.utilities;

import interpreter.exceptions.TermConversionException;
import interpreter.boolExpressions.*;
import interpreter.expressions.*;
import interpreter.statements.*;
import interpreter.types.IgnoreDummy;
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

    private static Map<String, Method> sConverters;

    public static CodeFragment valueToCodeFragment(Value value, boolean restrictToExpr) {
        if (value instanceof Term) {
            if (sConverters == null) {
                sConverters = new HashMap<String, Method>();
            }
            Term    term    = (Term) value;
            String  fc      = term.functionalCharacter().getUnquotedString();
            try {
                if (fc.charAt(0) == '\'' && fc.length() >= 3) { // all internally used terms start with single straight quote
                    // search in map
                    Method  converter   = sConverters.get(fc);
                    // search via reflection if method was not found in map
                    if (converter == null) {
                        // string used for method look-up
                        String      needle              = fc.substring(1, 2).toUpperCase() + fc.substring(2);
                        // look it up in [bool]expression and statement packages
                        String      packageNameBExpr    = Comparison.class.getPackage().getName();
                        String      packageNameExpr     = Expr      .class.getPackage().getName();
                        String      packageNameStmnt    = Statement .class.getPackage().getName();
                        // class which is searched
                        Class<?>    clAss               = null;
                        try {
                            clAss       = Class.forName(packageNameBExpr + '.' + needle);
                            converter   = clAss.getMethod("termToExpr", Term.class);
                        } catch (Exception e1) {
                            // look-up failed, try next package
                            try {
                                clAss       = Class.forName(packageNameExpr + '.' + needle);
                                converter   = clAss.getMethod("termToExpr", Term.class);
                            } catch (Exception e2) {
                                // look-up failed, try next package
                                if (restrictToExpr) {
                                    converter   = null;
                                } else {
                                    try {
                                        clAss       = Class.forName(packageNameExpr + '.' + needle);
                                        converter   = clAss.getMethod("termToStatement", Term.class);
                                    } catch (Exception e3) {
                                        // look-up failed, nothing more to try
                                        converter   = null;
                                    }
                                }
                            }
                        }
                        // insert result into map (if there is any result)
                        if (converter != null) {
                            sConverters.put(fc, converter);
                        }
                    }
                    // invoke method found
                    if (converter != null) {
                        try {
                            return (CodeFragment) converter.invoke(null, term);
                        } catch (Exception iae) { // this will never happen ;-)
                            throw new TermConversionException("Functional character does not represent an CodeFragment.");
                        }
                    }
                    // special cases
                    // non-generic expressions TODO
                    else if (false) {
                        return null;
                    }
                    // end of non-generic expressions
                    else if (restrictToExpr) {
                        throw new TermConversionException("Functional character does not represent an expression.");
                    }
                    // non-generic statements TODO
                    else if (false) {
                        return null;
                    }
                    // end of non-generic statements
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

    public static Statement valueToStatement(Value value) {
        CodeFragment cf = valueToCodeFragment(value, false);
        if (cf instanceof Statement) {
            return (Statement) cf;
        } else { // must be an expression
            return new ExpressionStatement((Expr) cf);
        }
    }

}


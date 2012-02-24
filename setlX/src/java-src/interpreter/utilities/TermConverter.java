package interpreter.utilities;

import interpreter.exceptions.TermConversionException;
import interpreter.expressions.*;
import interpreter.statements.*;
import interpreter.types.IgnoreDummy;
import interpreter.types.RangeDummy;
import interpreter.types.SetlList;
import interpreter.types.SetlSet;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;


public class TermConverter {

    public static CodeFragment valueToCodeFragment(Value value, boolean restrictToExpr) {
        if (value instanceof Term) {
            Term    term    = (Term) value;
            try {
                String  fc  = term.functionalCharacter().getUnquotedString();
                // expressions
                if (false) {
                    return null;
                } else if (fc == BracketedExpr.FUNCTIONAL_CHARACTER) {
                    return BracketedExpr.termToExpr(term);
                } else if (fc == CallRangeDummy.FUNCTIONAL_CHARACTER) {
                    return CallRangeDummy.CRD;
                } else if (fc == Product.FUNCTIONAL_CHARACTER) {
                    return Product.termToExpr(term);
                } else if (fc == StringConstructor.FUNCTIONAL_CHARACTER) {
                    return StringConstructor.termToExpr(term);
                } else if (fc == Sum.FUNCTIONAL_CHARACTER) {
                    return Sum.termToExpr(term);
                } else if (fc == Variable.FUNCTIONAL_CHARACTER) {
                    return Variable.termToExpr(term);
                } else if (fc == VariableIgnore.FUNCTIONAL_CHARACTER) {
                    return VariableIgnore.VI;
                }
                // end of expressions
                else if (restrictToExpr) {
                    throw new TermConversionException("Functional character does not represent an expression.");
                }
                // statements
                else if (false) {
                    return null;
                }
                // nothing matched
                else {
                    throw new TermConversionException("Functional character does not represent an CodeFragment.");
                }
            } catch (TermConversionException tce) {
                // some error occurred... create TermConstructor for this custom term
                return TermConstructor.termToExpr(term);
            }
        } else {
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


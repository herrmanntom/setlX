package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.CollectionValue;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.TermConverter;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
assignment
    : (variable ('[' anyExpr ']')* | idList) (':=' | '+=' | '-=' | '*=' | '/=' | '%=') ((assignment)=> assignment | anyExpr)
    ;

implemented here as:
       ========  ===============     ======
         mLhs        mItems           mLhs
*/

public class AssignmentLhs {
    // functional character used in terms (only used when assigning into collection (e.g. `a[1] := 5')
    private final static String FUNCTIONAL_CHARACTER_COLLECTION = "'assignmentIntoCollection";

    private Expr        mLhs;   // lhs (should be either a Variable or ListConstructor)
    private List<Expr>  mItems; // subsequent CollectionAccess's upon the mLhs Expr

    public AssignmentLhs(Expr lhs, List<Expr> items) {
        mLhs   = lhs;
        mItems = items;
    }

    public AssignmentLhs(Expr lhs) {
        this(lhs, null);
    }

    public Expr getExpr() {
        Expr result = mLhs;
        if (mItems != null && mItems.size() > 0) {
            for (Expr e : mItems) {
                List<Expr> args = new ArrayList<Expr>(1);
                args.add(e);
                result = new Call(result, args);
            }
        }
        return result;
    }

    public Value setValue(Value v) throws SetlException {
        if (mItems != null && mItems.size() > 0) {
            setVariableAfterCollectionAccess(v);
        } else {
            mLhs.assign(v);
        }
        return v;
    }

    /* string operations */

    public String toString(int tabs) {
        String result = mLhs.toString(tabs);
        if (mItems != null && mItems.size() > 0) {
            for (Expr e: mItems) {
                result += "[" + e.toString(tabs) + "]";
            }
        }
        return result;
    }

    /* term operations */

    public Value toTerm() {
        Value result = mLhs.toTerm();
        if (mItems != null && mItems.size() > 0) {
            Term        coll    = new Term(FUNCTIONAL_CHARACTER_COLLECTION);
            SetlList    args    = new SetlList();
            coll.addMember(result);
            coll.addMember(args);
            for (Expr e: mItems) {
                args.addMember(e.toTerm());
            }
            result  = coll;
        }
        return result;
    }

    public static AssignmentLhs valueToAssignmentLhs(Value value) throws TermConversionException {
        if (value instanceof Term) {
            Term    term    = (Term) value;
            String  fc      = term.functionalCharacter().getUnquotedString();
            if (fc.equalsIgnoreCase(Variable.FUNCTIONAL_CHARACTER)) {
                Variable    var = Variable.termToExpr(term);
                return new AssignmentLhs(var);
            } else if (fc.equals(FUNCTIONAL_CHARACTER_COLLECTION) && term.size() == 2 && term.lastMember() instanceof SetlList) {
                Expr        expr    = TermConverter.valueToExpr(term.firstMember());
                SetlList    argsLst = (SetlList) term.lastMember();
                List<Expr>  args    = new ArrayList<Expr>(argsLst.size());
                for (Value v : argsLst) {
                    args.add(TermConverter.valueToExpr(v));
                }
                return new AssignmentLhs(expr, args);
            } else {
                throw new TermConversionException("malformed AssignmentLhs");
            }
        } else if (value instanceof SetlList) {
            Expr    lhs = TermConverter.valueToExpr(value);
            return new AssignmentLhs(lhs);
        } else {
            throw new TermConversionException("malformed AssignmentLhs");
        }
    }

    /* private methods */

    // first evaluate the variable, then subsequently perform all CollectionAccess's and assign value to last result
    private void setVariableAfterCollectionAccess(Value newValue) throws SetlException {
        Value current = mLhs.eval();
        for (int i = 0; i < mItems.size(); ++i) {
            if (current instanceof CollectionValue) {
                Value index   = mItems.get(i).eval();
                if (i < mItems.size() - 1) {
                    current = current.getMemberUnCloned(index);
                } else {
                    current.setMember(index, newValue); // no newValue.clone() here, because setMember() already clones
                }
            } else {
                throw new IncompatibleTypeException("Left-hand-side of \"" + this + " := " + newValue + "\" is unusable for list assignment.");
            }
        }
    }
}


package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.IgnoreDummy;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlList;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/*
grammar rule:
string
    :   '@'?        STRING
    ;

implemented here as:
        ====        ======
      mEvaluate  mOriginalStr
*/

public class StringConstructor extends Expr {
    private boolean      mEvaluate;    // should this string be evaluated ('@' -> false)
    private String       mOriginalStr; // original String
    private List<String> mFragments;   // list of string fragments for after and between expressions
    private List<Expr>   mExprs;       // list of $-Expressions

    public StringConstructor(boolean evaluate, String originalStr) {
        mEvaluate    = evaluate;
        mOriginalStr = originalStr;
        mFragments   = new LinkedList<String>();
        mExprs       = new LinkedList<Expr>();

        // Strip out double quotes which the parser left in
        originalStr  = originalStr.substring(1, originalStr.length() - 1);
        int length   = originalStr.length();

        if (evaluate) {
            StringBuilder fragment  = new StringBuilder(); // buffer for string fragment
            StringBuilder expr      = new StringBuilder(); // buffer for inner expr string
            boolean       innerExpr = false;               // currently reading inner expr ?
            for (int i = 0; i < length; ++i) {
                char c = originalStr.charAt(i);  // current char
                char n = (i+1 < length)? originalStr.charAt(i+1) : '\0';  // next char
                if (innerExpr) {
                    if (c == '$') {
                        // end of inner expr
                        innerExpr = false;
                        // parse inner expr
                        try {
                            SetlString eStr = SetlString.createFromConstructor(expr.toString()); // parses escape characters properly
                            Expr exp = ParseSetlX.parseStringToExpr(eStr.getString());
                            // add inner expr to mExprs
                            mExprs.add(exp);
                        } catch (SetlException se) {
                            // this is all futile, as outer parsing run, which called this constructor, will notice the error and (later) break
                            // however we provide the user with at least some feedback
                            System.err.println("Parsing-Error in string " + this + ": " + se.getMessage());
                        }
                        // clear expression
                        expr.setLength(0);
                    } else {
                        // continue expr string
                        expr.append(c);
                    }
                } else {
                    if (c == '\\' && n == '$') {
                        // escaped dollar
                        fragment.append('$');
                        i++; // jump over next char
                    } else if (c == '$') {
                        // end outer string
                        mFragments.add(fragment.toString());
                        fragment.setLength(0);
                        // start inner expression
                        innerExpr = true;
                    } else {
                        // continue outer string
                        fragment.append(c);
                    }
                }
            }
            if (innerExpr) { // inner expr not complete
                // this was all futile
                // make outer parsing run, which called this constructor, notice the error and (later) break
                ParseSetlX.addReportedError();
                // however we provide the user with at least some feedback
                System.err.println("Parsing-Error in string " + this + ": closing '$' missing.");
            }
            // outer string must always be appended, even if empty
            mFragments.add(fragment.toString());
        } else {
            mFragments.add(originalStr);
        }
    }

    public SetlString evaluate() throws SetlException {
        Iterator<String>    fIter   = mFragments.iterator();
        Iterator<Expr>      eIter   = mExprs.iterator();
        // there always is at least one fragment, even if empty
        SetlString          result  = SetlString.createFromConstructor(fIter.next());
        while (eIter.hasNext() && fIter.hasNext()) {
            Expr    exp = eIter.next();
            Value   str = SetlString.createFromConstructor(fIter.next()); // string after expression
            // eval expression, but fail gracefully
            Value   v   = null;
            try {
                v   = exp.eval();
            } catch (SetlException se) {
                v   = new SetlString("$Error: " + se.getMessage() + "$");
            }
            // add both values, which concatenates them if at least one is a string, and str is indeed
            str = v.add(str);
            // concatenate (again)
            result = result.add(str);
        }
        // now expr-list should be empty in all cases
        if (eIter.hasNext()) {
            throw new UndefinedOperationException("Internal error in string construction!");
        }
        // however there might still be some fragments left
        while (fIter.hasNext()) {
            Value   str = SetlString.createFromConstructor(fIter.next());
            // concatenate (again)
            result = result.add(str);
        }
        return result;
    }

    /* string operations */

    public String toString(int tabs) {
        return (mEvaluate? "" : "@") + mOriginalStr;
    }

    /* term operations */

    public Term toTerm() {
        Term     result  = new Term("'string");

        SetlList strList = new SetlList();
        for (String str: mFragments) {
            strList.addMember(new SetlString(str));
        }
        result.addMember(strList);

        SetlList expList = new SetlList();
        for (Expr expr: mExprs) {
            expList.addMember(expr.toTerm());
        }
        result.addMember(expList);

        return result;
    }
}


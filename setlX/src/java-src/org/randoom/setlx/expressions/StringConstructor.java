package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.TermConverter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class StringConstructor extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^stringConstructor";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

    private boolean      mQuoted;      // do not parse inner expressions ('@' -> true)
    private String       mOriginalStr; // original String
    private List<String> mFragments;   // list of string fragments for after and between expressions
    private List<Expr>   mExprs;       // list of $-Expressions

    public StringConstructor(boolean quoted, String originalStr) {
        this(quoted, originalStr, new LinkedList<String>(), new LinkedList<Expr>());

        // Strip out double quotes which the parser left in
        originalStr  = originalStr.substring(1, originalStr.length() - 1);
        int length   = originalStr.length();

        if ( ! quoted) {
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
                            // SetlString parses escape characters properly
                            SetlString eStr = SetlString.createFromConstructor(expr.toString());
                            Expr exp = ParseSetlX.parseStringToExpr(eStr.getUnquotedString());
                            // add inner expr to mExprs
                            mExprs.add(exp);
                        } catch (SetlException se) {
                            /* Doing error handling here is futile, as outer parsing run,
                             * which called this constructor, will notice via the global
                             * error count and (later) halt.
                             * However we can at least provide the user with some feedback.
                             */
                            System.err.println(
                                "Parsing-Error in string " + this + ": " + se.getMessage()
                            );
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
                /* Doing error handling here is futile
                 * Instead make outer parsing run, which called this constructor,
                 * notice this error and (later) halt.
                 */
                ParseSetlX.addReportedError();
                // However we can at least provide the user with some feedback.
                System.err.println("Parsing-Error in string " + this + ": closing '$' missing.");
            }
            // outer string must always be appended, even if empty
            mFragments.add(fragment.toString());
        } else {
            mFragments.add(originalStr);
        }
    }

    private StringConstructor(boolean quoted, String originalStr, List<String> fragments, List<Expr> exprs) {
        mQuoted         = quoted;
        mOriginalStr    = originalStr;
        mFragments      = fragments;
        mExprs          = exprs;
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
            // add both values, which concatenates them
            str = (SetlString) v.str().sum(str);
            // concatenate (again)
            result = (SetlString) result.sum(str);
        }
        // now expr-list should be empty in all cases
        if (eIter.hasNext()) {
            throw new UndefinedOperationException("Internal error in string construction!");
        }
        // however there might still be some fragments left
        while (fIter.hasNext()) {
            Value   str = SetlString.createFromConstructor(fIter.next());
            // concatenate (again)
            result = (SetlString) result.sum(str);
        }
        return result;
    }

    /* string operations */

    public String toString(int tabs) {
        return mOriginalStr;
    }

    /* term operations */

    public Value toTerm() {
        if (mFragments.size() == 1 && mExprs.size() == 0) {
            // simple string without $-expression
            return new SetlString(mFragments.get(0));
        } else {
            Term result  = new Term(FUNCTIONAL_CHARACTER);

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

    public static Expr termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList && term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            boolean         quoted      = false;
            String          originalStr = "\"";
            List<String>    fragments   = new LinkedList<String>();
            List<Expr>      exprs       = new LinkedList<Expr>();

            Iterator<Value> fIter       = ((SetlList) term.firstMember()).iterator();
            Iterator<Value> eIter       = ((SetlList) term.lastMember()).iterator();

            while (fIter.hasNext()) {
                SetlString  sstring = (SetlString) fIter.next();
                String      string  = sstring.getUnquotedString();
                if ( ! quoted && string.contains("$")) {
                    quoted = true;
                }
                originalStr += sstring.getEscapedString();
                fragments.add(string);

                if (eIter.hasNext()) {
                    Expr expr = TermConverter.valueToExpr(eIter.next());
                    exprs.add(expr);
                    originalStr += "$" + expr.toString().replace("$", "\\$") + "$";
                }
            }
            if (eIter.hasNext()) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
            originalStr += "\"";
            Expr result = new StringConstructor(quoted, originalStr, fragments, exprs);
            if (quoted) {
                return new Quote(result);
            } else {
                return result;
            }
        }
    }

    public static Expr valueToExpr(Value value) throws TermConversionException {
        if ( ! (value instanceof SetlString)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        }
        SetlString      sstring     = (SetlString) value;
        String          string      = sstring.getUnquotedString();
        // string was quoted when it contains a $, otherwise it would have been split
        boolean         quoted      = string.contains("$");
        String          originalStr = "\"" + sstring.getEscapedString() + "\"";
        List<String>    fragments   = new LinkedList<String>();
        fragments.add(string);
        List<Expr>      exprs       = new LinkedList<Expr>();
        Expr            result      = new StringConstructor(quoted, originalStr, fragments, exprs);
        if (quoted) {
            return new Quote(result);
        } else {
            return result;
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


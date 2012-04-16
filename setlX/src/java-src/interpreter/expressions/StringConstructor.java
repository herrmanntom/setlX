package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.IgnoreDummy;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlList;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.ParseSetlX;
import interpreter.utilities.TermConverter;

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
                            SetlString eStr = SetlString.createFromConstructor(expr.toString()); // parses escape characters properly
                            Expr exp = ParseSetlX.parseStringToExpr(eStr.getUnquotedString());
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
        boolean         quoted      = string.contains("$"); // string was quoted when it contains a $, otherwise it would have been split
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


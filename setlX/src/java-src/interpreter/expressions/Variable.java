package interpreter.expressions;

import interpreter.exceptions.TermConversionException;
import interpreter.types.Om;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.VariableScope;

/*
grammar rule:
variable
    : ID
    ;

implemented here as:
      ==
      mId
*/

public class Variable extends Expr {
    // This functional character is used internally
    public  final static String FUNCTIONAL_CHARACTER          = "'Variable";
    // this one is used externally (e.g. during toString)
    public  final static String FUNCTIONAL_CHARACTER_EXTERNAL = "'variable";
    /* both are equal during matching and compare. However while terms with the
     * internal one always bind anything, terms with the external one only match
     * and do not bind.
     *
     * This is done to create a difference between the cases used in
     *      match(term) {
     *          case 'variable(x): foo2(); // matches only variables
     *          case x           : foo1(); // `x'.toTerm() results in 'Variable("x"); matches everything and binds it to x
     *      }
     */
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE                    = 9999;

    private String  mId;
    private boolean mIsTerm;
    private int     mLineNr;

    public Variable(String id) {
        mId     = id;
        mIsTerm  = (id.length() > 0 && (id.charAt(0) == '\'' || Character.isUpperCase(id.charAt(0))));
        mLineNr = -1;
    }

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = Environment.sourceLine;
    }

    public Value evaluate() {
        if (mIsTerm) {
            return new Term(mId);
        }

        Value v = VariableScope.findValue(mId);
        if (v == null) {
            return Om.OM;
        } else {
            return v;
        }
    }

    // sets this expression to the given value
    public void assign(Value v) {
        VariableScope.putValue(mId, v.clone());
    }

    // sets this expression to the given value
    public void makeGlobal() {
        VariableScope.makeGlobal(mId);
    }

    /* string operations */

    public String toString(int tabs) {
        return mId;
    }

    /* term operations */

    public Term toTerm() {
        if (mIsTerm) {
            return new Term(mId);
        }

        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(new SetlString(mId));
        return result;
    }

    public static Variable termToExpr(Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlString)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            String id = ((SetlString) term.firstMember()).getUnquotedString();
            return new Variable(id);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


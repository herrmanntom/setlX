package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.VariableScope;

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
    public  final static String FUNCTIONAL_CHARACTER          = "^Variable";
    // this one is used externally (e.g. during toString)
    public  final static String FUNCTIONAL_CHARACTER_EXTERNAL = "^variable";
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

    private final String mId;

    public Variable(final String id) {
        mId = id;
    }

    protected Value evaluate() {
        return VariableScope.findValue(mId);
    }

    // sets this expression to the given value
    public Value assign(final Value v) {
        VariableScope.putValue(mId, v);
        return v.clone();
    }

    // sets this expression to the given value
    public void makeGlobal() {
        VariableScope.makeGlobal(mId);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append(mId);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(new SetlString(mId));
        return result;
    }

    public Term toTermQuoted() {
        Term result = new Term(FUNCTIONAL_CHARACTER_EXTERNAL, 1);
        result.addMember(new SetlString(mId));
        return result;
    }

    public static Variable termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlString)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final String id = ((SetlString) term.firstMember()).getUnquotedString();
            return new Variable(id);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


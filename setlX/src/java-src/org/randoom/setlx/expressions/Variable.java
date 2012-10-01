package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

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

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        if (boundVariables.contains(this)) {
            usedVariables.add(this);
        } else {
            unboundVariables.add(this);
        }
    }

    // sets this expression to the given value
    public void assignUncloned(final Value v) {
        VariableScope.putValue(mId, v);
    }

    /* Similar to assignUncloned(),
       However, also checks if the variable is already defined in scopes up to
       (but EXCLUDING) `outerScope'.
       Returns true and sets `v' if variable is undefined or already equal to `v'.
       Returns false, if variable is defined and different from `v' */
    public boolean assignUnclonedCheckUpTo(final Value v, final VariableScope outerScope) {
        return VariableScope.putValueCheckUpTo(mId, v, outerScope);
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

    // methods used when inserted into HashSets etc
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Variable) {
            return mId.equals(((Variable) o).mId);
        } else {
            return false;
        }
    }
    public final boolean equals(final Variable v) {
        if (this == v) {
            return true;
        } else {
            return mId.equals(v.mId);
        }
    }

    private final static int initHashCode = Variable.class.hashCode();

    public int hashCode() {
        return initHashCode + mId.hashCode();
    }
}


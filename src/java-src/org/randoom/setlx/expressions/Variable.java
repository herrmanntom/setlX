package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
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

public class Variable extends AssignableExpression {
    // This functional character is used internally
    public  final static String   FUNCTIONAL_CHARACTER          = "^Variable";
    // this one is used externally (e.g. during toString)
    public  final static String   FUNCTIONAL_CHARACTER_EXTERNAL = "^variable";
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

    public  final static String   PREVENT_OPTIMIZATION_DUMMY    = "@123456789*%%";

    // precedence level in SetlX-grammar
    private final static int      PRECEDENCE                    = 9999;

    private final String mId;

    public Variable(final String id) {
        mId = id;
    }

    @Override
    public Value evaluate(final State state) throws SetlException {
        return state.findValue(mId);
    }

    @Override
    /*package*/ Value evaluateUnCloned(final State state) throws SetlException {
        return evaluate(state);
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        if (boundVariables.contains(mId)) {
            usedVariables.add(mId);
        } else {
            unboundVariables.add(mId);
        }
    }

    // sets this expression to the given value
    @Override
    public void assignUncloned(final State state, final Value v) throws IllegalRedefinitionException {
        state.putValue(mId, v);
    }

    /* Similar to assignUncloned(),
       However, also checks if the variable is already defined in scopes up to
       (but EXCLUDING) `outerScope'.
       Returns true and sets `v' if variable is undefined or already equal to `v'.
       Returns false, if variable is defined and different from `v' */
    @Override
    public boolean assignUnclonedCheckUpTo(final State state, final Value v, final VariableScope outerScope) throws SetlException {
        return state.putValueCheckUpTo(mId, v, outerScope);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(mId);
    }

    public String getID() {
        return mId;
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(state, new SetlString(mId));
        return result;
    }

    @Override
    public Term toTermQuoted(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER_EXTERNAL, 1);
        result.addMember(state, new SetlString(mId));
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
    @Override
    public int precedence() {
        return PRECEDENCE;
    }

    // methods used when inserted into HashSets etc
    @Override
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

    @Override
    public int hashCode() {
        return initHashCode + mId.hashCode();
    }
}


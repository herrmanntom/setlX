package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.TermConverter;


// grammar rule:
// reduce
//     : factor ([..] | '*/' factor)*
//     ;
//
// implemented here as:
//       ======              ======
//      mNeutral           mCollection

public class MultiplyMembersBinary extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^multiplyMembersBinary";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1850;

    private Expr mNeutral;
    private Expr mCollection;

    public MultiplyMembersBinary(Expr neutral, Expr collection) {
        mNeutral    = neutral;
        mCollection = collection;
    }

    protected Value evaluate() throws SetlException {
        return mCollection.eval().multiplyMembers(mNeutral.eval());
    }

    /* string operations */

    public String toString(int tabs) {
        return mNeutral.toString(tabs) + " */ " + mCollection.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mNeutral.toTerm());
        result.addMember(mCollection.toTerm());
        return result;
    }

    public static MultiplyMembersBinary termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr neutral    = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            Expr collection = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
            return new MultiplyMembersBinary(neutral, collection);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


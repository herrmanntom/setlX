package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
call
    : variable ('(' callParameters ')')? ('[' collectionAccessParams ']' | '{' anyExpr '}')*
    ;

implemented here as:
      ==================================                                       =======
                   mLhs                                                          mArg
*/

public class CollectMap extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^collectMap";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private final Expr  mLhs;      // left hand side (Variable, other CollectMap, CollectionAccess, etc)
    private final Expr  mArg;      // argument

    public CollectMap(final Expr lhs, final Expr arg) {
        mLhs = lhs;
        mArg = arg;
    }

    protected Value evaluate() throws SetlException {
        final Value lhs = mLhs.eval();
        if (lhs == Om.OM) {
            throw new UnknownFunctionException("\"" + mLhs + "\" is undefined.");
        }
        return lhs.collectMap(mArg.eval().clone());
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
        mLhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        mArg.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        mLhs.appendString(sb, tabs);
        sb.append("{");
        mArg.appendString(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mLhs.toTerm());
        result.addMember(mArg.toTerm());
        return result;
    }

    public Term toTermQuoted() throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mLhs.toTermQuoted());
        result.addMember(mArg.eval().toTerm());
        return result;
    }

    public static CollectMap termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(term.firstMember());
            final Expr arg = TermConverter.valueToExpr(term.lastMember());
            return new CollectMap(lhs, arg);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


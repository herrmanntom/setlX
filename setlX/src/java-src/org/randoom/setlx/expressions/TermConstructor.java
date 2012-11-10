package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
grammar rule:
term
    : TERM '(' termArguments ')'
    ;

implemented here as:
      ====     ==============
     mFChar        mArgs
*/

public class TermConstructor extends Expr {
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

    private final String     mFChar;     // functional character of the term
    private final List<Expr> mArgs;      // list of arguments

    public TermConstructor(final String fChar, final List<Expr> args) {
        mFChar  = fChar;
        mArgs   = args;
    }

    protected Term evaluate(final State state) throws SetlException {
        final Term result = new Term(mFChar, mArgs.size());

        for (final Expr arg: mArgs) {
            result.addMember(arg.eval(state).toTerm(state)); // evaluate arguments at runtime
        }

        return result;
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
        for (final Expr arg: mArgs) {
            arg.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append(mFChar);
        sb.append("(");

        final Iterator<Expr> iter = mArgs.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append(")");
    }

    /* term operations */

    public Term toTerm(final State state) {
        final Term result = new Term(mFChar, mArgs.size());

        for (final Expr arg: mArgs) {
            result.addMember(arg.toTerm(state)); // do not evaluate here
        }

        return result;
    }

    public Term toTermQuoted(final State state) throws SetlException {
        return this.evaluate(state);
    }

    public static Expr termToExpr(final Term term) {
        final String        functionalCharacter = term.functionalCharacter().getUnquotedString();
        final List<Expr>    args                = new ArrayList<Expr>(term.size());
        for (final Value v : term) {
            args.add(TermConverter.valueToExpr(v));
        }
        return new TermConstructor(functionalCharacter, args);
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


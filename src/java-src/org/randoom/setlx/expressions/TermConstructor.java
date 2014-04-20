package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An expression creating a Term.
 *
 * grammar rule:
 * term
 *     : TERM '(' termArguments ')'
 *     ;
 *
 * implemented here as:
 *       ====     ==============
 *      mFChar        mArgs
 */
public class TermConstructor extends Expr {
    // precedence level in SetlX-grammar
    private final static int PRECEDENCE = 9999;

    private final String     fChar;     // functional character of the term
    private final List<Expr> args;      // list of arguments

    /**
     * Constructor.
     *
     * @param fChar Functional character of the term.
     * @param args  List of arguments.
     */
    public TermConstructor(final String fChar, final List<Expr> args) {
        this.fChar = fChar;
        this.args = args;
    }

    @Override
    protected Term evaluate(final State state) throws SetlException {
        final Term result = new Term(fChar, args.size());

        for (final Expr arg: args) {
            result.addMember(state, arg.eval(state).toTerm(state)); // evaluate arguments at runtime
        }

        return result;
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        for (final Expr arg: args) {
            arg.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(fChar);
        sb.append("(");

        final Iterator<Expr> iter = args.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append(")");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(fChar, args.size());

        for (final Expr arg: args) {
            result.addMember(state, arg.toTerm(state)); // do not evaluate here
        }

        return result;
    }

    @Override
    public Term toTermQuoted(final State state) throws SetlException {
        return this.evaluate(state);
    }

    /**
     * Convert a term representing a TermConstructor expression into such an expression.
     *
     * @param state Current state of the running setlX program.
     * @param term  Term to convert.
     * @return      Resulting expression of this conversion.
     */
    public static TermConstructor termToExpr(final State state, final Term term) {
        final String        functionalCharacter = term.getFunctionalCharacter();
        final List<Expr>    args                = new ArrayList<Expr>(term.size());
        for (final Value v : term) {
            args.add(TermConverter.valueToExpr(state, v));
        }
        return new TermConstructor(functionalCharacter, args);
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


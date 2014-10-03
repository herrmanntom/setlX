package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A function call (or something syntactically identical).
 *
 * grammar rule:
 *
 * factor
 *     : [..]
 *     | ('(' expr ')' | procedure | variable) (memberAccess | call)* '!'?
 *     ;
 *
 * call
 *     :                                           ('(' callParameters ')' | [..])*
 *     ;
 *
 * implemented here as:
 *        ===================================           ==============
 *                     lhs                                   args
 */
public class Call extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Call.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 2100;

    private final Expr       lhs;  // left hand side
    private final List<Expr> args; // list of arguments

    /**
     * Create a new call expression.
     *
     * @param lhs  Left hand side to evaluate before execute the call on its result.
     * @param args Expressions to evaluate as arguments of the call.
     */
    public Call(final Expr lhs, final List<Expr> args) {
        this.lhs  = lhs;
        this.args = args;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        final Value lhs = this.lhs.eval(state);
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                "Left hand side \"" + this.lhs.toString(state) + "\" is undefined."
            );
        }
        // supply the original expressions (args), which are needed for 'rw' parameters
        try {
            return lhs.call(state, args);
        } catch (final SetlException se) {
            final StringBuilder error = new StringBuilder();
            error.append("Error in \"");
            lhs.appendString(state, error, 0);
            appendArgsString(state, error);
            error.append("\":");
            se.addToTrace(error.toString());
            throw se;
        }
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        lhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        for (final Expr expr : args) {
            expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
        // add dummy variable to prevent optimization, behavior of called function is unknown here!
        unboundVariables.add(Variable.getPreventOptimizationDummy());
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendString(state, sb, tabs);
        appendArgsString(state, sb);
    }
    private void appendArgsString(final State state, final StringBuilder sb) {
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
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        if (lhs instanceof Variable) {
            result.addMember(state, new SetlString(((Variable) lhs).getID()));
        } else {
            result.addMember(state, lhs.toTerm(state));
        }

        final SetlList arguments = new SetlList(args.size());
        for (final Expr arg: args) {
            arguments.addMember(state, arg.toTerm(state));
        }
        result.addMember(state, arguments);

        return result;
    }

    @Override
    public Term toTermQuoted(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        if (lhs instanceof Variable) {
            result.addMember(state, new SetlString(((Variable) lhs).getID()));
        } else {
            result.addMember(state, lhs.toTerm(state));
        }

        final SetlList arguments = new SetlList(args.size());
        for (final Expr arg: args) {
            arguments.addMember(state, arg.eval(state).toTerm(state));
        }
        result.addMember(state, arguments);

        return result;
    }

    /**
     * Convert a term representing a Call into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static Call termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Value lhsTerm = term.firstMember();
            final Expr  lhs;
            if (lhsTerm instanceof SetlString) {
                lhs = new Variable(lhsTerm.getUnquotedString(state));
            } else {
                lhs = TermConverter.valueToExpr(state, lhsTerm);
            }
            final SetlList   argsLst = (SetlList) term.lastMember();
            final List<Expr> args    = new ArrayList<Expr>(argsLst.size());
            for (final Value v : argsLst) {
                args.add(TermConverter.valueToExpr(state, v));
            }
            return new Call(lhs, args);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final Expr other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Call.class) {
            Call otr = (Call) other;
            if (lhs == otr.lhs && args == otr.args) {
                return 0; // clone
            }
            int cmp = lhs.compareTo(otr.lhs);
            if (cmp != 0) {
                return cmp;
            }
            final Iterator<Expr> iterFirst  = args.iterator();
            final Iterator<Expr> iterSecond = otr.args.iterator();
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                cmp = iterFirst.next().compareTo(iterSecond.next());
                if (cmp != 0) {
                    return cmp;
                }
            }
            if (iterFirst.hasNext()) {
                return 1;
            }
            if (iterSecond.hasNext()) {
                return -1;
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Call.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == Call.class) {
            Call other = (Call) obj;
            if (lhs == other.lhs && args == other.args) {
                return true; // clone
            } else if (args.size() == other.args.size() && lhs.equals(other.lhs)) {
                final Iterator<Expr> iterFirst  = args.iterator();
                final Iterator<Expr> iterSecond = other.args.iterator();
                while (iterFirst.hasNext() && iterSecond.hasNext()) {
                    if ( ! iterFirst.next().equals(iterSecond.next())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + lhs.hashCode();
        final int size = args.size();
        hash = hash * 31 + size;
        if (size >= 1) {
            hash = hash * 31 + args.get(0).hashCode();
            if (size >= 2) {
                hash = hash * 31 + args.get(size-1).hashCode();
            }
        }
        return hash;
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    public static String functionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


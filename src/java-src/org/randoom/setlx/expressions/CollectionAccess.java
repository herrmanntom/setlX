package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Expression that accesses a specific member of a collection value.
 *
 * grammar rule:
 * call
 *     : variable ('(' callParameters ')')? ('[' collectionAccessParams ']' | '{' anyExpr '}')*
 *     ;
 *
 * implemented here as:
 *       ==================================      ======================
 *                   lhs                                args
 */
public class CollectionAccess extends AssignableExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(CollectionAccess.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private final Expr       lhs;              // left hand side (Variable, CollectMap, other CollectionAccess, etc)
    private final List<Expr> args;             // list of arguments
    private final boolean    argsContainRange; // does args contain RangeDummy?

    /**
     * Constructor.
     *
     * @param lhs  Left hand side (Variable, CollectMap, other CollectionAccess, etc).
     * @param args List of parameters.
     */
    public CollectionAccess(final Expr lhs, final List<Expr> args) {
        this.lhs              = lhs;
        this.args             = args;
        this.argsContainRange = args.contains(CollectionAccessRangeDummy.CARD);
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        final Value lhs = this.lhs.eval(state);
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                "Left hand side \"" + this.lhs.toString(state) + "\" is undefined."
            );
        }
        // evaluate all arguments
        List<Value> args = new ArrayList<Value>(this.args.size());
        for (final Expr arg: this.args) {
            args.add(arg.eval(state).clone());
        }
        if ( ! argsContainRange && args.size() > 1) {
            SetlList argumentList = new SetlList(args.size());
            for (Value arg : args) {
                argumentList.addMember(state, arg);
            }
            args = new ArrayList<Value>(1);
            args.add(argumentList);
        }
        // execute
        return lhs.collectionAccess(state, args);
    }

    @Override
    /*package*/ Value evaluateUnCloned(final State state) throws SetlException {
        if (lhs instanceof AssignableExpression) {
            final Value lhs = ((AssignableExpression) this.lhs).evaluateUnCloned(state);
            if (lhs == Om.OM) {
                throw new UnknownFunctionException(
                    "Left hand side \"" + this.lhs.toString(state) + "\" is undefined."
                );
            }

            // evaluate all arguments
            List<Value> args = new ArrayList<Value>(this.args.size());
            for (final Expr arg: this.args) {
                args.add(arg.eval(state).clone());
            }
            if ( ! argsContainRange && args.size() > 1) {
                SetlList argumentList = new SetlList(args.size());
                for (Value arg : args) {
                    argumentList.addMember(state, arg);
                }
                args = new ArrayList<Value>(1);
                args.add(argumentList);
            }

            // execute
            return lhs.collectionAccessUnCloned(state, args);

        } else {
            throw new IncompatibleTypeException(
                "\"" + this.toString(state) + "\" is unusable for list assignment."
            );
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
    }

    @Override
    public void collectVariablesWhenAssigned (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        if (lhs instanceof AssignableExpression) {
            // lhs & args are read, not bound, so use collectVariablesAndOptimize()
            lhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
            for (final Expr expr : args) {
                expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
            }
        }
    }

    // sets this expression to the given value
    @Override
    public void assignUncloned(final State state, final Value value, final String context) throws SetlException {
        if ( ! argsContainRange && args.size() >= 1 && lhs instanceof AssignableExpression) {
            final Value lhs = ((AssignableExpression) this.lhs).evaluateUnCloned(state);
            if (lhs == Om.OM) {
                throw new UnknownFunctionException(
                    "Left hand side \"" + this.lhs.toString(state) + "\" is undefined."
                );
            }

            if (args.size() > 1) {
                // evaluate all arguments
                List<Value> arguments = new ArrayList<Value>(this.args.size());
                for (final Expr arg: this.args) {
                    arguments.add(arg.eval(state).clone());
                }
                SetlList argumentList = new SetlList(arguments.size());
                for (Value arg : arguments) {
                    argumentList.addMember(state, arg);
                }
                lhs.setMember(state, argumentList, value);
            } else {
                lhs.setMember(state, args.get(0).eval(state), value);
            }
        } else {
            throw new IncompatibleTypeException(
                "Left-hand-side of \"" + this.toString(state) + " := " + value.toString(state) + "\" is unusable for list assignment."
            );
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendString(state, sb, tabs);
        sb.append("[");

        final Iterator<Expr> iter = args.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                if ( ! argsContainRange) {
                    sb.append(",");
                }
                sb.append(" ");
            }
        }

        sb.append("]");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term     result    = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, lhs.toTerm(state));

        final SetlList arguments = new SetlList(args.size());
        for (final Expr arg: args) {
            arguments.addMember(state, arg.toTerm(state));
        }
        result.addMember(state, arguments);

        return result;
    }

    @Override
    public Term toTermQuoted(final State state) throws SetlException {
        final Term     result    = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, lhs.toTermQuoted(state));

        final SetlList arguments = new SetlList(args.size());
        for (final Expr arg: args) {
            arguments.addMember(state, arg.eval(state).toTerm(state));
        }
        result.addMember(state, arguments);

        return result;
    }

    /**
     * Convert a term representing a CollectionAccess into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting CollectionAccess Expression.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static CollectionAccess termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr       lhs     = TermConverter.valueToExpr(state, term.firstMember());
            final SetlList   argsLst = (SetlList) term.lastMember();
            final List<Expr> args    = new ArrayList<Expr>(argsLst.size());
            for (final Value v : argsLst) {
                args.add(TermConverter.valueToExpr(state, v));
            }
            return new CollectionAccess(lhs, args);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == CollectionAccess.class) {
            CollectionAccess otr = (CollectionAccess) other;
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

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(CollectionAccess.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == CollectionAccess.class) {
            CollectionAccess other = (CollectionAccess) obj;
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
}


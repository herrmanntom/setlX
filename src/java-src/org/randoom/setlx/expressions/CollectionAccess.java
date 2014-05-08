package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
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

    private final Expr       lhs;       // left hand side (Variable, CollectMap, other CollectionAccess, etc)
    private final List<Expr> args;      // list of arguments

    /**
     * Constructor.
     *
     * @param lhs Left hand side (Variable, CollectMap, other CollectionAccess, etc).
     * @param arg Parameter.
     */
    public CollectionAccess(final Expr lhs, final Expr arg) {
        this(lhs, new ArrayList<Expr>(1));
        args.add(arg);
    }

    /**
     * Constructor.
     *
     * @param lhs  Left hand side (Variable, CollectMap, other CollectionAccess, etc).
     * @param args List of parameters.
     */
    public CollectionAccess(final Expr lhs, final List<Expr> args) {
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
        // evaluate all arguments
        final List<Value> args = new ArrayList<Value>(this.args.size());
        for (final Expr arg: this.args) {
            if (arg != null) {
                args.add(arg.eval(state).clone());
            }
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
            final List<Value> args = new ArrayList<Value>(this.args.size());
            for (final Expr arg: this.args) {
                if (arg != null) {
                    args.add(arg.eval(state).clone());
                }
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
        if (args.size() == 1 && lhs instanceof AssignableExpression) {
            final Value lhs = ((AssignableExpression) this.lhs).evaluateUnCloned(state);
            if (lhs == Om.OM) {
                throw new UnknownFunctionException(
                    "Left hand side \"" + this.lhs.toString(state) + "\" is undefined."
                );
            }
            lhs.setMember(state, args.get(0).eval(state), value);
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
                sb.append(" ");
            }
        }

        sb.append("]");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
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

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


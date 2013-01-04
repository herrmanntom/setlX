package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
grammar rule:
call
    : variable ('(' callParameters ')')? ('[' collectionAccessParams ']' | '{' anyExpr '}')*
    ;

implemented here as:
      ==================================      ======================
                  mLhs                                mArgs
*/

public class CollectionAccess extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^collectionAccess";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private final Expr       mLhs;       // left hand side (Variable, CollectMap, other CollectionAccess, etc)
    private final List<Expr> mArgs;      // list of arguments

    public CollectionAccess(final Expr lhs, final Expr arg) {
        this(lhs, new ArrayList<Expr>(1));
        mArgs.add(arg);
    }

    public CollectionAccess(final Expr lhs, final List<Expr> args) {
        mLhs    = lhs;
        mArgs   = args;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        final Value lhs = mLhs.eval(state);
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                "Identifier \"" + mLhs + "\" is undefined."
            );
        }
        // evaluate all arguments
        final List<Value> args = new ArrayList<Value>(mArgs.size());
        for (final Expr arg: mArgs) {
            if (arg != null) {
                args.add(arg.eval(state).clone());
            }
        }
        // execute
        return lhs.collectionAccess(state, args);
    }

    private Value evaluateUnCloned(final State state) throws SetlException {
        Value lhs = null;
        if (mLhs instanceof Variable) {
            lhs = mLhs.eval(state);
        } else if (mLhs instanceof CollectionAccess) {
            lhs = ((CollectionAccess) mLhs).evaluateUnCloned(state);
        } else {
            throw new IncompatibleTypeException(
                "\"" + this + "\" is unusable for list assignment."
            );
        }
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                "Identifier \"" + mLhs + "\" is undefined."
            );
        }
        // evaluate all arguments
        final List<Value> args = new ArrayList<Value>(mArgs.size());
        for (final Expr arg: mArgs) {
            if (arg != null) {
                args.add(arg.eval(state).clone());
            }
        }
        // execute
        return lhs.collectionAccessUnCloned(state, args);
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
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        mLhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        for (final Expr expr : mArgs) {
            expr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    // sets this expression to the given value
    @Override
    public void assignUncloned(final State state, final Value v) throws SetlException {
        Value lhs = null;
        if (mLhs instanceof Variable) {
            lhs = mLhs.eval(state);
            if (lhs == Om.OM) {
                throw new UnknownFunctionException(
                    "Identifier \"" + mLhs + "\" is undefined."
                );
            }
        } else if (mLhs instanceof CollectionAccess) {
            lhs = ((CollectionAccess) mLhs).evaluateUnCloned(state);
        }
        if (lhs != null && lhs instanceof CollectionValue && mArgs.size() == 1) {
            lhs.setMember(state, mArgs.get(0).eval(state), v);
        } else {
            throw new IncompatibleTypeException(
                "Left-hand-side of \"" + mLhs + " := " + v + "\" is unusable for list assignment."
            );
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        mLhs.appendString(state, sb, tabs);
        sb.append("[");

        final Iterator<Expr> iter = mArgs.iterator();
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
        final Term        result      = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, mLhs.toTerm(state));

        final SetlList    arguments   = new SetlList(mArgs.size());
        for (final Expr arg: mArgs) {
            arguments.addMember(state, arg.toTerm(state));
        }
        result.addMember(state, arguments);

        return result;
    }

    @Override
    public Term toTermQuoted(final State state) throws SetlException {
        final Term        result      = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, mLhs.toTermQuoted(state));

        final SetlList    arguments   = new SetlList(mArgs.size());
        for (final Expr arg: mArgs) {
            arguments.addMember(state, arg.eval(state).toTerm(state));
        }
        result.addMember(state, arguments);

        return result;
    }

    public static CollectionAccess termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr        lhs     = TermConverter.valueToExpr(term.firstMember());
            final SetlList    argsLst = (SetlList) term.lastMember();
            final List<Expr>  args    = new ArrayList<Expr>(argsLst.size());
            for (final Value v : argsLst) {
                args.add(TermConverter.valueToExpr(v));
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


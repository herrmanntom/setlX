package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
grammar rule:

factor
    : [..]
    | ('(' expr ')' | procedureDefinition | variable | value) call '!'?
    ;

call
    :                                                         ('(' callParameters ')' | [..])*
    ;

implemented here as:
       =====================================================       ==============
                                mLhs                                  mArgs
*/

public class Call extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String     FUNCTIONAL_CHARACTER = "^call";
    // precedence level in SetlX-grammar
    private final static int        PRECEDENCE           = 2100;

    private final Expr       mLhs;  // left hand side
    private final List<Expr> mArgs; // list of arguments
    private final String     _this; // pre-computed toString(), which has less stack penalty in case of stack overflow error...

    public Call(final Expr lhs, final List<Expr> args) {
        mLhs    = lhs;
        mArgs   = args;
        _this   = toString();
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        final Value lhs = mLhs.eval(state);
        final boolean finishOuterFunction = state.isDebugFinishFunction;
        try {
            if (state.areBreakpointsEnabled && ! state.isDebugPromptActive()) {
                if (state.isBreakpoint(mLhs.toString())) {
                    state.setDebugModeActive(true);
                }
            }
            if (finishOuterFunction) { // unset, because otherwise it would be reset when this call returns
                state.setDebugFinishFunction(false);
            }
            // supply the original expressions (mArgs), which are needed for 'rw' parameters
            return lhs.call(state, mArgs);
        } catch (final StackOverflowError e) {
            throw new JVMException(
                "Stack overflow.\n" +
                "Try preventing recursion and/or execute with larger stack size.\n" +
                "(use '-Xss<size>' parameter for java loader, where <size> is like '32m')"
            );
        } finally {
            if (finishOuterFunction) {
                state.setDebugFinishFunction(true);
            }
        }
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
        // add dummy variable to prevent optimization, behavior of called function is unknown here!
        unboundVariables.add(Variable.PREVENT_OPTIMIZATION_DUMMY);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        if (_this != null && tabs == 0 && ! state.isPrintVerbose()) {
            sb.append(_this);
        } else {
            mLhs.appendString(state, sb, tabs);
            sb.append("(");

            final Iterator<Expr> iter = mArgs.iterator();
            while (iter.hasNext()) {
                iter.next().appendString(state, sb, 0);
                if (iter.hasNext()) {
                    sb.append(", ");
                }
            }

            sb.append(")");
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term      result      = new Term(FUNCTIONAL_CHARACTER, 2);

        if (mLhs instanceof Variable) {
            result.addMember(state, new SetlString(mLhs.toString()));
        } else {
            result.addMember(state, mLhs.toTerm(state));
        }

        final SetlList  arguments   = new SetlList(mArgs.size());
        for (final Expr arg: mArgs) {
            arguments.addMember(state, arg.toTerm(state));
        }
        result.addMember(state, arguments);

        return result;
    }

    @Override
    public Term toTermQuoted(final State state) throws SetlException {
        final Term      result      = new Term(FUNCTIONAL_CHARACTER, 2);

        if (mLhs instanceof Variable) {
            result.addMember(state, new SetlString(mLhs.toString()));
        } else {
            result.addMember(state, mLhs.toTerm(state));
        }

        final SetlList  arguments   = new SetlList(mArgs.size());
        for (final Expr arg: mArgs) {
            arguments.addMember(state, arg.eval(state).toTerm(state));
        }
        result.addMember(state, arguments);

        return result;
    }

    public static Call termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Value       lhsTerm = term.firstMember();
            final Expr        lhs;
            if (lhsTerm instanceof SetlString) {
                lhs = new Variable(lhsTerm.getUnquotedString());
            } else {
                lhs = TermConverter.valueToExpr(lhsTerm);
            }
            final SetlList    argsLst = (SetlList) term.lastMember();
            final List<Expr>  args    = new ArrayList<Expr>(argsLst.size());
            for (final Value v : argsLst) {
                args.add(TermConverter.valueToExpr(v));
            }
            return new Call(lhs, args);
        }
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}


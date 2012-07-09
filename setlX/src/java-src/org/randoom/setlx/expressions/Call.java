package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
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
      ========      ==============
        mLhs             mArgs
*/

public class Call extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String     FUNCTIONAL_CHARACTER = "^call";
    // precedence level in SetlX-grammar
    private final static int        PRECEDENCE           = 2100;
    // are any breakpoints set? MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean    sBreakpointsEnabled  = false;
    // continue execution of function, which includes this call, in debug mode until it returns. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean    sFinishOuterFunction = false;

    private final Variable   mLhs;  // left hand side
    private final List<Expr> mArgs; // list of arguments
    private final String     _this; // pre-computed toString(), which has less stack penalty in case of stack overflow error...

    public Call(final Variable lhs, final List<Expr> args) {
        mLhs    = lhs;
        mArgs   = args;
        _this   = toString();
    }

    protected Value evaluate() throws SetlException {
        final Value lhs = mLhs.eval();
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                "Identifier \"" + mLhs + "\" is undefined."
            );
        }
        boolean finishOuterFunction = sFinishOuterFunction;
        try {
            if (sBreakpointsEnabled && ! Environment.isDebugPromptActive()) {
                if (Environment.isBreakpoint(mLhs.toString())) {
                    Environment.setDebugModeActive(true);
                }
            }
            if (finishOuterFunction) { // unset, because otherwise it would be reset when this call returns
                Environment.setDebugFinishFunction(false);
            }
            // also supply the original expressions (mArgs), which are needed for 'rw' parameters
            return lhs.call(mArgs);
        } catch (StackOverflowError e) {
            throw new JVMException(
                "Stack overflow.\n" +
                "Try preventing recursion and/or execute with larger stack size.\n" +
                "(use '-Xss<size>' parameter for java loader, where <size> is like '32m')"
            );
        } finally {
            if (finishOuterFunction) {
                Environment.setDebugFinishFunction(true);
            }
        }
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        if (_this != null && tabs == 0 && ! Environment.isPrintVerbose()) {
            sb.append(_this);
        } else {
            mLhs.appendString(sb, tabs);
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
    }

    /* term operations */

    public Term toTerm() {
        final Term      result      = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(new SetlString(mLhs.toString()));

        final SetlList  arguments   = new SetlList(mArgs.size());
        for (final Expr arg: mArgs) {
            arguments.addMember(arg.toTerm());
        }
        result.addMember(arguments);

        return result;
    }

    public Term toTermQuoted() throws SetlException {
        final Term      result      = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(new SetlString(mLhs.toString()));

        final SetlList  arguments   = new SetlList(mArgs.size());
        for (Expr arg: mArgs) {
            arguments.addMember(arg.eval().toTerm());
        }
        result.addMember(arguments);

        return result;
    }

    public static Call termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlString && term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final String      lhs     = ((SetlString) term.firstMember()).getUnquotedString();
            final SetlList    argsLst = (SetlList) term.lastMember();
            final List<Expr>  args    = new ArrayList<Expr>(argsLst.size());
            for (final Value v : argsLst) {
                args.add(TermConverter.valueToExpr(v));
            }
            return new Call(new Variable(lhs), args);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


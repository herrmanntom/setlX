package interpreter.expressions;

import interpreter.exceptions.JVMException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UnknownFunctionException;
import interpreter.types.Om;
import interpreter.types.RangeDummy;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
call
    : variable ('(' callParameters ')' | '{' anyExpr '}')*
    ;

implemented here as:
      =========      ==============
         mLhs            mArgs
*/

public class Call extends Expr {
    private Expr       mLhs;       // left hand side (function name, other call, variable, etc)
    private List<Expr> mArgs;      // list of arguments
    private boolean    isRange;    // true if mArgs contains 'CallRangeDummy.CRD', which represents '..'
    private String     _this;      // pre-computed toString(), which has less stack penalty in case of stack overflow error...

    public Call(Expr lhs, List<Expr> args) {
        mLhs            = lhs;
        mArgs           = args;
        isRange         = mArgs.contains(CallRangeDummy.CRD);
        _this           = _toString(0);
    }

    public Value evaluate() throws SetlException {
        Value lhs = mLhs.eval();
        if (lhs == Om.OM) {
            throw new UnknownFunctionException("Identifier \"" + mLhs + "\" is undefined.");
        }
        // evaluate all arguments
        List<Value> args = new ArrayList<Value>(mArgs.size());
        for (Expr arg: mArgs) {
            if (arg == CallRangeDummy.CRD) {
                args.add(RangeDummy.RD);
            } else if (arg != null) {
                args.add(arg.eval().clone());
            }
        }
        try {
            // but also supply the original expressions, which are needed for 'rw' parameters
            return lhs.call(mArgs, args);
        } catch (StackOverflowError e) {
            throw new JVMException("Stack overflow.\n"
                                 + "Try preventing recursion and/or execute with larger stack size.\n"
                                 + "(use '-Xss<size>' parameter for java loader, where <size> is like '10m')");
        }
    }

    /* string operations */

    public String toString(int tabs) {
        if (tabs == 0 && ! Environment.isPrintVerbose()) {
            return _this;
        } else {
            return _toString(tabs);
        }
    }

    public String _toString(int tabs) {
        String result = mLhs.toString(tabs) + "(";
        for (int i = 0; i < mArgs.size(); ++i) {
            if (i > 0 && !isRange) {
                result += ", ";
            }
            result += mArgs.get(i).toString(tabs);
        }
        result += ")";
        return result;
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term        result      = new Term("'call");
        SetlList    arguments   = new SetlList();
        result.addMember(mLhs.toTerm());
        result.addMember(arguments);
        for (Expr arg: mArgs) {
            arguments.addMember(arg.toTerm());
        }
        return result;
    }
}


package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.JVMException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UnknownFunctionException;
import interpreter.functions.PreDefinedFunction;
import interpreter.types.RangeDummy;
import interpreter.types.SetlOm;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.ArrayList;
import java.util.List;

public class Call extends Expr {
    private Expr       mLhs;              // left hand side (function name, variable, etc)
    private List<Expr> mArgs;             // list of arguments
    private boolean    mReturnCollection; // true if arguments enclosed with { }
    private boolean    isRange;           // true if mArgs contains 'CallRangeDummy.CRD', which represents '..'
    private String     _this;             // pre-computed toString() which has less stack penalty in case of stack overflow error...

    public Call(Expr lhs, List<Expr> args, boolean returnCollection) {
        mLhs              = lhs;
        mArgs             = args;
        mReturnCollection = returnCollection;
        isRange           = mArgs.contains(CallRangeDummy.CRD);
        _this             = _toString();
    }

    public Value evaluate() throws SetlException {
        Value lhs = mLhs.eval();
        if (lhs == SetlOm.OM) {
            throw new UnknownFunctionException("Identifier `" + mLhs + "´ is undefined.");
        }
        List<Value> args = new ArrayList<Value>(mArgs.size());
        if (lhs instanceof PreDefinedFunction && ((PreDefinedFunction) lhs).writeVars()) {
            for (Expr arg: mArgs) {
                if (arg instanceof Variable) {
                    args.add(new SetlString(((Variable) arg).getId()));
                } else {
                    throw new IncompatibleTypeException("Only variables are allowed as parameters for get() and read().");
                }
            }
        } else {
            for (Expr arg: mArgs) {
                if (arg == CallRangeDummy.CRD) {
                    args.add(RangeDummy.RD);
                } else if (arg != null) {
                    args.add(arg.eval().clone());
                }
            }
        }
        try {
            return lhs.call(args, mReturnCollection);
        } catch (StackOverflowError e) {
            throw new JVMException("Stack overflow.\n"
                                 + "Try preventing recursion and/or execute with larger stack size.\n"
                                 + "(use '-Xss<size>' parameter for java loader, where <size> is like '10m')");
        }
    }

    public String toString() {
        return _this;
    }

    public String _toString() {
        String result = mLhs + ((mReturnCollection)? "{" : "(");
        for (int i = 0; i < mArgs.size(); ++i) {
            if (i > 0 && !isRange) {
                result += ", ";
            }
            result += mArgs.get(i);
        }
        result += (mReturnCollection)? "}" : ")";
        return result;
    }
}


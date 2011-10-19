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
    private Expr       mLhs;       // left hand side (function name, other call, variable, etc)
    private List<Expr> mArgs;      // list of arguments
    private boolean    isRange;    // true if mArgs contains 'CallRangeDummy.CRD', which represents '..'
    private String[]   _this;      // pre-computed toString() with various tab-sizes which has less stack penalty in case of stack overflow error...

    public Call(Expr lhs, List<Expr> args) {
        mLhs              = lhs;
        mArgs             = args;
        isRange           = mArgs.contains(CallRangeDummy.CRD);
        _this             = new String[4];
        for (int i = 0; i < _this.length; ++i) {
            _this[i] = _toString(i);
        }
    }

    public Value evaluate() throws SetlException {
        Value lhs = mLhs.eval();
        if (lhs == SetlOm.OM) {
            throw new UnknownFunctionException("Identifier `" + mLhs + "´ is undefined.");
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

    public String toString(int tabs) {
        if (tabs < _this.length) {
            return _this[tabs];
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
}


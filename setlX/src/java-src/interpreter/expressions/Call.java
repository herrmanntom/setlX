package interpreter.expressions;

import interpreter.exceptions.JVMException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.exceptions.UnknownFunctionException;
import interpreter.types.Om;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.TermConverter;

import java.util.ArrayList;
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
    private final static String FUNCTIONAL_CHARACTER = "'call";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private Expr       mLhs;       // left hand side (only variable is allowed here!)
    private List<Expr> mArgs;      // list of arguments
    private String     _this;      // pre-computed toString(), which has less stack penalty in case of stack overflow error...

    public Call(Expr lhs, List<Expr> args) {
        mLhs            = lhs;
        mArgs           = args;
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
            if (arg != null) {
                args.add(arg.eval().clone());
            }
        }
        try {
            // also supply the original expressions (mArgs), which are needed for 'rw' parameters
            return lhs.call(mArgs, args);
        } catch (StackOverflowError e) {
            throw new JVMException("Stack overflow.\n"
                                 + "Try preventing recursion and/or execute with larger stack size.\n"
                                 + "(use '-Xss<size>' parameter for java loader, where <size> is like '32m')");
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
            if (i > 0) {
                result += ", ";
            }
            result += mArgs.get(i).toString(tabs);
        }
        result += ")";
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term        result      = new Term(FUNCTIONAL_CHARACTER);
        SetlList    arguments   = new SetlList();
        result.addMember(mLhs.toTerm());
        result.addMember(arguments);
        for (Expr arg: mArgs) {
            arguments.addMember(arg.toTerm());
        }
        return result;
    }

    public static Call termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr        lhs     = TermConverter.valueToExpr(term.firstMember());
            SetlList    argsLst = (SetlList) term.lastMember();
            List<Expr>  args    = new ArrayList<Expr>(argsLst.size());
            for (Value v : argsLst) {
                args.add(TermConverter.valueToExpr(v));
            }
            return new Call(lhs, args);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


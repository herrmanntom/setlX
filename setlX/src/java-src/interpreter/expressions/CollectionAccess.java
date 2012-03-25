package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.exceptions.UnknownFunctionException;
import interpreter.types.CollectionValue;
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
      =========                               ======================
         mLhs                                          mArgs
*/

public class CollectionAccess extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^collectionAccess";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private Expr       mLhs;       // left hand side (Variable, CollectMap, other CollectionAccess, etc)
    private List<Expr> mArgs;      // list of arguments
    private int        mLineNr;

    public CollectionAccess(Expr lhs, Expr arg) {
        this(lhs, new ArrayList<Expr>(1));
        mArgs.add(arg);
    }

    public CollectionAccess(Expr lhs, List<Expr> args) {
        mLhs    = lhs;
        mArgs   = args;
        mLineNr = -1;
    }

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = Environment.sourceLine;
        mLhs.computeLineNr();
        for (Expr arg : mArgs) {
            arg.computeLineNr();
        }
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
        // execute
        return lhs.collectionAccess(args);
    }

    private Value evaluateUnCloned() throws SetlException {
        Value lhs = null;
        if (mLhs instanceof Variable) {
            lhs = mLhs.eval();
        } else if (mLhs instanceof CollectionAccess) {
            lhs = ((CollectionAccess) mLhs).evaluateUnCloned();
        } else {
            throw new IncompatibleTypeException("\"" + this + "\" is unusable for list assignment.");
        }
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
        // execute
        return lhs.collectionAccessUnCloned(args);
    }

    // sets this expression to the given value
    public Value assign(Value v) throws SetlException {
        Value lhs = null;
        if (mLhs instanceof Variable) {
            lhs = mLhs.eval();
            if (lhs == Om.OM) {
                throw new UnknownFunctionException("Identifier \"" + mLhs + "\" is undefined.");
            }
        } else if (mLhs instanceof CollectionAccess) {
            lhs = ((CollectionAccess) mLhs).evaluateUnCloned();
        }
        if (lhs != null && lhs instanceof CollectionValue && mArgs.size() == 1) {
            lhs.setMember(mArgs.get(0).eval(), v); // no v.clone() here, because setMember() already clones
            return v.clone();
        } else {
            throw new IncompatibleTypeException("Left-hand-side of \"" + mLhs + " := " + v + "\" is unusable for list assignment.");
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = mLhs.toString(tabs) + "[";
        for (int i = 0; i < mArgs.size(); ++i) {
            if (i > 0) {
                result += " ";
            }
            result += mArgs.get(i).toString(tabs);
        }
        result += "]";
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

    public static CollectionAccess termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr        lhs     = TermConverter.valueToExpr(term.firstMember());
            SetlList    argsLst = (SetlList) term.lastMember();
            List<Expr>  args    = new ArrayList<Expr>(argsLst.size());
            for (Value v : argsLst) {
                args.add(TermConverter.valueToExpr(v));
            }
            return new CollectionAccess(lhs, args);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}


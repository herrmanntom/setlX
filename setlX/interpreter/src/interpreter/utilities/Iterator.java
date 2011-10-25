package interpreter.utilities;

import interpreter.exceptions.ContinueException;
import interpreter.exceptions.BreakException;
import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.CollectionValue;
import interpreter.types.Value;

public class Iterator {
    private Expr                mLhs;    // Lhs is a simple variable or a list (hopefully only of (lists of) variables)
    private Expr                mRhs;    // Rhs (should be Set/List)
    private Iterator            mNext;   // next iterator

    public Iterator(Expr lhs, Expr rhs) {
        mLhs  = lhs;
        mRhs  = rhs;
        mNext = null;
    }

    // adds next iterator to end of current iterator chain
    public void add(Iterator i) {
        if (mNext == null) {
            mNext = i;
        } else {
            mNext.add(i);
        }
    }

    /* executes container in environment created by this iteration
       note: resets to outer environment after its finished!
       note: each iterator introduces a new environment to allow local variables */
    public void eval(IteratorExecutionContainer exec) throws SetlException {
        SetlException ex       = null;
        Environment   outerEnv = Environment.getEnv();
        try {
            evaluate(exec);
        } catch (BreakException ee) {
            // throwing the exception already broke the loop
            // nothing to be done, not a real error
        } catch (SetlException e) {
            ex = e;
        }
        Environment.setEnv(outerEnv); // make sure env is always reset
        if (ex != null) {
            throw ex;
        }
    }

    public String toString(int tabs) {
        String result = mLhs.toString(tabs) + " in " + mRhs.toString(tabs);
        if (mNext != null) {
            result += ", " + mNext.toString(tabs);
        }
        return result;
    }

    public String toString() {
        return toString(0);
    }

    private void evaluate(IteratorExecutionContainer exec) throws SetlException {
        Value iterationValue = mRhs.eval(); // trying to iterate over this value
        if (iterationValue instanceof CollectionValue) {
            CollectionValue coll     = (CollectionValue) iterationValue;
            // environment for inner execution/next iterator
            Environment     innerEnv = Environment.getEnv().createInteratorBlock();
            // iterate over items
            for (Value v: coll) {
                // restore inner environment
                Environment.setEnv(innerEnv);
                innerEnv.setWriteThrough(false); // force iteration variables to be local to this block
                // assign value from collection
                mLhs.assign(v);
                // reset WriteThrough, because changes during execution are not strictly local
                innerEnv.setWriteThrough(true);
                /* Starts iteration of next iterator or execution if this is the
                   last iterator.
                   Stops iteration if requested by execution.
                   Note: BreakException breaks the loop and is handled by eval() */
                try {
                    if (mNext != null) {
                        mNext.evaluate(exec);
                    } else {
                        exec.execute(v);
                    }
                } catch (ContinueException ce) {
                    continue;
                }
            }
        } else {
            throw new IncompatibleTypeException("Evaluation of iterator `" + iterationValue + "´ is not a compound value.");
        }
    }
}


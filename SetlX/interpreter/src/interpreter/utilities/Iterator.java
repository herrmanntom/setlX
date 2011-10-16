package interpreter.utilities;

import interpreter.exceptions.ContinueException;
import interpreter.exceptions.BreakException;
import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;
import interpreter.expressions.SetTupleConstructor;
import interpreter.types.CollectionValue;
import interpreter.types.SetlOm;
import interpreter.types.SetlTuple;
import interpreter.types.Value;

public class Iterator {
    private String              mId;
    private SetTupleConstructor mTupleConstructor;
    private Expr                mExpr;
    private Iterator            mNext;

    public Iterator(String id, SetTupleConstructor tupleConstructor, Expr expr) {
        mId               = id;
        mTupleConstructor = tupleConstructor;
        mExpr             = expr;
        mNext             = null;
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
        SetlException ex = null;
        Environment outerEnv = Environment.getEnv();
        try {
            evaluate(exec);
        } catch (BreakException ee) {
            // nothing to be done, not a real error
        } catch (SetlException e) {
            ex = e;
        }
        Environment.setEnv(outerEnv); // make sure env is always reset
        if (ex != null) {
            throw ex;
        }
    }

    // sets all variables used in this iterator to OM
    public void setToOm() throws UndefinedOperationException {
        if (mNext != null) {
            mNext.setToOm();
        }
        if (mId != null) {
            Environment.putValue(mId, SetlOm.OM);
        } else if (mTupleConstructor != null) {
            mTupleConstructor.setIdsToOm();
        } else {
            throw malformedError();
        }
    }

    public String toString() {
        String r = "";
        if (mId != null) {
            r = mId;
        } else if (mTupleConstructor != null) {
            r = mTupleConstructor.toString();
        }
        r += " in " + mExpr;
        if (mNext != null) {
            r += ", " + mNext;
        }
        return r;
    }

    private void evaluate(IteratorExecutionContainer exec) throws SetlException {
        Value iterationValue = mExpr.eval(); // trying to iterate over this value
        if (iterationValue instanceof CollectionValue) {
            CollectionValue  coll    = (CollectionValue) iterationValue;
            // environment for inner execution/next iterator
            Environment     innerEnv = Environment.getEnv().createInteratorBlock();
            // iterate over items
            for (Value v: coll) {
                // restore inner environment
                Environment.setEnv(innerEnv);
                innerEnv.setWriteThrough(false); // force iteration variables to be local to this block
                if (mId != null) { // single variable as target
                    Environment.putValue(mId, v);
                } else if (mTupleConstructor != null) { // tuple as target
                    if (v instanceof SetlTuple) {
                        if (mTupleConstructor.setIds((SetlTuple) v)) {
                            // tuple extraction successful
                        } else {
                            throw membersUnusableError(coll);
                        }
                    } else {
                        throw membersUnusableError(coll);
                    }
                } else {
                    throw malformedError();
                }
                // reset, because changes during execution are not strictly local
                innerEnv.setWriteThrough(true);
                /* Starts iteration of next iterator or execution if this is the
                   last iterator.
                   Stops iteration if requested by execution.
                   Note: ExitException breaks the loop and is handled by eval() */
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

    private UndefinedOperationException malformedError() {
        return new UndefinedOperationException("Tuple/Set iteration is malformed.");
    }

    private IncompatibleTypeException membersUnusableError(Value v) {
        return new IncompatibleTypeException("Members of `" + v + "´ are unusable for tuple extraction.");
    }
}



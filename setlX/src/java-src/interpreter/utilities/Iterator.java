package interpreter.utilities;

import interpreter.exceptions.ContinueException;
import interpreter.exceptions.BreakException;
import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.CollectionValue;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;

/*
grammar rule:
iteratorChain
    : iterator                       (',' iterator)*
    ;

iterator
    : assignable 'in' expr
    ;

implemented here as:
      ==========      ====        ||      ========
      mAssignable  mCollection    ||       mNext
*/

public class Iterator {
    private Expr        mAssignable; // Lhs is a simple variable or a list (hopefully only of (lists of) variables)
    private Expr        mCollection; // Rhs (should be Set/List)
    private Iterator    mNext;       // next iterator in iteratorChain

    public Iterator(Expr assignable, Expr collection) {
        mAssignable = assignable;
        mCollection = collection;
        mNext       = null;
    }

    // adds next iterator to end of current iterator chain
    public void add(Iterator i) {
        if (mNext == null) {
            mNext = i;
        } else {
            mNext.add(i);
        }
    }

    /* executes container in scope created by this iteration
       note: resets to outer scope after iteration is finished!
       note: each iterator introduces a new scope to allow its iteration
             variable to be local
       note: variables inside the whole iteration are not _not_ local
             all will be written `through' these inner scopes                 */
    public void eval(IteratorExecutionContainer exec) throws SetlException {
        VariableScope   outerScope  = VariableScope.getScope();
        try {
            evaluate(exec);
        } catch (BreakException ee) {
            // throwing the exception already broke the loop
            // nothing to be done, not a real error
        } finally { // make sure scope is always reset
            VariableScope.setScope(outerScope);
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = mAssignable.toString(tabs) + " in " + mCollection.toString(tabs);
        if (mNext != null) {
            result += ", " + mNext.toString(tabs);
        }
        return result;
    }

    public String toString() {
        return toString(0);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'iterator");
        result.addMember(mAssignable.toTerm());
        result.addMember(mCollection.toTerm());
        if (mNext != null) {
            result.addMember(mNext.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }
        return result;
    }

    /* private functions */

    private void evaluate(IteratorExecutionContainer exec) throws SetlException {
        Value iterationValue = mCollection.eval(); // trying to iterate over this value
        if (iterationValue instanceof CollectionValue) {
            CollectionValue coll        = (CollectionValue) iterationValue;
            // scope for inner execution/next iterator
            VariableScope   innerScope  = VariableScope.getScope().createInteratorBlock();
            // iterate over items
            for (Value v: coll) {
                // restore inner scope
                VariableScope.setScope(innerScope);
                innerScope.setWriteThrough(false); // force iteration variables to be local to this block
                // assign value from collection
                mAssignable.assign(v);
                // reset WriteThrough, because changes during execution are not strictly local
                innerScope.setWriteThrough(true);
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
            throw new IncompatibleTypeException("Evaluation of iterator '" + iterationValue + "' is not a compound value.");
        }
    }
}


package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;

public class WriteBackAgent {

    private List<Expr>  mExpressions;
    private List<Value> mValues;

    public WriteBackAgent(int size) {
        mExpressions = new ArrayList<Expr>(size);
        mValues      = new ArrayList<Value>(size);
    }

    public void add(Expr expression, Value value) {
        mExpressions.add(expression);
        mValues.add(value);
    }

    /* This functions tries to writes the stored values into the variables used
       in expressions to initialize the parameters of the previously executed
       SetlDefinition. Hereby 'rw' parameters are possible without pointers...
       Two types of expressions are supported:
         simple variables
       and
         lists of (lists of) simple variables
       If the expressions used are more complex or it is otherwise not possible
       to write the values back, the current pair of expr+value is ignored. */
    public void writeBack() {
        for (int i = 0; i < mExpressions.size(); ++i) {
            Expr  expr  = mExpressions.get(i);
            Value value = mValues.get(i).clone();
            try {
                expr.assign(value);
            } catch (SetlException se) {
                // assignment failed => just ignore it
            }
        }
    }
}


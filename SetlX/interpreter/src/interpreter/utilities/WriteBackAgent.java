package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.expressions.SetListConstructor;
import interpreter.expressions.Variable;
import interpreter.types.SetlList;
import interpreter.types.Value;

import java.util.LinkedList;
import java.util.List;

public class WriteBackAgent {

    private List<Expr>  mExpressions;
    private List<Value> mValues;

    public WriteBackAgent() {
        mExpressions = new LinkedList<Expr>();
        mValues      = new LinkedList<Value>();
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
       to write the values back, the current pair of expr+value us ignored. */
    public void writeBack() {
        for (int i = 0; i < mExpressions.size(); ++i) {
            Expr  expr  = mExpressions.get(i);
            Value value = mValues.get(i).clone();
            if (expr instanceof Variable) {
                Variable var = (Variable) expr;
                Environment.putValue(var.getId(), value);
            } else if (expr  instanceof SetListConstructor &&
                       value instanceof SetlList              )
            {
                SetListConstructor lc = (SetListConstructor) expr;
                try {
                    lc.setIds((SetlList) value);
                } catch (SetlException se) {
                    // list assignment failed => just ignore it
                }
            }
        }
    }
}

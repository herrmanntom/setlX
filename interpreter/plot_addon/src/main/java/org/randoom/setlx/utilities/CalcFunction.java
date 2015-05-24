package org.randoom.setlx.utilities;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;

public class CalcFunction {

    String function = "x";
    State state = new State();

    public CalcFunction(String function) {
        this.setFunction(function);
    }

    /**
     * evaluates the value of the function with the given parameter using setlx interpreter
     *
     * @param x parameter given to the (math.) function
     * @return
     */
    public Double calcYfromX(Double x) throws SetlException {
        String s = x.toString();
        String localFunction = function.replace("x", s);

        Expr expr = ParseSetlX.parseStringToExpr(state, localFunction);
        Value v = expr.eval(state);
        if (v.isDouble().equalTo(SetlBoolean.TRUE)) {
            return v.jDoubleValue();
        }
        if (v.isInteger().equalTo(SetlBoolean.TRUE)) {
            return (double) v.jIntValue();
        }


        return null;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}

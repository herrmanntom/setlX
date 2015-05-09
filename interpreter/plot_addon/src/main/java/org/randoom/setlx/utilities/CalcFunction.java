package org.randoom.setlx.utilities;


import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Value;

public class CalcFunction {

    String function = "x";
    State state = new State();

    public CalcFunction(String function) {
        this.setFunction(function);
    }

    public Double calcYfromX(Double x) {
        String s = x.toString();
        String localFunction = function.replace("x", s);
        try {
            Expr expr = ParseSetlX.parseStringToExpr(state, localFunction);
            Value v = expr.eval(state);
            return v.jDoubleValue();
        } catch (Exception e) {
            p(e.toString());
        }
        return null;
    }

    public void p(String s) {
        System.out.println(s);
    }

    public void setFunction(String function) {
        this.function = function;
    }
}

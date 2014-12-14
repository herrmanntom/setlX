package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operators.*;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.utilities.State;
import org.randoom.setlxUI.pc.PcEnvProvider;

import java.util.ArrayList;

public class OperatorTest {
    public final static void main(String args[]) throws SetlException {
        final State state = new State(new PcEnvProvider(""));

        // 10 + 20 = 30
        {
            ArrayList<AOperator> operators = new ArrayList<AOperator>();
            operators.add(new ValueOperator(Rational.valueOf(10)));
            operators.add(new ValueOperator(Rational.valueOf(20)));
            operators.add(new Sum());
            evaluate(state, operators);
        }

        // 10 + 20 * 2 = 50
        {
            ArrayList<AOperator> operators = new ArrayList<AOperator>();
            operators.add(new ValueOperator(Rational.valueOf(10)));
            operators.add(new ValueOperator(Rational.valueOf(20)));
            operators.add(new ValueOperator(Rational.valueOf(2)));
            operators.add(new Product());
            operators.add(new Sum());
            evaluate(state, operators);
        }

        // 10 * 2 + 20 = 40
        {
            ArrayList<AOperator> operators = new ArrayList<AOperator>();
            operators.add(new ValueOperator(Rational.valueOf(10)));
            operators.add(new ValueOperator(Rational.valueOf(2)));
            operators.add(new Product());
            operators.add(new ValueOperator(Rational.valueOf(20)));
            operators.add(new Sum());
            evaluate(state, operators);
        }

        // 10 * -2 + 20 = 0
        {
            ArrayList<AOperator> operators = new ArrayList<AOperator>();
            operators.add(new ValueOperator(Rational.valueOf(10)));
            operators.add(new ValueOperator(Rational.valueOf(2)));
            operators.add(new Minus());
            operators.add(new Product());
            operators.add(new ValueOperator(Rational.valueOf(20)));
            operators.add(new Sum());
            evaluate(state, operators);
        }

        // (10 + 2) * 20 = 240
        {
            ArrayList<AOperator> operators = new ArrayList<AOperator>();
            operators.add(new ValueOperator(Rational.valueOf(10)));
            operators.add(new ValueOperator(Rational.valueOf(2)));
            operators.add(new Sum());
            operators.add(new ValueOperator(Rational.valueOf(20)));
            operators.add(new Product());
            evaluate(state, operators);
        }

        // 10 - 5 + 3 = 8
        {
            ArrayList<AOperator> operators = new ArrayList<AOperator>();
            operators.add(new ValueOperator(Rational.valueOf(10)));
            operators.add(new ValueOperator(Rational.valueOf(5)));
            operators.add(new Difference());
            operators.add(new ValueOperator(Rational.valueOf(3)));
            operators.add(new Sum());
            evaluate(state, operators);
        }

        // 10 - (5 + 3) = 2
        {
            ArrayList<AOperator> operators = new ArrayList<AOperator>();
            operators.add(new ValueOperator(Rational.valueOf(10)));
            operators.add(new ValueOperator(Rational.valueOf(5)));
            operators.add(new ValueOperator(Rational.valueOf(3)));
            operators.add(new Sum());
            operators.add(new Difference());
            evaluate(state, operators);
        }

        // 2 - 5 - 3 = -6
        {
            ArrayList<AOperator> operators = new ArrayList<AOperator>();
            operators.add(new ValueOperator(Rational.valueOf(2)));
            operators.add(new ValueOperator(Rational.valueOf(5)));
            operators.add(new Difference());
            operators.add(new ValueOperator(Rational.valueOf(3)));
            operators.add(new Difference());
            evaluate(state, operators);
        }

        // 2 - (5 - 3) = -1
        {
            ArrayList<AOperator> operators = new ArrayList<AOperator>();
            operators.add(new ValueOperator(Rational.valueOf(1)));
            operators.add(new ValueOperator(Rational.valueOf(5)));
            operators.add(new ValueOperator(Rational.valueOf(3)));
            operators.add(new Difference());
            operators.add(new Difference());
            evaluate(state, operators);
        }
    }

    private static void evaluate(State state, ArrayList<AOperator> operators) throws SetlException {
        OperatorExpression operatorExpression = new OperatorExpression(operators);

        StringBuilder sb = new StringBuilder();
        operatorExpression.appendString(state, sb, 0);
        sb.append(" = ");
        operatorExpression.evaluate(state).appendString(state, sb, 0);

        state.outWriteLn(sb.toString());
    }
}

package org.randoom.setlx.operatorUtilities;

import org.junit.Before;
import org.junit.Test;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operators.*;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("JavaDoc")
public class OperatorExpressionTest {
    private State state;

    @Before
    public void testSetup() {
        state = new State();
    }

    @Test
    public void givenSumExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        ArrayList<AOperator> operators = new ArrayList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 20);
        operators.add(new Sum());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals(result.getExpression(), "10 + 20");
        assertEquals(result.getResultAsInt(), 30);
    }

    @Test
    public void givenSumAndMultiplicationExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        ArrayList<AOperator> operators = new ArrayList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 20);
        addNumber(operators, 2);
        operators.add(new Product());
        operators.add(new Sum());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals(result.getExpression(), "10 + 20 * 2");
        assertEquals(result.getResultAsInt(), 50);
    }

    @Test
    public void givenMultiplicationAndSumExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        ArrayList<AOperator> operators = new ArrayList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 2);
        operators.add(new Product());
        addNumber(operators, 20);
        operators.add(new Sum());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals(result.getExpression(), "10 * 2 + 20");
        assertEquals(result.getResultAsInt(), 40);
    }

    @Test
    public void givenMultiplicationAndNegationAndSumExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        ArrayList<AOperator> operators = new ArrayList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 2);
        operators.add(new Minus());
        operators.add(new Product());
        addNumber(operators, 20);
        operators.add(new Sum());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals(result.getExpression(), "10 * -2 + 20");
        assertEquals(result.getResultAsInt(), 0);
    }

    @Test
    public void givenBracketedSumAndMultiplicationExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        ArrayList<AOperator> operators = new ArrayList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 2);
        operators.add(new Sum());
        addNumber(operators, 20);
        operators.add(new Product());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals(result.getExpression(), "(10 + 2) * 20");
        assertEquals(result.getResultAsInt(), 240);
    }

    @Test
    public void givenDifferenceAndSumExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        ArrayList<AOperator> operators = new ArrayList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 5);
        operators.add(new Difference());
        addNumber(operators, 3);
        operators.add(new Sum());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals(result.getExpression(), "10 - 5 + 3");
        assertEquals(result.getResultAsInt(), 8);
    }

    @Test
    public void givenDifferenceAndBracketedSumExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        ArrayList<AOperator> operators = new ArrayList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 5);
        addNumber(operators, 3);
        operators.add(new Sum());
        operators.add(new Difference());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals(result.getExpression(), "10 - (5 + 3)");
        assertEquals(result.getResultAsInt(), 2);
    }

    @Test
    public void givenDifferenceAndDifferenceExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        ArrayList<AOperator> operators = new ArrayList<AOperator>();
        addNumber(operators, 1);
        addNumber(operators, 5);
        operators.add(new Difference());
        addNumber(operators, 3);
        operators.add(new Difference());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals(result.getExpression(), "1 - 5 - 3");
        assertEquals(result.getResultAsInt(), -7);
    }

    @Test
    public void givenDifferenceAndBracketedDifferenceExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        ArrayList<AOperator> operators = new ArrayList<AOperator>();
        addNumber(operators, 1);
        addNumber(operators, 5);
        addNumber(operators, 3);
        operators.add(new Difference());
        operators.add(new Difference());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals(result.getExpression(), "1 - (5 - 3)");
        assertEquals(result.getResultAsInt(), -1);
    }

    private boolean addNumber(ArrayList<AOperator> operators, int number) {
        return operators.add(new ValueOperator(Rational.valueOf(number)));
    }

    private EvaluationResult evaluate(ArrayList<AOperator> operators) throws SetlException {
        OperatorExpression operatorExpression = new OperatorExpression(operators);
        return new EvaluationResult(
                operatorExpression.toString(state),
                operatorExpression.evaluate(state)
        );
    }

    private static class EvaluationResult {
        private String expression;
        private Value result;

        public EvaluationResult(String expression, Value result) {
            this.expression = expression;
            this.result = result;
        }

        public String getExpression() {
            return expression;
        }

        public Value getResult() {
            return result;
        }

        public int getResultAsInt() {
            try {
                return getResult().jIntValue();
            } catch (SetlException e) {
                return Integer.MIN_VALUE;
            }
        }
    }
}

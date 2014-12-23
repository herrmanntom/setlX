package org.randoom.setlx.operatorUtilities;

import org.junit.Before;
import org.junit.Test;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operators.*;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
        FragmentList<AOperator> operators = new FragmentList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 20);
        operators.add(new Sum());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals("10 + 20", result.getExpression());
        assertEquals("^sum(10, 20)", result.getExpressionTerm());
        assertEquals(30, result.getResultAsInt());
    }

    @Test
    public void givenSumAndMultiplicationExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        FragmentList<AOperator> operators = new FragmentList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 20);
        addNumber(operators, 2);
        operators.add(new Product());
        operators.add(new Sum());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals("10 + 20 * 2", result.getExpression());
        assertEquals("^sum(10, ^product(20, 2))", result.getExpressionTerm());
        assertEquals(50, result.getResultAsInt());
    }

    @Test
    public void givenMultiplicationAndSumExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        FragmentList<AOperator> operators = new FragmentList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 2);
        operators.add(new Product());
        addNumber(operators, 20);
        operators.add(new Sum());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals("10 * 2 + 20", result.getExpression());
        assertEquals("^sum(^product(10, 2), 20)", result.getExpressionTerm());
        assertEquals(40, result.getResultAsInt());
    }

    @Test
    public void givenMultiplicationAndNegationAndSumExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        FragmentList<AOperator> operators = new FragmentList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 2);
        operators.add(new Minus());
        operators.add(new Product());
        addNumber(operators, 20);
        operators.add(new Sum());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals("10 * -2 + 20", result.getExpression());
        assertEquals("^sum(^product(10, ^minus(2)), 20)", result.getExpressionTerm());
        assertEquals(0, result.getResultAsInt());
    }

    @Test
    public void givenBracketedSumAndMultiplicationExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        FragmentList<AOperator> operators = new FragmentList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 2);
        operators.add(new Sum());
        addNumber(operators, 20);
        operators.add(new Product());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals("(10 + 2) * 20", result.getExpression());
        assertEquals("^product(^sum(10, 2), 20)", result.getExpressionTerm());
        assertEquals(240, result.getResultAsInt());
    }

    @Test
    public void givenDifferenceAndSumExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        FragmentList<AOperator> operators = new FragmentList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 5);
        operators.add(new Difference());
        addNumber(operators, 3);
        operators.add(new Sum());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals("10 - 5 + 3", result.getExpression());
        assertEquals("^sum(^difference(10, 5), 3)", result.getExpressionTerm());
        assertEquals(8, result.getResultAsInt());
    }

    @Test
    public void givenDifferenceAndBracketedSumExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        FragmentList<AOperator> operators = new FragmentList<AOperator>();
        addNumber(operators, 10);
        addNumber(operators, 5);
        addNumber(operators, 3);
        operators.add(new Sum());
        operators.add(new Difference());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals("10 - (5 + 3)", result.getExpression());
        assertEquals("^difference(10, ^sum(5, 3))", result.getExpressionTerm());
        assertEquals(2, result.getResultAsInt());
    }

    @Test
    public void givenDifferenceAndDifferenceExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        FragmentList<AOperator> operators = new FragmentList<AOperator>();
        addNumber(operators, 1);
        addNumber(operators, 5);
        operators.add(new Difference());
        addNumber(operators, 3);
        operators.add(new Difference());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals("1 - 5 - 3", result.getExpression());
        assertEquals("^difference(^difference(1, 5), 3)", result.getExpressionTerm());
        assertEquals(-7, result.getResultAsInt());
    }

    @Test
    public void givenDifferenceAndBracketedDifferenceExpressionWhenEvaluatingOperatorsThenResultIsCorrect() throws SetlException {
        // given
        FragmentList<AOperator> operators = new FragmentList<AOperator>();
        addNumber(operators, 1);
        addNumber(operators, 5);
        addNumber(operators, 3);
        operators.add(new Difference());
        operators.add(new Difference());

        // when
        EvaluationResult result = evaluate(operators);

        // then
        assertEquals("1 - (5 - 3)", result.getExpression());
        assertEquals("^difference(1, ^difference(5, 3))", result.getExpressionTerm());
        assertEquals(-1, result.getResultAsInt());
    }

    @Test
    public void givenThreeDifferentExpressionWhenComparingThenResultIsCorrect() throws SetlException {
        // given
        FragmentList<AOperator> operatorsA = new FragmentList<AOperator>();
        addNumber(operatorsA, 10);
        addNumber(operatorsA, 20);
        operatorsA.add(new Sum());
        OperatorExpression expressionA = new OperatorExpression(operatorsA);

        FragmentList<AOperator> operatorsB = new FragmentList<AOperator>();
        addNumber(operatorsB, 10);
        addNumber(operatorsB, 5);
        operatorsB.add(new Difference());
        addNumber(operatorsB, 3);
        operatorsB.add(new Sum());
        OperatorExpression expressionB = new OperatorExpression(operatorsB);

        FragmentList<AOperator> operatorsC = new FragmentList<AOperator>();
        addNumber(operatorsC, 10);
        addNumber(operatorsC, 2);
        operatorsC.add(new Sum());
        addNumber(operatorsC, 20);
        operatorsC.add(new Product());
        OperatorExpression expressionC = new OperatorExpression(operatorsC);

        // when
        int compareAA = expressionA.compareTo(expressionA);
        int compareAB = expressionA.compareTo(expressionB);
        int compareAC = expressionA.compareTo(expressionC);

        int compareBA = expressionB.compareTo(expressionA);
        int compareBB = expressionB.compareTo(expressionB);
        int compareBC = expressionB.compareTo(expressionC);

        int compareCA = expressionC.compareTo(expressionA);
        int compareCB = expressionC.compareTo(expressionB);
        int compareCC = expressionC.compareTo(expressionC);

        // then
        assertEquals(0, compareAA);
        assertEquals(0, compareBB);
        assertEquals(0, compareCC);

        assertNotEquals(expressionA, expressionB);
        assertNotEquals(expressionA, expressionC);
        assertNotEquals(expressionB, expressionC);
        assertNotEquals(expressionB, expressionA);
        assertNotEquals(expressionC, expressionA);
        assertNotEquals(expressionC, expressionB);

        assertNotEquals(expressionA.hashCode(), expressionB.hashCode());
        assertNotEquals(expressionA.hashCode(), expressionC.hashCode());
        assertNotEquals(expressionB.hashCode(), expressionC.hashCode());

        assertTrue(compareAB != 0);
        assertEquals(compareAB, -1 * compareBA);

        assertTrue(compareAC != 0);
        assertEquals(compareAC, -1 * compareCA);

        assertTrue(compareBC != 0);
        assertEquals(compareBC, -1 * compareCB);

        int implicationHoldingTrue = 0;
        if (compareAB < 0 && compareBC < 0) {
            assertTrue(compareAC < 0);
            implicationHoldingTrue++;
        }
        if (compareAB > 0 && compareBC > 0) {
            assertTrue(compareAC > 0);
            implicationHoldingTrue++;
        }
        if (compareAC < 0 && compareCB < 0) {
            assertTrue(compareAB < 0);
            implicationHoldingTrue++;
        }
        if (compareAC > 0 && compareCB > 0) {
            assertTrue(compareAB > 0);
            implicationHoldingTrue++;
        }
        if (compareBA < 0 && compareAC < 0) {
            assertTrue(compareBC < 0);
            implicationHoldingTrue++;
        }
        if (compareBA > 0 && compareAC > 0) {
            assertTrue(compareBC > 0);
            implicationHoldingTrue++;
        }
        assertEquals(1, implicationHoldingTrue);
    }

    @Test
    public void givenTwoEqualExpressionWhenComparingThenResultIsCorrect() throws SetlException {
        // given
        FragmentList<AOperator> operatorsA = new FragmentList<AOperator>();
        addNumber(operatorsA, 10);
        addNumber(operatorsA, 5);
        operatorsA.add(new Difference());
        addNumber(operatorsA, 3);
        operatorsA.add(new Sum());
        OperatorExpression expressionA = new OperatorExpression(operatorsA);

        FragmentList<AOperator> operatorsB = new FragmentList<AOperator>();
        addNumber(operatorsB, 10);
        addNumber(operatorsB, 5);
        operatorsB.add(new Difference());
        addNumber(operatorsB, 3);
        operatorsB.add(new Sum());
        OperatorExpression expressionB = new OperatorExpression(operatorsB);

        // when
        int compareAB = expressionA.compareTo(expressionB);
        int compareBA = expressionB.compareTo(expressionA);

        // then
        assertEquals(0, compareAB);
        assertEquals(0, compareBA);
        assertEquals(expressionA, expressionB);
        assertEquals(expressionA.hashCode(), expressionB.hashCode());
    }

    private void addNumber(FragmentList<AOperator> operators, int number) {
        operators.add(new ValueOperator(Rational.valueOf(number)));
    }

    private EvaluationResult evaluate(FragmentList<AOperator> operators) throws SetlException {
        OperatorExpression operatorExpression = new OperatorExpression(operators);
        StringBuilder expressionTerm = new StringBuilder();
        operatorExpression.toTerm(state).canonical(state, expressionTerm);
        return new EvaluationResult(
                operatorExpression.toString(state),
                expressionTerm.toString(),
                operatorExpression.evaluate(state)
        );
    }

    private static class EvaluationResult {
        private String expression;
        private String expressionTerm;
        private Value  result;

        public EvaluationResult(String expression, String expressionTerm, Value result) {
            this.expression     = expression;
            this.expressionTerm = expressionTerm;
            this.result         = result;
        }

        public String getExpression() {
            return expression;
        }

        public String getExpressionTerm() {
            return expressionTerm;
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

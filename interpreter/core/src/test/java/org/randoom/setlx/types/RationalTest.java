package org.randoom.setlx.types;

import org.junit.Before;
import org.junit.Test;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RationalTest {

    private State state;

    @Before
    public void testSetup() {
        state = new State();
    }

    @Test
    public void givenZeroValueAndPositiveExponentWhenRaisingValueByExponentThenResultIsZero() throws SetlException {
        // given
        Rational number = Rational.ZERO;
        Rational exponent = Rational.FIVE;

        // when
        Value result = number.power(state, exponent);

        // then
        assertEquals(Rational.ZERO, result);
    }

    @Test
    public void givenZeroValueAndZeroExponentWhenRaisingValueByExponentThenResultIsOne() throws SetlException {
        // given
        Rational number = Rational.ZERO;
        Rational exponent = Rational.ZERO;

        // when
        Value result = number.power(state, exponent);

        // then
        assertEquals(Rational.ONE, result);
    }

    @Test
    public void givenZeroValueAndNegativeExponentWhenRaisingValueByExponentThenExpectException() throws SetlException {
        // given
        Rational number = Rational.ZERO;
        Rational exponent = Rational.ONE.minus(state);

        // when
        boolean catchedException = false;
        try { // <- this try catch is not so nice, but lets not pull in lots of test libs to make it look better
            number.power(state, exponent);
        } catch (UndefinedOperationException uoe) {
            if ("'0 ** -1' is undefined.".equals(uoe.getMessage())) {
                catchedException = true;
            }
        }

        // then
        assertTrue(catchedException);
    }

    @Test
    public void givenPositiveValueAndPositiveExponentWhenRaisingValueByExponentThenResultIsPositive() throws SetlException {
        // given
        Rational number = Rational.SIX;
        Rational exponent = Rational.SEVEN;

        // when
        Value result = number.power(state, exponent);

        // then
        assertEquals(Rational.valueOf(279936), result);
    }

    @Test
    public void givenPositiveValueAndZeroExponentWhenRaisingValueByExponentThenResultIsOne() throws SetlException {
        // given
        Rational number = Rational.EIGHT;
        Rational exponent = Rational.ZERO;

        // when
        Value result = number.power(state, exponent);

        // then
        assertEquals(Rational.ONE, result);
    }

    @Test
    public void givenPositiveValueAndNegativeExponentWhenRaisingValueByExponentThenResultIsLessThanOne() throws SetlException {
        // given
        Rational number = Rational.SEVEN;
        Rational exponent = Rational.THREE.minus(state);

        // when
        Value result = number.power(state, exponent);

        // then
        assertEquals(Rational.valueOf(1, 343), result);
    }

    @Test
    public void givenNegativeValueAndEvenPositiveExponentWhenRaisingValueByExponentThenResultIsPositive() throws SetlException {
        // given
        Rational number = Rational.SIX.minus(state);
        Rational exponent = Rational.FOUR;

        // when
        Value result = number.power(state, exponent);

        // then
        assertEquals(Rational.valueOf(1296), result);
    }

    @Test
    public void givenNegativeValueAndOddPositiveExponentWhenRaisingValueByExponentThenResultIsNegative() throws SetlException {
        // given
        Rational number = Rational.SIX.minus(state);
        Rational exponent = Rational.FIVE;

        // when
        Value result = number.power(state, exponent);

        // then
        assertEquals(Rational.valueOf(-7776), result);
    }

    @Test
    public void givenNegativeValueAndZeroExponentWhenRaisingValueByExponentThenResultIsOne() throws SetlException {
        // given
        Rational number = Rational.NINE.minus(state);
        Rational exponent = Rational.ZERO;

        // when
        Value result = number.power(state, exponent);

        // then
        assertEquals(Rational.ONE, result);
    }

    @Test
    public void givenNegativeValueAndNegativeExponentWhenRaisingValueByExponentThenResultIsRationalNumber() throws SetlException {
        // given
        Rational number = Rational.THREE.minus(state);
        Rational exponent = Rational.SEVEN.minus(state);

        // when
        Value result = number.power(state, exponent);

        // then
        assertEquals(Rational.valueOf(-1, 2187), result);
    }
}

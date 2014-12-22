package org.randoom.setlx.types;

import org.junit.Before;
import org.junit.Test;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("JavaDoc")
public class SetlSetTest {
    private State state;

    @Before
    public void testSetup() {
        state = new State();
    }

    private SetlSet generateSetOfFourRationals() {
        SetlSet set = new SetlSet();
        set.addMember(state, Rational.TWO);
        set.addMember(state, Rational.FOUR);
        set.addMember(state, Rational.THREE);
        set.addMember(state, Rational.ONE);
        return set;
    }

    @Test
    public void givenSetlSetWhenIteratingThenIterationIsCorrect() {
        // given
        SetlSet set = generateSetOfFourRationals();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = set.iterator();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }

        // then
        assertEquals(set.size(), values.size());
        assertEquals(Rational.ONE, values.get(0));
        assertEquals(Rational.FOUR, values.get(3));
    }

    @Test
    public void givenSetlListWhenIteratingAndRemovingThenIterationIsCorrect() {
        // given
        SetlSet set = generateSetOfFourRationals();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = set.iterator();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            if (value.equalTo(Rational.TWO) || value.equalTo(Rational.THREE)) {
                iterator.remove();
            } else {
                values.add(value);
            }
        }

        // then
        assertEquals(2, set.size());
        assertEquals(set.size(), values.size());
        assertEquals(Rational.ONE, values.get(0));
        assertEquals(Rational.FOUR, values.get(1));
    }

    @Test
    public void givenSetlListWhenIteratingInReverseThenIterationIsCorrect() {
        // given
        SetlSet set = generateSetOfFourRationals();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = set.descendingIterator();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }

        // then
        assertEquals(set.size(), values.size());
        assertEquals(Rational.FOUR, values.get(0));
        assertEquals(Rational.ONE, values.get(3));
    }

    @Test
    public void givenSetlListWhenIteratingInReverseAndRemovingThenIterationIsCorrect() {
        // given
        SetlSet set = generateSetOfFourRationals();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = set.descendingIterator();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            if (value.equalTo(Rational.TWO) || value.equalTo(Rational.THREE)) {
                iterator.remove();
            } else {
                values.add(value);
            }
        }

        // then
        assertEquals(2, set.size());
        assertEquals(set.size(), values.size());
        assertEquals(Rational.FOUR, values.get(0));
        assertEquals(Rational.ONE, values.get(1));
    }
}

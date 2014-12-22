package org.randoom.setlx.types;

import org.junit.Before;
import org.junit.Test;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("JavaDoc")
public class SetlListTest {
    private State state;

    @Before
    public void testSetup() {
        state = new State();
    }

    private SetlList generateListOfFourRationals() {
        SetlList list = new SetlList();
        list.addMember(state, Rational.ONE);
        list.addMember(state, Rational.TWO);
        list.addMember(state, Rational.THREE);
        list.addMember(state, Rational.FOUR);
        return list;
    }

    @Test
    public void givenSetlListWhenIteratingThenIterationIsCorrect() {
        // given
        SetlList list = generateListOfFourRationals();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = list.iterator();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }

        // then
        assertEquals(list.size(), values.size());
        assertEquals(Rational.ONE, values.get(0));
        assertEquals(Rational.FOUR, values.get(3));
    }

    @Test
    public void givenSetlListWhenIteratingAndRemovingThenIterationIsCorrect() {
        // given
        SetlList list = generateListOfFourRationals();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = list.iterator();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            if (value.equalTo(Rational.TWO) || value.equalTo(Rational.THREE)) {
                iterator.remove();
            } else {
                values.add(value);
            }
        }

        // then
        assertEquals(2, list.size());
        assertEquals(list.size(), values.size());
        assertEquals(Rational.ONE, values.get(0));
        assertEquals(Rational.FOUR, values.get(1));
    }

    @Test
    public void givenSetlListWhenIteratingInReverseThenIterationIsCorrect() {
        // given
        SetlList list = generateListOfFourRationals();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = list.descendingIterator();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }

        // then
        assertEquals(list.size(), values.size());
        assertEquals(Rational.FOUR, values.get(0));
        assertEquals(Rational.ONE, values.get(3));
    }

    @Test
    public void givenSetlListWhenIteratingInReverseAndRemovingThenIterationIsCorrect() {
        // given
        SetlList list = generateListOfFourRationals();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = list.descendingIterator();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            if (value.equalTo(Rational.TWO) || value.equalTo(Rational.THREE)) {
                iterator.remove();
            } else {
                values.add(value);
            }
        }

        // then
        assertEquals(2, list.size());
        assertEquals(list.size(), values.size());
        assertEquals(Rational.FOUR, values.get(0));
        assertEquals(Rational.ONE, values.get(1));
    }
}

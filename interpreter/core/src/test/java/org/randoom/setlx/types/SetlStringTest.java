package org.randoom.setlx.types;

import org.junit.Before;
import org.junit.Test;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("JavaDoc")
public class SetlStringTest {
    private State state;

    @Before
    public void testSetup() {
        state = new State();
    }

    private SetlString generateStringOfFourNumbers() {
        return new SetlString("1234");
    }

    @Test
    public void givenSetlStringWhenIteratingThenIterationIsCorrect() {
        // given
        SetlString string = generateStringOfFourNumbers();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = string.iterator();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }

        // then
        assertEquals(string.size(), values.size());
        assertEquals(new SetlString("1"), values.get(0));
        assertEquals(new SetlString("4"), values.get(3));
    }

    @Test
    public void givenSetlStringWhenIteratingAndRemovingThenIterationIsCorrect() {
        // given
        SetlString string = generateStringOfFourNumbers();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = string.iterator();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            if (value.equalTo(new SetlString("2")) || value.equalTo(new SetlString("3"))) {
                iterator.remove();
            } else {
                values.add(value);
            }
        }

        // then
        assertEquals(2, string.size());
        assertEquals(string.size(), values.size());
        assertEquals(new SetlString("1"), values.get(0));
        assertEquals(new SetlString("4"), values.get(1));
    }

    @Test
    public void givenSetlStringWhenIteratingInReverseThenIterationIsCorrect() {
        // given
        SetlString string = generateStringOfFourNumbers();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = string.descendingIterator();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }

        // then
        assertEquals(string.size(), values.size());
        assertEquals(new SetlString("4"), values.get(0));
        assertEquals(new SetlString("1"), values.get(3));
    }

    @Test
    public void givenSetlStringWhenIteratingInReverseAndRemovingThenIterationIsCorrect() {
        // given
        SetlString string = generateStringOfFourNumbers();

        // when
        ArrayList<Value> values = new ArrayList<Value>();
        Iterator<Value> iterator = string.descendingIterator();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            if (value.equalTo(new SetlString("2")) || value.equalTo(new SetlString("3"))) {
                iterator.remove();
            } else {
                values.add(value);
            }
        }

        // then
        assertEquals(2, string.size());
        assertEquals(string.size(), values.size());
        assertEquals(new SetlString("4"), values.get(0));
        assertEquals(new SetlString("1"), values.get(1));
    }
}

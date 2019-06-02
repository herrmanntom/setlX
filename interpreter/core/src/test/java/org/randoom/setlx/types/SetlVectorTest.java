package org.randoom.setlx.types;

import org.junit.Before;
import org.junit.Test;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Patrick Robinson
 */
@SuppressWarnings("JavaDoc")
public class SetlVectorTest {

    private State state;
    private Map<Integer, SetlDouble> sdi;
    private SetlVector simple;
    private Map<Integer, ArrayList<Double>> simple_sdi_results_mul;

    @Before
    public void testSetup() throws SetlException {
        state = new State();

        ArrayList<Double> simpleBase = new ArrayList<>(3);
        simpleBase.add(1.0);
        simpleBase.add(2.0);
        simpleBase.add(3.0);
        sdi = new TreeMap<>();
            for (int i = -10000; i <= 10000; i++) {
                sdi.put(i, SetlDouble.valueOf(i));
            }
        simple_sdi_results_mul = new TreeMap<>();
        for (int i = -10000; i <= 10000; i++) {
            ArrayList<Double> a = new ArrayList<>(3);
            a.add(simpleBase.get(0) * i);
            a.add(simpleBase.get(1) * i);
            a.add(simpleBase.get(2) * i);
            simple_sdi_results_mul.put(i, a);
        }
        simple = new SetlVector(simpleBase);
    }

    @Test
    public void testMultiply() {
        // System.err.println("[DEBUG]: testMultiply");
        // Simple:
        // - Vector * Vector
        // - Scalar * Vector
        // - Vector * Scalar
        // (vers. Datentypen fuer Scalar)
        // Randbedingungen ?
        // Complex:
        // - Kombinationen mit Termen

        // Vector * Scalar
        Value s;
        for (int i = -10000; i <= 10000; i++) {
            try {
                s = simple.product(null, sdi.get(i));
            } catch (SetlException ex) {
                System.err.println(ex.getMessage());
                fail("Simple_sdi_mul error: " + i + " SetlException on .product");
                return;
            }
            assertTrue("Simple_sdi_mul error: " + i + " not instanceof SetlVector", s instanceof SetlVector);
            ArrayList<Double> sbase = ((SetlVector) s).getVectorCopy();
            ArrayList<Double> rbase = simple_sdi_results_mul.get(i);
            assertEquals("Simple_sdi_mul error: " + i + " wrong result: " + sbase + " vs " + rbase, sbase, rbase);
        }

        // Scalar * Vector
        for (int i = -10000; i <= 10000; i++) {
            try {
                s = sdi.get(i).product(null, simple);
            } catch (SetlException ex) {
                System.err.println(ex.getMessage());
                fail("Simple_sdi_mul_rev error: " + i + " SetlException on .product");
                return;
            }
            assertTrue("Simple_sdi_mul_rev error: " + i + " not instanceof SetlVector", s instanceof SetlVector);
            ArrayList<Double> sbase = ((SetlVector) s).getVectorCopy();
            ArrayList<Double> rbase = simple_sdi_results_mul.get(i);
            assertEquals("Simple_sdi_mul error: " + i + " wrong result: " + sbase + " vs " + rbase, sbase, rbase);
        }

        // Vector * Vector
        try {
            s = simple.product(null, simple);
        } catch (SetlException ex) {
            System.err.println(ex.getMessage());
            fail("Simple_simple_mul error: SetlException on .product");
            return;
        }
        assertTrue("Simple_simple_mul error: " + s + " not a number", s.isNumber() == SetlBoolean.TRUE);
        assertTrue("Simple_simple_mul error: wrong result: " + s + " vs 14", ((NumberValue) s).equalTo(sdi.get(14)));
    }

    @Test
    public void testConstruction() {
        // System.err.println("[DEBUG]: testConstruction");
        // vers. Constructors
        // Matrixconversion
        // PD_vector

        SetlList base = new SetlList();
        base.addMember(state, sdi.get(1));
        base.addMember(state, sdi.get(2));
        base.addMember(state, sdi.get(3));
        SetlVector coltest;
        try {
            coltest = new SetlVector(state, base);
        } catch (SetlException ex) {
            System.err.println(ex.getMessage());
            fail("Simple_construct error: SetlException");
            return;
        }
        assertTrue("Simple_construct error: wrong result: " + coltest + " vs " + simple, coltest.equalTo(simple));

        base.removeLastMember();
        base.addMember(null, SetlBoolean.TRUE);
        try {
            coltest = new SetlVector(null, base);
            fail("Simple_construct missing_error: IncompatibleTypeException not thrown");
        } catch (SetlException ex) {
        }
    }

    @Test
    public void testTools() {
        // System.err.println("[DEBUG]: testTools");
        // ==, clone, compare, iterator, canonical, ...
        assertTrue("== error", simple.equalTo(simple));
        assertTrue("clone error", simple.equalTo(simple.clone()));
        assertTrue("compareTo equal error", simple.compareTo(simple) == 0);
        Value b = sdi.get(0);
        for (Value a : simple) {
            try {
            b = b.sum(state, a);
            } catch (SetlException ex) {
                System.err.println(ex.getMessage());
                fail("Iterator error: sum " + a);
                return;
            }
        }
        assertTrue("Iterator error: wrong result " + b + " vs 6.0", b.equalTo(sdi.get(6)));
        StringBuilder simpleBuilder = new StringBuilder();
        simple.canonical(state, simpleBuilder);
        assertTrue("Canonical error: wrong result " + simpleBuilder.toString() + " vs <<1.0 2.0 3.0>>", simpleBuilder.toString().equals("<<1.0 2.0 3.0>>"));
        List<Value> simpleIdx = new ArrayList<>();
        simpleIdx.add(Rational.ONE);
        try {
            assertTrue("Simple[] error: wrong result", simple.collectionAccess(null, simpleIdx).equalTo(sdi.get(1)));
        } catch (SetlException ex) {
            System.err.println(ex.getMessage());
            fail("Simple[] error: access exception");
        }
        SetlVector scl = (SetlVector) simple.clone();
        try {
            scl.setMember(state, 1, sdi.get(4));
        } catch (SetlException ex) {
            System.err.println(ex.getMessage());
            fail("Simple[] := error: access exception");
        }
            assertTrue("Simple[] := error: wrong result", scl.getVectorCopy().get(0).equals(sdi.get(4).jDoubleValue()));
    }

    @Test
    public void testSum() {
        // System.err.println("[DEBUG]: testSum");
        Value s;
        try {
            s = simple.sum(null, simple);
        } catch (SetlException ex) {
            System.err.println(ex.getMessage());
            fail("Simple sum error: SetlException on .sum");
            return;
        }
        assertTrue("Simple sum error: instanceof", s instanceof SetlVector);
        ArrayList<Double> sbase = ((SetlVector) s).getVectorCopy();
        assertTrue("Simple sum error: wrong result: " + sbase + " vs [2,4,6]", sbase.get(0).equals(2.0) && sbase.get(1).equals(4.0) && sbase.get(2).equals(6.0));
        try {
            s = simple.sum(null, new SetlMatrix(null, simple));
        } catch (SetlException ex) {
            System.err.println(ex.getMessage());
            fail("Simple sum matrix error: SetlException on .sum");
            return;
        }
        assertTrue("Simple sum matrix error: instanceof", s instanceof SetlVector);
        sbase = ((SetlVector) s).getVectorCopy();
        assertTrue("Simple sum matrix error: wrong result: " + sbase + " vs [2,4,6]", sbase.get(0).equals(2.0) && sbase.get(1).equals(4.0) && sbase.get(2).equals(6.0));
    }

    @Test
    public void testDif() {
        // System.err.println("[DEBUG]: testDif");
        Value s;
        try {
            s = simple.difference(state, simple);
        } catch (SetlException ex) {
            System.err.println(ex.getMessage());
            fail("Simple dif error: SetlException on .sum");
            return;
        }
        assertTrue("Simple dif error: instanceof", s instanceof SetlVector);
        ArrayList<Double> sbase = ((SetlVector) s).getVectorCopy();
        assertTrue("Simple dif error: wrong result: " + sbase + " vs [0,0,0]", sbase.get(0).equals(0.0) && sbase.get(1).equals(0.0) && sbase.get(2).equals(0.0));
        try {
            s = simple.difference(state, new SetlMatrix(state, simple));
        } catch (SetlException ex) {
            System.err.println(ex.getMessage());
            fail("Simple dif matrix error: SetlException on .sum");
            return;
        }
        assertTrue("Simple dif matrix error: instanceof", s instanceof SetlVector);
        sbase = ((SetlVector) s).getVectorCopy();
        assertTrue("Simple dif matrix error: wrong result: " + sbase + " vs [0,0,0]", sbase.get(0).equals(0.0) && sbase.get(1).equals(0.0) && sbase.get(2).equals(0.0));
    }

    private SetlVector generateVectorOfFourDoubles() {
        ArrayList<Double> vector = new ArrayList<>();
        vector.add(1.0);
        vector.add(2.0);
        vector.add(3.0);
        vector.add(4.0);
        return new SetlVector(vector);
    }

    @Test
    public void givenSetlVectorWhenIteratingThenIterationIsCorrect() throws UndefinedOperationException {
        // given
        SetlVector vector = generateVectorOfFourDoubles();

        // when
        ArrayList<Value> values = new ArrayList<>();
        Iterator<Value> iterator = vector.iterator();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }

        // then
        assertEquals(vector.size(), values.size());
        assertEquals(SetlDouble.ONE, values.get(0));
        assertEquals(SetlDouble.valueOf(4.0), values.get(3));
    }

    @Test
    public void givenSetlVectorWhenIteratingAndRemovingThenIterationIsCorrect() throws UndefinedOperationException {
        // given
        SetlVector vector = generateVectorOfFourDoubles();

        // when
        ArrayList<Value> values = new ArrayList<>();
        Iterator<Value> iterator = vector.iterator();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            if (value.equalTo(SetlDouble.valueOf(2.0)) || value.equalTo(SetlDouble.valueOf(3.0))) {
                iterator.remove();
            } else {
                values.add(value);
            }
        }

        // then
        assertEquals(2, vector.size());
        assertEquals(vector.size(), values.size());
        assertEquals(SetlDouble.ONE, values.get(0));
        assertEquals(SetlDouble.valueOf(4.0), values.get(1));
    }

    @Test
    public void givenSetlVectorWhenIteratingInReverseThenIterationIsCorrect() throws UndefinedOperationException {
        // given
        SetlVector vector = generateVectorOfFourDoubles();

        // when
        ArrayList<Value> values = new ArrayList<>();
        Iterator<Value> iterator = vector.descendingIterator();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }

        // then
        assertEquals(vector.size(), values.size());
        assertEquals(SetlDouble.valueOf(4.0), values.get(0));
        assertEquals(SetlDouble.ONE, values.get(3));
    }

    @Test
    public void givenSetlVectorWhenIteratingInReverseAndRemovingThenIterationIsCorrect() throws UndefinedOperationException {
        // given
        SetlVector vector = generateVectorOfFourDoubles();

        // when
        ArrayList<Value> values = new ArrayList<>();
        Iterator<Value> iterator = vector.descendingIterator();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            if (value.equalTo(SetlDouble.valueOf(2.0)) || value.equalTo(SetlDouble.valueOf(3.0))) {
                iterator.remove();
            } else {
                values.add(value);
            }
        }

        // then
        assertEquals(2, vector.size());
        assertEquals(vector.size(), values.size());
        assertEquals(SetlDouble.valueOf(4.0), values.get(0));
        assertEquals(SetlDouble.ONE, values.get(1));
    }
}

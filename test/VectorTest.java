package org.randoom.setlx.tests;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.NumberValue;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlMatrix;
import org.randoom.setlx.types.SetlVector;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 */
public class VectorTest {

	static Map<Integer, SetlDouble> sdi;
	static NumberValue[] simpleBase;
	static SetlVector simple;
	static Map<Integer, Value[]> simple_sdi_results_mul;

	@BeforeClass
	public static void testSetup() {
		// Load cases from file (generated from octave)
		simpleBase = new NumberValue[3];
		try {
			simpleBase[0] = SetlDouble.valueOf(1);
			simpleBase[1] = SetlDouble.valueOf(2);
			simpleBase[3] = SetlDouble.valueOf(3);
		} catch(UndefinedOperationException ex) {
			System.err.println(ex.getMessage());
			fail("Error in setting up simpleBase");
		}
		sdi = new TreeMap<Integer, SetlDouble>();
		try {
			for(int i = -10000; i <= 10000; i++) {
				sdi.put(i, SetlDouble.valueOf(i));
			}
		} catch(UndefinedOperationException ex) {
			System.err.println(ex.getMessage());
			fail("Error in setting up sdi");
		}
		simple_sdi_results_mul = new TreeMap<Integer, Value[]>();
		try {
			for(int i = -10000; i <= 10000; i++) {
				Value[] a = new Value[3];
				a[0] = simpleBase[0].product(null, sdi.get(i));
				a[1] = simpleBase[1].product(null, sdi.get(i));
				a[2] = simpleBase[2].product(null, sdi.get(i));
			}
		} catch(SetlException ex) {
			System.err.println(ex.getMessage());
			fail("Error in setting up simple_sdi_results");
		}
		try {
			simple = new SetlVector(simpleBase);
		} catch(IncompatibleTypeException ex) {
			System.err.println(ex.getMessage());
			fail("Simple vector construction throws IncompatibleTypeException.");
		}
	}

	@Test
	public void testMultiply() {
		// Simple:
		// - Vector * Vector
		// - Scalar * Vector
		// - Vector * Scalar
		// (vers. Datentypen für Scalar)
		// Randbedingungen ?
		// Complex:
		// - Kombinationen mit Termen

		// Vector * Scalar
		Value s;
		for(int i = -10000; i <= 10000; i++) {
			try {
				s = simple.product(null, sdi.get(i));
			} catch(SetlException ex) {
				System.err.println(ex.getMessage());
				fail("Simple_sdi_mul error: " + i + " SetlException on .product");
				return;
			}
			assertTrue("Simple_sdi_mul error: " + i + " not instanceof SetlVector", s instanceof SetlVector);
			NumberValue[] sbase = ((SetlVector)s).getValue();
			Value[] rbase = simple_sdi_results_mul.get(i);
			assertTrue("Simple_sdi_mul error: " + i + " wrong result: " + sbase + " vs " + rbase, sbase[0].equalTo(rbase[0]) && sbase[1].equalTo(rbase[1]) && sbase[2].equalTo(rbase[2]));
		}

		// Scalar * Vector
		for(int i = -10000; i <= 10000; i++) {
			try {
				s = sdi.get(i).product(null, simple);
			} catch(SetlException ex) {
				System.err.println(ex.getMessage());
				fail("Simple_sdi_mul_rev error: " + i + " SetlException on .product");
				return;
			}
			assertTrue("Simple_sdi_mul_rev error: " + i + " not instanceof SetlVector", s instanceof SetlVector);
			NumberValue[] sbase = ((SetlVector)s).getValue();
			Value[] rbase = simple_sdi_results_mul.get(i);
			assertTrue("Simple_sdi_mul_rev error: " + i + " wrong result: " + sbase + " vs " + rbase, sbase[0].equalTo(rbase[0]) && sbase[1].equalTo(rbase[1]) && sbase[2].equalTo(rbase[2]));
		}

		// Vector * Vector
		try {
			s = simple.product(null, simple);
		} catch(SetlException ex) {
			System.err.println(ex.getMessage());
			fail("Simple_simple_mul error: SetlException on .product");
			return;
		}
		assertTrue("Simple_simple_mul error: " + s + " not a number", s.isNumber() == SetlBoolean.TRUE);
		assertTrue("Simple_simple_mul error: wrong result: " + s + " vs 14", ((NumberValue)s).equalTo(sdi.get(14))); // TODO Check result
	}

	@Test
	public void testConstruction() {
		// vers. Constructors
		// Matrixconversion
		// PD_vector

		SetlList base = new SetlList();
		base.addMember(null, sdi.get(1));
		base.addMember(null, sdi.get(2));
		base.addMember(null, sdi.get(3));
		SetlVector coltest;
		try {
			coltest = new SetlVector(null, base);
		} catch(IncompatibleTypeException ex) {
			System.err.println(ex.getMessage());
			fail("Simple_construct error: IncompatibleTypeException");
			return;
		}
		assertTrue("Simple_construct error: wrong result: " + coltest + " vs " + simple, coltest.equalTo(simple));

		base.removeLastMember();
		base.addMember(null, SetlBoolean.TRUE);
		try {
			coltest = new SetlVector(null, base);
			fail("Simple_construct missing_error: IncompatibleTypeException not thrown");
		} catch(IncompatibleTypeException ex) {
		}

		Random rng = new Random();
		for(int n = 1; n <= 1000; n++) {
			NumberValue[] rawSource = new NumberValue[n];
			SetlList colSource = new SetlList(n);
			for(int i = 0; i < n; n++) {
				SetlDouble val = sdi.get(rng.nextInt(20001) - 10000);
				rawSource[i] = val;
				colSource.addMember(null, val);
			}
			SetlVector rawVector;
			try {
				rawVector = new SetlVector(rawSource);
			} catch(IncompatibleTypeException ex) {
				System.err.println(ex.getMessage());
				fail("Rng_construct error " + n + " : IncompatibleTypeException on raw " + rawSource);
				return;
			}
			try {
				coltest = new SetlVector(null, colSource);
			} catch(IncompatibleTypeException ex) {
				System.err.println(ex.getMessage());
				fail("Rng_construct error " + n + " : IncompatibleTypeException on col " + rawSource);
				return;
			}
			assertTrue("Rng_construct error " + n + " : raw not equal col " + rawSource, rawVector.equalTo(coltest));
		}

		// Matrixconversion
		for(int n = 1; n <= 1000; n++) {
			NumberValue[] rawSource = new NumberValue[n];
			SetlList rowBase = new SetlList();
			SetlList row = new SetlList(n);
			SetlList columnBase = new SetlList(n);
			for(int i = 0; i < n; n++) {
				SetlDouble val = sdi.get(rng.nextInt(20001) - 10000);
				rawSource[i] = val;
				row.addMember(null, val);
				SetlList column = new SetlList();
				column.addMember(null, val);
				columnBase.addMember(null, column);
			}
			rowBase.addMember(null, row);
			SetlVector rawVector;
			try {
				rawVector = new SetlVector(rawSource);
			} catch(IncompatibleTypeException ex) {
				System.err.println(ex.getMessage());
				fail("Rng_convert error " + n + " : IncompatibleTypeException on raw " + rawSource);
				return;
			}
			SetlVector rowMatrix;
			try {
				rowMatrix = new SetlVector(null, new SetlMatrix(null, rowBase));

			} catch(SetlException ex) {
				System.err.println(ex.getMessage());
				fail("Rng_convert error " + n + " : SetlException on row " + rawSource);
				return;
			}
			SetlVector columnMatrix;
			try {
				columnMatrix = new SetlVector(null, new SetlMatrix(null, columnBase));

			} catch(SetlException ex) {
				System.err.println(ex.getMessage());
				fail("Rng_convert error " + n + " : SetlException on column " + rawSource);
				return;
			}
			SetlVector convMatrix;
			try {
				convMatrix = new SetlVector(null, new SetlMatrix(null, rawVector));

			} catch(SetlException ex) {
				System.err.println(ex.getMessage());
				fail("Rng_convert error " + n + " : SetlException on conv " + rawSource);
				return;
			}
			assertTrue("Rng_convert error " + n + " : raw not equal row " + rawSource, rawVector.equalTo(rowMatrix));
			assertTrue("Rng_convert error " + n + " : raw not equal column " + rawSource, rawVector.equalTo(columnMatrix));
			assertTrue("Rng_convert error " + n + " : raw not equal conv " + rawSource, rawVector.equalTo(convMatrix));
		}
	}

	@Test
	public void testTools() {
		// ==, <=, clone, compare, iterator, canonical, ...
		// wie kann ich automatisch zugriff mit [] aus setlx testen?
	}

	@Test
	public void testSum() {
		Value s;
		try {
			s = simple.sum(null, simple);
		} catch(SetlException ex) {
			System.err.println(ex.getMessage());
			fail("Simple sum error: SetlException on .sum");
			return;
		}
		assertTrue("Simple sum error: instanceof", s instanceof SetlVector);
		NumberValue[] sbase = ((SetlVector)s).getValue();
		assertTrue("Simple sum error: wrong result: " + sbase + " vs [2,4,6]", sbase[0].equalTo(sdi.get(2)) && sbase[1].equalTo(sdi.get(4)) && sbase[2].equalTo(sdi.get(6)));
	}

	@Test
	public void testDif() {
		Value s;
		try {
			s = simple.difference(null, simple);
		} catch(SetlException ex) {
			System.err.println(ex.getMessage());
			fail("Simple dif error: SetlException on .sum");
			return;
		}
		assertTrue("Simple dif error: instanceof", s instanceof SetlVector);
		NumberValue[] sbase = ((SetlVector)s).getValue();
		assertTrue("Simple dif error: wrong result: " + sbase + " vs [0,0,0]", sbase[0].equalTo(sdi.get(0)) && sbase[1].equalTo(sdi.get(0)) && sbase[2].equalTo(sdi.get(0)));
	}

	@Test
	public void testPow() {
		Value s;
		try {
			s = simple.power(null, simple);
		} catch(SetlException ex) {
			System.err.println(ex.getMessage());
			fail("Simple pow error: SetlException on .power");
			return;
		}
		assertTrue("Simple pow error: instanceof", s instanceof SetlVector);
		NumberValue[] sbase = ((SetlVector)s).getValue();
		assertTrue("Simple pow error: wrong result: " + sbase + " vs [0,0,0]", sbase[0].equalTo(sdi.get(0)) && sbase[1].equalTo(sdi.get(0)) && sbase[2].equalTo(sdi.get(0)));
	}
}

package org.randoom.setlx.tests;

import java.util.Map;
import java.util.TreeMap;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.SetlMatrix;

/**
 * @author Patrick Robinson
 */
public class MatrixTest {

	static Map<Integer, SetlDouble> sdi;
	static double[][] simpleBase;
	static SetlMatrix simple;
	static double[][] snsBase;
	static SetlMatrix sns;

	@BeforeClass
	public static void testSetup() {
		// Load cases from file (generated from octave)
		sdi = new TreeMap<Integer, SetlDouble>();
		try {
			for(int i = -10000; i <= 10000; i++) {
				sdi.put(i, SetlDouble.valueOf(i));
			}
		} catch(UndefinedOperationException ex) {
			System.err.println(ex.getMessage());
			fail("Error in setting up sdi");
		}
		simpleBase = new double[2][2];
		simpleBase[0][0] = 1;
		simpleBase[0][1] = 2;
		simpleBase[1][0] = 3;
		simpleBase[1][1] = 4;
		simple = new SetlMatrix(new Jama.Matrix(simpleBase));
		snsBase = new double[2][3];
		snsBase[0][0] = 1;
		snsBase[0][1] = 2;
		snsBase[0][2] = 3;
		snsBase[1][0] = 4;
		snsBase[1][1] = 5;
		snsBase[1][2] = 6;
	}

	@Test
	public void testMultiply() {
		// Simple:
		// - Matrix * Matrix
		double[][] tmpResult;
		try {
			tmpResult = ((SetlMatrix)simple.product(null, simple)).getBase().getArray();
		} catch(SetlException ex) {
			System.err.println(ex.getMessage());
			fail("simple_simple_mul error: exception on .product");
			return;
		}
		double[][] shouldBeResult = new double[2][2];
		shouldBeResult[0][0] = 7;
		shouldBeResult[0][1] = 10;
		shouldBeResult[1][0] = 15;
		shouldBeResult[1][1] = 22;
		assertTrue("simple_simple_mul error: wrong result: " + tmpResult + " vs " + shouldBeResult, Arrays.deepEquals(tmpResult, shouldBeResult));

		try {
			tmpResult = ((SetlMatrix)simple.product(null, sns)).getBase().getArray();
		} catch(SetlException ex) {
			System.err.println(ex.getMessage());
			fail("simple_sns_mul error: exception on .product");
			return;
		}
		shouldBeResult = new double[2][3];
		shouldBeResult[0][0] = 9;
		shouldBeResult[0][1] = 12;
		shouldBeResult[0][2] = 15;
		shouldBeResult[1][0] = 19;
		shouldBeResult[1][1] = 26;
		shouldBeResult[1][2] = 33;
		assertTrue("simple_sns_mul error: wrong result: " + tmpResult + " vs " + shouldBeResult, Arrays.deepEquals(tmpResult, shouldBeResult));

		try {
			sns.product(null, simple);
			fail("sns_simple missing_error: Incompatible dimensions not found");
		} catch(IncompatibleTypeException ex) {
		} catch(SetlException ex) {
			System.err.println(ex.getMessage());
			fail("sns_simple wrong_error: SetlException");
		}
		// - Scalar * Matrix
		// - Matrix * Scalar
		// (vers. Datentypen für Scalar)
		// Randbedingungen ?
		// - Kombinationen mit Termen
	}

	@Test
	public void testConstruction() {
		// vers. Constructors
		// vectorconversion
		// PD_matrix
	}

	@Test
	public void testTools() {
		// ==, <=, clone, compare, iterator, canonical, ...
		// wie kann ich automatisch zugriff mit [] aus setlx testen?
	}

	@Test
	public void testSum() {

	}

	@Test
	public void testDif() {

	}

	@Test
	public void testPow() {

	}

	@Test
	public void testCalls() {
		// Calls, that basically just depend on the Jama lib (solve, svd, ...) + entsprechende PD_*
		// eigen*
	}

	@Test
	public void testFactorial() {

	}
}

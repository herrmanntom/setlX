package org.randoom.setlx.tests;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlMatrix;
import org.randoom.setlx.types.Value;

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
		// - Matrix * Scalar
		for(int i = -10000; i <= 10000; i++) {
			try {
				tmpResult = ((SetlMatrix)simple.product(null, sdi.get(i))).getBase().getArray();
			} catch(SetlException ex) {
				System.err.println(ex.getMessage());
				fail("simple_scalar_mul error: exception on .product");
				return;
			}
			assertTrue("simple_scalar_mul error: wrong result " + i, tmpResult[0][0] == (simpleBase[0][0] * i) && tmpResult[0][1] == (simpleBase[0][1] * i) && tmpResult[1][0] == (simpleBase[1][0] * i) && tmpResult[1][1] == (simpleBase[1][1] * i));
		}
		// - Scalar * Matrix
		for(int i = -10000; i <= 10000; i++) {
			try {
				tmpResult = ((SetlMatrix)sdi.get(i).product(null, simple)).getBase().getArray();
			} catch(SetlException ex) {
				System.err.println(ex.getMessage());
				fail("scalar_simple_mul error: exception on .product");
				return;
			}
			assertTrue("scalar_simple_mul error: wrong result " + i, tmpResult[0][0] == (simpleBase[0][0] * i) && tmpResult[0][1] == (simpleBase[0][1] * i) && tmpResult[1][0] == (simpleBase[1][0] * i) && tmpResult[1][1] == (simpleBase[1][1] * i));
		}
	}

	@Test
	public void testConstruction() {
		// vers. Constructors
		// vectorconversion
		// PD_matrix
		SetlList colBase = new SetlList();
		SetlList tmpList = new SetlList();
		tmpList.addMember(null, sdi.get(1));
		tmpList.addMember(null, sdi.get(2));
		colBase.addMember(null, tmpList);
		tmpList = new SetlList();
		tmpList.addMember(null, sdi.get(3));
		tmpList.addMember(null, sdi.get(4));
		colBase.addMember(null, tmpList);
		try {
			assertTrue("col_construct error: wrong result", (new SetlMatrix(null, colBase)).equalTo(simple));
		} catch(SetlException ex) {
			System.err.println(ex.getMessage());
			fail("col_construct error: exception");
		}
	}

	@Test
	public void testTools() {
		// ==, clone, compare, iterator, canonical, ...
		assertTrue("== simple error", simple.equalTo(simple));
		assertTrue("== clone error", simple.equalTo(simple.clone()));
		assertTrue("!= error: wrong result", sns.equalTo(simple));
		assertTrue("compare to same error: wrong result", simple.compareTo(simple) == 0);
		assertTrue("compare to different error: wrong result", sns.compareTo(simple) != 0);

		StringBuilder stringBuilder = new StringBuilder();
		simple.canonical(null, stringBuilder);
		assertTrue("canonical error: wrong result " + stringBuilder.toString() + " vs < [ 1 2 ] [ 3 4 ] >", stringBuilder.toString().equals("< [ 1 2 ] [ 3 4 ] >"));

		Value b = sdi.get(0);
		for(Value row : simple) {
			try {
				for(Value cell : (CollectionValue)row) {
					b = b.sum(null, cell);
				}
			} catch(SetlException ex) {
				System.err.println(ex.getMessage());
				fail("Iterator error: sum " + row);
				return;
			}
		}
		assertTrue("Iterator error: wrong result " + b + " vs 10", b.equalTo(sdi.get(10)));

		List<Value> idx = new ArrayList<Value>();
		idx.add(sdi.get(1));
		SetlList tmpList;
		try {
			tmpList = (SetlList)simple.collectionAccess(null, idx);
		} catch(SetlException ex) {
			System.err.println(ex.getMessage());
			fail("matrix[] access error: exception");
			return;
		}
		int i = 1;
		for(Value v : tmpList) {
			assertTrue("matrix[" + i + "] error: wrong result: " + v, v.equalTo(sdi.get(i)));
			i++;
		}
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

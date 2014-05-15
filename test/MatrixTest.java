package org.randoom.setlx.tests;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Patrick Robinson
 */
public class MatrixTest {

	@BeforeClass
	public static void testSetup() {
		// Load cases from file (generated from octave)
	}

	@Test
	public void testMultiply() {
		// Simple:
		// - Matrix * Matrix
		// - Scalar * Matrix
		// - Matrix * Scalar
		// (vers. Datentypen für Scalar)
		// Randbedingungen ?
		// Complex:
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

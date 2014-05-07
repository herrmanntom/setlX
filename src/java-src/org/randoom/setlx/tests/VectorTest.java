package org.randoom.setlx.tests;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Patrick Robinson
 */
public class VectorTest {

	@BeforeClass
	public static void testSetup() {
		// Load cases from file (generated from octave)
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
	}

	@Test
	public void testConstruction() {
		// vers. Constructors
		// Matrixconversion
		// PD_vector
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
}

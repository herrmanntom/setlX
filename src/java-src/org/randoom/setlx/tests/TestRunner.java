package org.randoom.setlx.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @author Patrick Robinson
 */
public class TestRunner {

	public static void main(String[] args) {
		Result rMatrix = JUnitCore.runClasses(MatrixTest.class);
		Result rVector = JUnitCore.runClasses(VectorTest.class);
		System.out.println("Matrix:");
		for(Failure f : rMatrix.getFailures()) {
			System.err.println(f.toString());
		}
		System.out.println("Vector:");
		for(Failure f : rVector.getFailures()) {
			System.err.println(f.toString());
		}
	}
}

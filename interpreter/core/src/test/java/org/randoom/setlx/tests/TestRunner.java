package org.randoom.setlx.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @author Patrick Robinson
 */
public class TestRunner {
    public static void main(String[] args) {
        System.err.println("Matrix:");
        Result rMatrix = JUnitCore.runClasses(MatrixTest.class);
        System.err.println("CountFailMatrix: " + rMatrix.getFailureCount());
        for (Failure f : rMatrix.getFailures()) {
            System.err.println("FailMatrix: " + f.toString());
            // System.err.println(f.getMessage() + " " + f.getTrace() + " " + f.getDescription() + " " + f.getException());
        }
        System.err.println("Vector:");
        Result rVector = JUnitCore.runClasses(VectorTest.class);
        System.err.println("CountFailVec: " + rVector.getFailureCount());
        for (Failure f : rVector.getFailures()) {
            System.err.println("FailVec: " + f.toString());
            // System.err.println(f.getMessage() + " " + f.getTrace() + " " + f.getDescription() + " " + f.getException());
        }
        System.err.println(rMatrix.getFailureCount() + rVector.getFailureCount() > 0 ? "Still buggy" : "Success: no failures found");
    }
}

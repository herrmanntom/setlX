package org.randoom.setlx;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.randoom.setlx.types.SetlMatrixTest;
import org.randoom.setlx.types.SetlVectorTest;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Running setlX unit tests:");
        Result testResult = JUnitCore.runClasses(SetlMatrixTest.class, SetlVectorTest.class);
        for (Failure f : testResult.getFailures()) {
            System.err.println("Failure: " + f.toString());
            // System.err.println(f.getMessage() + " " + f.getTrace() + " " + f.getDescription() + " " + f.getException());
        }
        System.out.println("Tests run: " + testResult.getRunCount() + " Tests failed: " + testResult.getFailureCount());
        if (testResult.getFailureCount() > 0) {
            System.err.println("Still buggy");
            System.exit(1);
        } else {
            System.out.println("Success: no failures found");
            System.exit(0);
        }
    }
}

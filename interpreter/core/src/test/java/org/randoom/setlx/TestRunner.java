package org.randoom.setlx;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Run all tests.
 */
public class TestRunner {
    /**
     * Main function which runs all tests in source files ending with Test.java
     *
     * @param args path to directory that contains source of test classes
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Parameter missing! Please pass directory that contains sources of test classes as only parameter.");
            System.exit(1);
        }
        File testDir = new File(args[0]);
        if (!testDir.isDirectory() || ! testDir.canRead()) {
            System.err.println("Parameter is not a valid directory.");
            System.exit(1);
        }
        ArrayList<Class<?>> testClasses    = new ArrayList<Class<?>>();
        LinkedList<File>    directoryQueue = new LinkedList<File>();
        directoryQueue.add(testDir);
        while (!directoryQueue.isEmpty()) {
            File directory = directoryQueue.poll();
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && file.canRead()) {
                        directoryQueue.add(file);
                    } else if (file.isFile() && file.canRead()) {
                        String fileName = file.getName();
                        if (fileName.endsWith("Test.java")) {
                            try {
                                BufferedReader br = new BufferedReader(new FileReader(file));
                                try {
                                    String line;
                                    do {
                                        line = br.readLine();
                                        if (line != null && line.startsWith("package ") && line.endsWith(";")) {
                                            String packageName = line.replace("package ","").replace(";","");
                                            testClasses.add(Class.forName(packageName + "." + fileName.replace(".java", "")));
                                        }
                                    } while (line != null);
                                } finally {
                                    br.close();
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                                System.exit(1);
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }
                    }
                }
            }
        }
        if (testClasses.isEmpty()) {
            System.err.println("No test classes found in " + testDir.getAbsolutePath() + ".");
            System.exit(1);
        }

        System.out.println("Running setlX unit tests...");
        int testsRun = 0;
        int failures = 0;
        for (Class<?> testClass : testClasses) {
            System.out.println("  " + testClass.getSimpleName() + ":");
            Result testResult = JUnitCore.runClasses(testClass);
            for (Failure f : testResult.getFailures()) {
                System.err.println("    Failure: " + f.getMessage());
                f.getException().printStackTrace();
            }
            System.out.println("    Tests run: " + testResult.getRunCount() + ", Tests failures: " + testResult.getFailureCount());
            testsRun += testResult.getRunCount();
            failures += testResult.getFailureCount();
        }

        System.out.println("  -----------------------------------");
        System.out.println("    Tests run: " + testsRun + ", Tests failures: " + failures);
        System.out.println("  ===================================");

        if (failures > 0) {
            System.err.println("Still buggy");
            System.exit(1);
        } else {
            System.out.println("Success: no failures found");
            System.exit(0);
        }
    }
}

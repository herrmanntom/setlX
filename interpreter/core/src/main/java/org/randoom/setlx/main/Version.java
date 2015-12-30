package org.randoom.setlx.main;

import org.randoom.setlx.utilities.State;

/**
 * Class containing main-function to print current version of this library.
 */
public class Version {
    /**
     * The main method.
     *
     * @param args Command line arguments - all of which are ignored.
     */
    public static void main(final String[] args) {
        System.out.println(State.getSetlXVersion());
        System.out.println("(Source version: " + State.getSetlXBuildNumber() + ")");
    }
}


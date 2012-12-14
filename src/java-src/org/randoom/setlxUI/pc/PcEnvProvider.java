package org.randoom.setlxUI.pc;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.utilities.EnvironmentProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// This interface class provides access to the I/O mechanisms of the target platform etc
/*package*/ class PcEnvProvider implements EnvironmentProvider {

    private final static String         sTAB            = "\t";
    private       static String         sENDL           = null;

  /*package*/     static String         sLibraryPath    = "";

    // buffered reader for stdin
    private       static BufferedReader sStdInReader    = null;

    private static BufferedReader getStdIn() {
        if (sStdInReader == null) {
            sStdInReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return sStdInReader;
    }

    /* interface functions */

    // read from input
    public boolean  inReady() throws JVMIOException {
        try {
            return getStdIn().ready();
        } catch (IOException ioe) {
            throw new JVMIOException("Unable to open stdIn!");
        }
    }
    public String   inReadLine() throws JVMIOException {
        try {
                   // line is read and returned without termination character(s)
            return getStdIn().readLine();
        } catch (IOException ioe) {
            throw new JVMIOException("Unable to open stdIn!");
        }
    }

    // write to standard output
    public void     outWrite(String msg) {
        System.out.print(msg);
    }

    // write to standard error
    public void     errWrite(String msg) {
        System.err.print(msg);
    }

    // prompt for user input
    public void    promptForInput(String msg) {
        System.out.print(msg);
        System.out.flush();
    }

    // some text format stuff
    public String   getTab() {
        return sTAB;
    }
    public String   getEndl() {
        if (sENDL == null) {
            sENDL = System.getProperty("line.separator");
        }
        return sENDL;
    }

    // allow modification of fileName/path when reading files
    public String   filterFileName(String fileName) {
        return fileName; // not required on PC
    }

    // allow modification of library name
    public String   filterLibraryName(String name) {
        name = name.trim();
        if (name.length() < 1 || name.charAt(0) == '/') {
            return name;
        } else {
            return sLibraryPath + name;
        }
    }
}


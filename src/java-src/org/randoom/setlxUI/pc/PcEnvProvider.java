package org.randoom.setlxUI.pc;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.utilities.EnvironmentProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * This implementation provides access to the I/O mechanisms of PCs
 */
public class PcEnvProvider implements EnvironmentProvider {

    private final static String TAB = "\t";

    private final String         endl;
  /*package*/     String         libraryPath;

    // buffered reader for stdin
    private       BufferedReader stdInReader;

    public PcEnvProvider() {
        this.endl        = System.getProperty("line.separator");
        this.libraryPath = "";
        this.stdInReader = null;
    }

    /**
     * Get current StdIn reader.
     * @return StdIn reader.
     */
    private BufferedReader getStdIn() {
        if (stdInReader == null) {
            stdInReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return stdInReader;
    }

    /* interface functions */

    @Override
    public boolean  inReady() throws JVMIOException {
        try {
            return getStdIn().ready();
        } catch (final IOException ioe) {
            throw new JVMIOException("Unable to open stdIn!");
        }
    }
    @Override
    public String   inReadLine() throws JVMIOException {
        try {
                   // line is read and returned without termination character(s)
            return getStdIn().readLine();
        } catch (final IOException ioe) {
            throw new JVMIOException("Unable to open stdIn!");
        }
    }

    @Override
    public void     outWrite(final String msg) {
        System.out.print(msg);
    }

    @Override
    public void     errWrite(final String msg) {
        System.err.print(msg);
    }

    @Override
    public void    promptForInput(final String msg) {
        System.out.print(msg);
        System.out.flush();
    }

    @Override
    public String   getTab() {
        return TAB;
    }
    @Override
    public String   getEndl() {
        return endl;
    }

    @Override
    public String   filterFileName(final String fileName) {
        return fileName; // not required on PCs
    }

    @Override
    public String   filterLibraryName(String name) {
        name = name.trim();
        if (name.length() < 1 || name.charAt(0) == '/') {
            return name;
        } else {
            return libraryPath + name;
        }
    }
}


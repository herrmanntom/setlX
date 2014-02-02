package org.randoom.setlxUI.pc;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.utilities.EnvironmentProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * This implementation provides access to the I/O mechanisms of PCs.
 */
public class PcEnvProvider implements EnvironmentProvider {

    private final static String TAB = "\t";

    private final String         endl;
    private final String         osName;
    /**
     * Path where the library files are expected to reside.
     */
    private       String         libraryPath;

    // buffered reader for stdin
    private       BufferedReader stdInReader;

    /**
     * Create new PcEnvProvider.
     *
     * @param libraryPath Path where the library files are expected to reside.
     */
    public PcEnvProvider(final String libraryPath) {
        this.endl        = System.getProperty("line.separator");
        this.osName      = System.getProperty("os.name");
        this.libraryPath = libraryPath;
        this.stdInReader = null;
    }

    /**
     * Set new library path.
     *
     * @param libraryPath Path where the library files are expected to reside.
     */
    public void setlibraryPath(final String libraryPath) {
        this.libraryPath = libraryPath;
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
    public String   getOsID() {
        return osName;
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


package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.JVMIOException;

// This interface provides access to the I/O mechanisms of the target platform etc

public interface EnvironmentProvider {

    // read from input
    public abstract boolean inReady() throws JVMIOException;
                   // line is read and returned without termination character(s)
    public abstract String  inReadLine() throws JVMIOException;

    // write to standard output
    public abstract void    outWrite(final String msg);

    // write to standard error
    public abstract void    errWrite(final String msg);

    // prompt for user input
    public abstract void    promptForInput(final String msg);

    // some text format stuff
    public abstract String  getTab();
    public abstract String  getEndl();

    // allow modification of fileName/path when reading files
    public abstract String  filterFileName(final String fileName);

    // allow modification of library name
    public abstract String  filterLibraryName(final String name);

}


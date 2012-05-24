package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.JVMIOException;

// This interface provides access to the I/O mechanisms of the target platform etc

public interface EnvironmentProvider {

    /* I/O */

    // read from input
    public abstract boolean inReady() throws JVMIOException;
                   // line is read and returned without termination character(s)
    public abstract String  inReadLine() throws JVMIOException;

    // write to standard output
    public abstract void    outWrite(String msg);
    public abstract void    outFlush();

    // write to standard error
    public abstract void    errWrite(String msg);
    public abstract void    errFlush();

    // some text format stuff
    public abstract String  getTab();
    public abstract String  getEndl();

    /* other stuff */

    // number of CPUs (cores) in the executing system
    public abstract int     getNumberOfCores();

    // current time in ms
    public abstract long    currentTimeMillis();

}


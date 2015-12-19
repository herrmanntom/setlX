package org.randoom.setlx.utilities;

import java.util.List;

import org.randoom.setlx.exceptions.JVMIOException;

/**
 * This interface provides access to the I/O mechanisms of the target platform.
 */
public interface EnvironmentProvider {

    /**
     * Query if user provided input stream (e.g. stdin) has queued input.
     *
     * @return                True if input has queued input.
     * @throws JVMIOException Thrown in case of IO errors.
     */
    boolean inReady() throws JVMIOException;

    /**
     * Read a single line without termination character(s) from user provided
     * input stream (e.g. stdin).
     *
     * @return                Contents of the line read.
     * @throws JVMIOException Thrown in case of IO errors.
     */
    String  inReadLine() throws JVMIOException;

    /**
     * Write to standard output.
     *
     * @param msg Message to write.
     */
    void    outWrite(final String msg);

    /**
     * Write to standard error.
     *
     * @param msg Message to write.
     */
    void    errWrite(final String msg);

    /**
     * Display a message to the user, before querying for input.
     *
     * @param msg Message to display.
     */
    void    promptForInput(final String msg);

    /**
     * Display a question to the user, before forcing to select one of the
     * provided answers.
     *
     * @param question        Question to display.
     * @param answers         Non-empty list of questions to select from.
     * @return                Answer selected by the user.
     * @throws JVMIOException Thrown in case of IO errors.
     */
    String  promptSelectionFromAnswers(final String question, final List<String> answers) throws JVMIOException;

    /**
     * Get the tabulator character to use.
     *
     * @return Tabulator character.
     */
    String  getTab();

    /**
     * Get system dependent newline character sequence.
     *
     * @return Newline character sequence.
     */
    String  getEndl();

    /**
     * Get identifier for the operating system that setlX runs on.
     *
     * @return Identifier for the operating system executing setlX.
     */
    String  getOsID();

    /**
     * Allows modification of filename/path when reading files from within setlX.
     *
     * @param fileName Filename as passed to setlX via input/source code.
     * @return         Filename to use when opening for read/write.
     */
    String  filterFileName(final String fileName);

    /**
     * Allows modification of library-path when loading libraries from within setlX.
     *
     * @param name Filename as expected by setlX.
     * @return     Filename to use when opening for read/write.
     */
    String  filterLibraryName(final String name);

    /**
     * Get the stack size in kb to request from the VM for each new thread.
     *
     * @return Stack size in kb to request from the VM.
     */
    int getStackSizeWishInKb();

    /**
     * Get the stack size in kb to request from the VM for each new 'medium' thread.
     *
     * @return Stack size in kb to request from the VM.
     */
    int getMediumStackSizeWishInKb();

    /**
     * Get the stack size in kb to request from the VM for each new 'small' thread.
     *
     * @return Stack size in kb to request from the VM.
     */
    int getSmallStackSizeWishInKb();
}


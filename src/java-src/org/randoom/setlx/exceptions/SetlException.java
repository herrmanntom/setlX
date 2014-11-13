package org.randoom.setlx.exceptions;

import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all Exceptions occurring in SetlX.
 */
public abstract class SetlException extends Exception {

    private static final long         serialVersionUID = -3764480484946122585L;

    private        final List<String> trace;

    /**
     * Create a new SetlException.
     *
     * @param msg Message describing the exception that occurred.
     */
    protected SetlException(final String msg) {
        this(msg, null);
    }

    /**
     * Create a new SetlException.
     *
     * @param msg   Message describing the exception that occurred.
     * @param cause The cause (which is saved for later retrieval by the getCause() method).
     */
    protected SetlException(final String msg, Throwable cause) {
        super(msg, cause);
        trace = new ArrayList<String>();
        trace.add(msg);
    }

    /**
     * Add a message to the beginning of this exceptions "StackTrace".
     * Note that this StackTrace must only include SetlX Statements & Expressions.
     *
     * @param msg Message to add to the trace.
     */
    public void addToTrace(final String msg) {
        trace.add(msg);
    }

    /**
     * Print this exceptions trace of SetlX Statements & Expressions.
     * Messages in the middle of the trace are suppressed, when number of messages
     * exceeds the 'max_messages' parameter.
     *
     * @param state State of the currently executed SetlX program.
     */
    public void printExceptionsTrace(final State state) {
        final int end = trace.size();
        final int m_2 = state.getMaxExceptionMessages() / 2;
        for (int i = end - 1; i >= 0; --i) {
            // leave out some messages in the middle, which are most likely just clutter
            if (end > state.getMaxExceptionMessages() && i > m_2 - 1 && i < end - (m_2 + 1)) {
                if (i == m_2) {
                    state.errWriteLn(" ... \n     omitted " + (end - state.getMaxExceptionMessages()) + " messages\n ... ");
                }
            } else {
                state.errWriteLn(trace.get(i));
            }
        }
        state.errWriteStackTrace(getCause(), true);
    }
}


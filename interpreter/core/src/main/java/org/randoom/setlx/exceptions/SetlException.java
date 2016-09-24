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
    private        final List<List<String>> replay;

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
        trace = new ArrayList<>();
        trace.add(msg);
        replay = new ArrayList<>();
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
     * Add a message to the beginning of this exceptions replay.
     *
     * @param replayFrames Messages to add to the replay.
     */
    public void addToReplay(final List<String> replayFrames) {
        replay.add(replayFrames);
    }

    /**
     * Print this exceptions trace of SetlX Statements & Expressions and its replay.
     * Messages in the middle of the trace are suppressed, when number of messages
     * exceeds the 'max_messages' parameter.
     *
     * @param state State of the currently executed SetlX program.
     */
    public void printExceptionsTraceAndReplay(final State state) {
        printTrace(state, trace, true);
        state.errWriteStackTrace(getCause(), true);
        if (replay.size() > 0) {
            state.errWriteLn();
            state.errWriteLn("Replay: ");
            List<String> serializedReplays = new ArrayList<>();
            for (int i = 0; i < replay.size(); i++) {
                List<String> replayFrames = replay.get(i);
                String format = "%0" + String.valueOf(replay.size()).length() + "d.%0" + String.valueOf(replayFrames.size()).length() + "d: %s";
                for (int j = 0; j < replayFrames.size(); j++) {
                    serializedReplays.add(String.format(format, replay.size() - i, replayFrames.size() - j, replayFrames.get(j)));
                }
            }
            printTrace(state, serializedReplays, false);
        }
    }

    private static void printTrace(State state, List<String> trace, boolean reverse) {
        final int end = trace.size();
        final int m_2 = state.getMaxExceptionMessages() / 2;
        if (reverse) {
            for (int i = end - 1; i >= 0; --i) {
                printTraceLine(state, trace, end, m_2, i);
            }
        } else {
            for (int i = 0; i < end; ++i) {
                printTraceLine(state, trace, end, m_2, i);
            }
        }
    }

    private static void printTraceLine(State state, List<String> trace, int end, int m_2, int i) {
        // leave out some messages in the middle, which are most likely just clutter
        if (end > state.getMaxExceptionMessages() && i > m_2 - 1 && i < end - (m_2 + 1)) {
            if (i == m_2) {
                state.errWriteLn(" ... \n     omitted " + (end - state.getMaxExceptionMessages()) + " messages\n ... ");
            }
        } else {
            state.errWriteLn(trace.get(i));
        }
    }
}


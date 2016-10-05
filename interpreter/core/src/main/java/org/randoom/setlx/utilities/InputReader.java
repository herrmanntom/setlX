package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.JVMException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Reads from a provided input and returns an input stream
 */
public final class InputReader {

    /**
     * Reads from System.in until input is non-empty (e.g. includes characters other than EOL).
     *
     * End-of-line-sequences escaped with backslashes are ignored.
     *
     * If multiLine is true the user has to accepted input with an extra empty line.
     * Returned stream does _not_ include the extra empty line.
     *
     * @param state               Current state of the running setlX program.
     * @param endl                End-of-line sequence to use.
     * @param multiLine           Set to true if multiple line mode should be enabled.
     * @return                    Text read from the input.
     * @throws EndOfFileException Thrown in case the user inserts a EOF character.
     */
    public static InputStream getStream(final State state, final String endl, final boolean multiLine) throws EndOfFileException {
        final StringBuilder   input       = new StringBuilder();
              String          line        = null;
              int             endlAdded   = 0;
        try {
            while (true) {
                // line is read and returned without termination character(s)
                line   = state.inReadLine();
                if (line == null) {
                    throw new EndOfFileException();
                } else {
                    // append line
                    input.append(line);
                    if (line.endsWith("\\")) {
                        // remove backslash for added line
                        input.deleteCharAt(input.length() - 1);
                        continue;
                    } else {
                        // add line termination (e.g. Unix style '\n')
                        input.append(endl);  endlAdded += endl.length();
                    }
                }
                if (( ! multiLine || line.length() == 0) && input.length() > endlAdded) {
                    final byte[] byteArray = input.substring(0, input.length() - endl.length()).getBytes();
                    return new ByteArrayInputStream(byteArray);
                }
            }
        } catch (final JVMException e) {
            // should never happen
            throw new EndOfFileException("unable to read from stdin...", e);
        }
    }
}


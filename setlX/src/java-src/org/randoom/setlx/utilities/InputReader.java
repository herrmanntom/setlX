package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.utilities.Environment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Reads from system in and returns an input stream
 *
 * Reads from System.in until input is non-empty (e.g. includes characters other than EOL).
 *
 * Also endlines can be escaped with backslashes, if they should be ignored.
 *
 * If multiLine is true the user has to accepted input with an extra empty line.
 * Returned stream does _not_ include the extra empty line.
 */

public final class InputReader {
    private static String   EOL         = null;

    public static InputStream getStream() throws EndOfFileException {
        final StringBuilder   input       = new StringBuilder();
              String          line        = null;
              int             endlAdded   = 0;
        final boolean         multiLine   = Environment.isMultiLineEnabled();
        if (EOL == null) {
            EOL = Environment.getEndl();
        }
        try {
            while (true) {
                // line is read and returned without termination character(s)
                line   = Environment.inReadLine();
                if (line == null) {
                    throw new EndOfFileException("EndOfFile");
                } else {
                    // append line
                    input.append(line);
                    if (line.endsWith("\\")) {
                        // remove backslash for added line
                        input.deleteCharAt(input.length() - 1);
                        continue;
                    } else {
                        // add line termination (e.g. Unix style '\n')
                        input.append(EOL);  endlAdded += EOL.length();
                    }
                }
                if (( ! multiLine || line.length() == 0) && input.length() > endlAdded) {
                    byte[] byteArray = input.substring(0, input.length() - EOL.length()).getBytes();
                    return new ByteArrayInputStream(byteArray);
                }
            }
        } catch (EndOfFileException eof) {
            throw eof;
        } catch (JVMIOException e) {
            // should never happen
            throw new EndOfFileException("unable to read from stdin...");
        }
    }
}


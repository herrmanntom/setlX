package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.utilities.Environment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class InputReader {
    private static String         EOL = null;

    /**
     * Reads from system in and returns an input stream
     *
     * Reads from System.in until
     * (a) input is non-empty (e.g. includes characters other than EOL)
     * and
     * (b) user accepted input with an extra empty line
     *
     * Returned stream does _not_ include the extra empty line.
     *
     * By default EOL sequences read from System.in are replaced by
     * Unix style EOL ('\n'). This can be controlled by useNativeEOL().
     *
     * @return input from System.in, wrapped in new InputStream
     */
    public static InputStream getStream() throws EndOfFileException {
        StringBuilder   input       = new StringBuilder();
        String          line        = null;
        int             endlAdded   = 0;
        if (EOL == null) {
            EOL = Environment.getEndl();
        }
        try {
            while (true) {
                // line is read and returned without termination character(s)
                line   = Environment.inReadLine();
                // add line termination (e.g. Unix style '\n')
                input.append(line);
                input.append(EOL);  endlAdded += EOL.length();
                if (line == null) {
                    throw new EndOfFileException("EndOfFile");
                } else if (line.length() == 0 && input.length() > endlAdded) {
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


package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.JVMIOException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class InputReader {
    private static String         EOL = "\n";

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
        try {
            while (true) {
                // line is read and returned without termination character(s)
                line   = Environment.inReadLine();
                // add line termination (Unix style '\n' by default)
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

    /**
     * (re-)sets EOL sequence of read stream to native or Unix EOL
     *
     * (re-)sets EOL sequence of streams subsequently read by
     * getStream() to the EOL sequence native to the executing platform
     * or to Unix style '\n' EOL sequence
     *
     * @param useNativeEOL if true uses native EOL, Unix EOL otherwise
     */
    public static void useNativeEOL(boolean useNativeEOL) {
        if (useNativeEOL) {
            EOL = System.getProperty("line.separator");
        } else {
            EOL = "\n";
        }
    }
}


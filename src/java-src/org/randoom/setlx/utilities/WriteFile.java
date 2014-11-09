package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.FileNotWritableException;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class to handle writing files.
 */
public class WriteFile {

    /**
     * Write a file to storage.
     *
     * @param state                      Current state of the running setlX program.
     * @param content                    Content to write into the file.
     * @param fileName                   Name of file to write.
     * @param append                     Set to true to append instead of creating/overwriting.
     * @throws org.randoom.setlx.exceptions.FileNotWritableException Thrown in case the file cannot be created/appended.
     */
    public static void writeToFile(final State state, final String content, String fileName, final boolean append) throws FileNotWritableException {
        FileWriter     fWr = null;
        BufferedWriter out = null;
        try {
            // allow modification of fileName/path by environment provider
            fileName = state.filterFileName(fileName);
            // write file
            fWr = new FileWriter(fileName, append);
            out = new BufferedWriter(fWr);
            out.write(content);
        } catch (final FileNotFoundException fnfe) {
            throw new FileNotWritableException("File '" + fileName + "' could not be opened for writing.", fnfe);
        } catch (final IOException ioe) {
            throw new FileNotWritableException(ioe.getMessage(), ioe);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fWr != null) {
                    fWr.close();
                }
            } catch (final IOException ioe) {
                // don't care at this point
            }
        }
    }
}


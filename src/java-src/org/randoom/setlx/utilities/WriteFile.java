package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.FileNotWriteableException;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFile {

    public static void writeToFile(final State state, final String content, String fileName, final boolean append) throws FileNotWriteableException {
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
            throw new FileNotWriteableException("File '" + fileName + "' could not be opened for writing.");
        } catch (final IOException ioe) {
            throw new FileNotWriteableException(ioe.getMessage());
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


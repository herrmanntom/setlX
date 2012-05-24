package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.FileNotWriteableException;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class DumpSetlX {

    public static void dumpToFile(String program, String fileName, boolean append) throws FileNotWriteableException {
        // then dump to file
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(fileName, append));
            out.write(program);
        } catch (FileNotFoundException fnfe) {
            throw new FileNotWriteableException("File '" + fileName + "' could not be opened for writing.");
        } catch (IOException ioe) {
            throw new FileNotWriteableException(ioe.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
                // don't care at this point
            }
        }
    }
}


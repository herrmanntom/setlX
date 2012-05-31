package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.FileNotReadableException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

// readFile(fileName)            : reads file and returns list of strings, each
//                                 representing a single line of the file read

public class PD_readFile extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_readFile();

    private PD_readFile() {
        super("readFile");
        addParameter("fileName");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException, FileNotReadableException {
        Value   fileArg = args.get(0);
        if ( ! (fileArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Expression-argument '" + fileArg + "' is not a string."
            );
        }

        // get name of file to be read
        String          fileName    = fileArg.getUnquotedString();
        // allow modification of fileName/path by environment provider
        fileName = Environment.filterFileName(fileName);

        // read file
        FileInputStream fstream     = null;
        DataInputStream fIn         = null;
        BufferedReader  fBr         = null;

        SetlList        fileContent = new SetlList();

        try {
            fstream     = new FileInputStream(fileName);
            fIn         = new DataInputStream(fstream);
            fBr         = new BufferedReader(new InputStreamReader(fIn));
            String line = null;

            while ((line = fBr.readLine()) != null) {
                fileContent.addMember(new SetlString(line));
            }

            return fileContent;

        } catch (FileNotFoundException fnfe) {
            throw new FileNotReadableException("File '" + fileName + "' does not exist.");
        } catch (IOException ioe) {
            throw new FileNotReadableException("Unable to read file '" + fileName + "'.");
        } finally {
            try {
                if (fBr != null) {
                    fBr.close();
                }
                if (fIn != null) {
                    fIn.close();
                }
                if (fstream != null) {
                    fstream.close();
                }
            } catch (IOException ioe) { /* who cares */ }
        }
    }
}


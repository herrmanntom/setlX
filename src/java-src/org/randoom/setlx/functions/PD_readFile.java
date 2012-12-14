package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.FileNotReadableException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.State;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

// readFile(fileName [, listOfLineNumbers]) :
//                                 Reads a file and returns list of strings, each
//                                 representing a single line of the file read.
//                                 When the optional parameter `listOfLineNumbers/
//                                 is used, only lines in this list will be read.

public class PD_readFile extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_readFile();

    private PD_readFile() {
        super("readFile");
        addParameter("fileName");
        addParameter("listOfLineNumbers");
        allowFewerParameters();
    }

    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        if (args.size() < 1) {
            String error = "Procedure is defined with a larger number of parameters ";
            error +=       "(1 or 2).";
            throw new IncorrectNumberOfParametersException(error);
        }

        final Value fileArg = args.get(0);
        if ( ! (fileArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "FileName-argument '" + fileArg + "' is not a string."
            );
        }

        HashSet<Integer> lineNumbers = null;
        if (args.size() == 2) {
            Value numbers = args.get(1);
            if ( ! (numbers instanceof CollectionValue)) {
                throw new IncompatibleTypeException(
                    "ListOfLineNumbers-argument '" + numbers + "' is not a collection value."
                );
            }
            lineNumbers = new HashSet<Integer>(numbers.size());
            for (Value num : (CollectionValue) numbers) {
                if (num.isInteger() == SetlBoolean.FALSE) {
                    throw new IncompatibleTypeException(
                        "Value '" + num + "' in listOfLineNumbers is not an integer."
                    );
                }
                final int n = ((Rational) num).intValue();
                if (n > 0) {
                    lineNumbers.add(n);
                } else {
                    throw new IncompatibleTypeException(
                        "Value '" + num + "' in listOfLineNumbers is illogical."
                    );
                }
            }
        }

        // get name of file to be read and allow modification of fileName/path by environment provider
        final String    fileName    = Environment.filterFileName(fileArg.getUnquotedString());

        // read file
        FileInputStream fstream     = null;
        DataInputStream fIn         = null;
        BufferedReader  fBr         = null;
        int             lineCounter = 0;

        final SetlList  fileContent = new SetlList();

        try {
            fstream     = new FileInputStream(fileName);
            fIn         = new DataInputStream(fstream);
            fBr         = new BufferedReader(new InputStreamReader(fIn));
            String line = null;

            while ((line = fBr.readLine()) != null) {
                if (lineNumbers == null || lineNumbers.contains(++lineCounter)) {
                    fileContent.addMember(new SetlString(line));

                    if (lineNumbers != null) {
                        lineNumbers.remove(lineCounter);
                        if (lineNumbers.isEmpty()) {
                            break;
                        }
                    }
                }
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


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.FileNotReadableException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.*;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * readFile(fileName [, listOfLineNumbers]) :
 *                               Reads a file and returns list of strings, each
 *                               representing a single line of the file read.
 *                               When the optional parameter `listOfLineNumbers'
 *                               is used, only lines in this list will be read.
 */
public class PD_readFile extends PreDefinedProcedure {

    private final static ParameterDefinition FILE_NAME            = createParameter("fileName");
    private final static ParameterDefinition LIST_OF_LINE_NUMBERS = createOptionalParameter("listOfLineNumbers", Om.OM);

    /** Definition of the PreDefinedProcedure `readFile'. */
    public  final static PreDefinedProcedure DEFINITION           = new PD_readFile();

    private PD_readFile() {
        super();
        addParameter(FILE_NAME);
        addParameter(LIST_OF_LINE_NUMBERS);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value fileArg = args.get(FILE_NAME);
        if ( ! (fileArg instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "FileName-argument '" + fileArg.toString(state) + "' is not a string."
            );
        }

        HashSet<Integer> lineNumbers = null;
        final Value numbers = args.get(LIST_OF_LINE_NUMBERS);
        if (numbers != Om.OM) {
            if ( ! (numbers instanceof CollectionValue)) {
                throw new IncompatibleTypeException(
                    "ListOfLineNumbers-argument '" + numbers.toString(state) + "' is not a collection value."
                );
            }
            lineNumbers = new HashSet<Integer>(numbers.size());
            for (final Value num : (CollectionValue) numbers) {
                if (num.isInteger() == SetlBoolean.FALSE) {
                    throw new IncompatibleTypeException(
                        "Value '" + num.toString(state) + "' in listOfLineNumbers is not an integer."
                    );
                }
                final int n = num.jIntValue();
                if (n > 0) {
                    lineNumbers.add(n);
                } else {
                    throw new IncompatibleTypeException(
                        "Value '" + num.toString(state) + "' in listOfLineNumbers is illogical."
                    );
                }
            }
        }

        // get name of file to be read and allow modification of fileName/path by environment provider
        final String    fileName    = state.filterFileName(fileArg.getUnquotedString(state));

        // read file
        FileInputStream fstream     = null;
        DataInputStream fIn         = null;
        BufferedReader  fBr         = null;
        int             lineCounter = 0;

        final SetlList  fileContent = new SetlList();

        try {
            fstream = new FileInputStream(fileName);
            fIn     = new DataInputStream(fstream);
            fBr     = new BufferedReader(new InputStreamReader(fIn));
            String line;

            while ((line = fBr.readLine()) != null) {
                if (lineNumbers == null || lineNumbers.contains(++lineCounter)) {
                    fileContent.addMember(state, new SetlString(line));

                    if (lineNumbers != null) {
                        lineNumbers.remove(lineCounter);
                        if (lineNumbers.isEmpty()) {
                            break;
                        }
                    }
                }
            }

            return fileContent;

        } catch (final FileNotFoundException fnfe) {
            throw new FileNotReadableException("File '" + fileName + "' does not exist.", fnfe);
        } catch (final IOException ioe) {
            throw new FileNotReadableException("Unable to read file '" + fileName + "'.", ioe);
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
            } catch (final IOException ioe) { /* who cares */ }
        }
    }
}


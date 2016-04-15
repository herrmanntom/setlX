package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.FileNotWritableException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.WriteFile;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * writeFile(fileName, content)  : Writes a list of strings into a file, each
 *                                 string representing a single line.
 */
public class PD_writeFile extends PreDefinedProcedure {

    private final static ParameterDefinition FILE_NAME  = createParameter("fileName");
    private final static ParameterDefinition CONTENTS   = createParameter("contents");

    /** Definition of the PreDefinedProcedure `writeFile'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_writeFile();

    /**
     * Create a new writeFile function.
     */
    protected PD_writeFile() {
        super();
        addParameter(FILE_NAME);
        addParameter(CONTENTS);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return exec(state, args, false);
    }

    /**
     * Execute writeFile() functionality.
     *
     * @param state          Current state of the running setlX program.
     * @param args           Values of the call-parameters.
     * @param append         Defines if file should be appended, instead of newly created.
     * @return               SetlBoolean.TRUE if writing was successful.
     * @throws IncompatibleTypeException Thrown in case the wrong parameters are supplied.
     * @throws FileNotWritableException File to be written cannot be written.
     */
    protected Value exec(final State state, final HashMap<ParameterDefinition, Value> args, final boolean append) throws SetlException {
        final Value  fileArg = args.get(FILE_NAME);
        if (fileArg.isString() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("FileName-argument '" + fileArg.toString(state) + "' is not a string.");
        }
        final Value contentArg = args.get(CONTENTS);

        // get name of file to be written
        final String    fileName = fileArg.getUnquotedString(state);
        // get content to be written into the file
        CollectionValue content;
        if (contentArg instanceof CollectionValue && ! (contentArg.isTerm() == SetlBoolean.TRUE || contentArg.isString() == SetlBoolean.TRUE)) {
            content = (CollectionValue) contentArg;
        } else {
            content = new SetlList(1);
            content.addMember(state, contentArg);
        }

        final boolean verbose = state.isPrintVerbose();
        state.setPrintVerbose(true);
        final String  endl    = state.getEndl();
        state.setPrintVerbose(verbose);

        // write file
        final StringBuilder sb = new StringBuilder();
        for (final Value v : content) {
            v.appendUnquotedString(state, sb, 0);
            sb.append(endl);
        }
        WriteFile.writeToFile(state, sb.toString(), fileName, append);

        return SetlBoolean.TRUE;
    }
}


package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.IncorrectNumberOfParametersException;
import interpreter.exceptions.SetlException;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlError;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.statements.Block;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

import java.util.List;

// load(path [, output])   : loads SetlX source code file and executes it, if `output' is true results of statements are printed when in interactive mode

public class PD_load extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_load();

    private PD_load() {
        super("load");
        addParameter("path_to_setlX_file");
        addParameter("output");             // optional parameter
        allowFewerParameters();
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        if (args.size() < 1) {
            throw new IncorrectNumberOfParametersException("Procedure 'load' is defined with either one or two parameters.");
        }
        Value   filePath            = args.get(0);
        if ( ! (filePath instanceof SetlString)) {
            throw new IncompatibleTypeException("Path-argument '" + filePath + "' is not a string.");
        }

        // check and get optional 2nd parameter
        Value   doNotDisableOutput  = SetlBoolean.FALSE;
        if (args.size() == 2) {
            doNotDisableOutput  = args.get(1);
            if ( ! (doNotDisableOutput instanceof SetlBoolean)) {
                throw new IncompatibleTypeException("do-not-disable-output-argument '" + doNotDisableOutput + "' is not a Boolean value.");
            }
        }

        // get string of file path to be parsed
        String  file    = filePath.getUnquotedString();

        // parse the file
        ParseSetlX.resetErrorCount();
        Block   blk     = ParseSetlX.parseFile(file);

        // execute the contents
        boolean interactive = Environment.isInteractive();
        try {
            if (doNotDisableOutput == SetlBoolean.FALSE) {
                Environment.setInteractive(false);
            }
            blk.execute();
        } finally {
            Environment.setInteractive(interactive);
        }

        // newline to visually separate result
        if (interactive && doNotDisableOutput == SetlBoolean.TRUE) {
            System.out.println();
        }

        // everything is good
        return SetlBoolean.TRUE;
    }
}


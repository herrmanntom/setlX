package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.ParseSetlX;

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
            throw new IncorrectNumberOfParametersException(
                "Procedure 'load' is defined with either one or two parameters."
            );
        }
        Value   filePath            = args.get(0);
        if ( ! (filePath instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Path-argument '" + filePath + "' is not a string."
            );
        }

        // check and get optional 2nd parameter
        Value   doNotDisableOutput  = SetlBoolean.FALSE;
        if (args.size() == 2) {
            doNotDisableOutput  = args.get(1);
            if ( ! (doNotDisableOutput instanceof SetlBoolean)) {
                throw new IncompatibleTypeException(
                    "do-not-disable-output-argument '" + doNotDisableOutput + "' is not a Boolean value."
                );
            }
        }

        // get string of file path to be parsed
        String  file    = filePath.getUnquotedString();

        // parse the file
        ParseSetlX.resetErrorCount();
        Block   blk     = ParseSetlX.parseFile(file);

        // execute the contents
        boolean printAfterEval = Environment.isPrintAfterEval();
        try {
            if (doNotDisableOutput == SetlBoolean.FALSE) {
                Environment.setPrintAfterEval(false);
            }
            blk.execute();
        } finally {
            Environment.setPrintAfterEval(printAfterEval);
        }

        // newline to visually separate result
        if (printAfterEval && doNotDisableOutput == SetlBoolean.TRUE) {
            Environment.outWriteLn();
        }

        // everything is good
        return SetlBoolean.TRUE;
    }
}


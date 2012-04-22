package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.io.IOException;
import java.util.List;

// get()                   : reads a single line from stdin

public class PD_get extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_get();

    private PD_get() {
        super("get");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        Value          inputValue = Om.OM;
        String         input      = null;
        try {
            Environment.promptForStdInOnStdOut(": ");
            input = Environment.getStdIn().readLine();
        } catch (IOException ioe) {
            System.err.println(ioe);
            System.err.println("IO error trying to read from stdin!");
        }
        if (input != null) {
            inputValue = new SetlString(input);
        } else {
            inputValue = Om.OM;
        }

        return inputValue;
    }
}


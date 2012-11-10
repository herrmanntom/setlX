package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.State;

import java.util.List;

// get(message, ...)             : prompts the user with `message', then reads a single line from stdin

public class PD_get extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_get();

    private PD_get() {
        super("get");
        addParameter("message");
        enableUnlimitedParameters();
        allowFewerParameters();
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        Value          inputValue = Om.OM;
        String         input      = null;
        String         prompt     = null;
        if (args.size() == 0) {
            prompt = ": ";
        } else {
            prompt = "";
            for (Value arg : args) {
                prompt += arg.getUnquotedString();
            }
        }
        try {
            Environment.prompt(prompt);
            input = Environment.inReadLine();
        } catch (JVMIOException ioe) {
            Environment.errWriteLn("IO error trying to read from stdin!");
        }

        if (input != null) {
            inputValue = new SetlString(input);
        } else {
            inputValue = Om.OM;
        }

        return inputValue;
    }
}


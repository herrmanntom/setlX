package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// read(message, ...)            : prompts the user with `message', then reads from
//                                 stdin until user enters at least one character,
//                                 converts input into integer or real if possible

public class PD_read extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_read();

    private PD_read() {
        super("read");
        addParameter("message");
        enableUnlimitedParameters();
        allowFewerParameters();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        Value          inputValue = Om.OM;
        String         input      = null;
        String         prompt     = null;
        if (args.size() == 0) {
            prompt = ": ";
        } else {
            prompt = "";
            for (final Value arg : args) {
                prompt += arg.getUnquotedString();
            }
        }
        try {
            do {
                state.prompt(prompt);
                input = state.inReadLine();
                if (input != null) {
                    input = input.trim();
                }
            } while (input != null && input.equals(""));
        } catch (final JVMIOException ioe) {
            state.errWriteLn("IO error trying to read from stdin!");
        }

        if (input != null) {
            inputValue = new SetlString(input);
            if (inputValue.toInteger() != Om.OM) {
                inputValue = inputValue.toInteger();
            } else if (inputValue.toReal() != Om.OM) {
                inputValue = inputValue.toReal();
            }
        } else {
            inputValue = Om.OM;
        }

        return inputValue;
    }
}


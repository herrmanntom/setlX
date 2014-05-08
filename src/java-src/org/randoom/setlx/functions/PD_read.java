package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * read(message, ...) : Prompts the user with `message', then reads from
 *                      stdin until user enters at least one character.
 *                      Converts input into integer or double if possible.
 */
public class PD_read extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `read'. */
    public final static PreDefinedProcedure DEFINITION = new PD_read();

    private PD_read() {
        super();
        addParameter("message");
        enableUnlimitedParameters();
        setMinimumNumberOfParameters(0);
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        Value               inputValue = Om.OM;
        String              input      = null;
        final StringBuilder prompt     = new StringBuilder();
        if (args.size() == 0) {
            prompt.append(": ");
        } else {
            for (final Value arg : args) {
                arg.appendUnquotedString(state, prompt, 0);
            }
        }
        try {
            do {
                state.prompt(prompt.toString());
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
            if (inputValue.toInteger(state) != Om.OM) {
                inputValue = inputValue.toInteger(state);
            } else if (inputValue.toDouble(state) != Om.OM) {
                inputValue = inputValue.toDouble(state);
            }
        } else {
            inputValue = Om.OM;
        }

        return inputValue;
    }
}


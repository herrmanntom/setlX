package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * get(message, ...) : Prompts the user with `message', then reads a single line from standard in (stdin).
 */
public class PD_get extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `get'. */
    public final static PreDefinedProcedure DEFINITION = new PD_get();

    private PD_get() {
        super();
        addParameter("message");
        enableUnlimitedParameters();
        setMinimumNumberOfParameters(0);
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        Value               inputValue = Om.OM;
        String              input      = null;
        final StringBuilder prompt     = new StringBuilder();
        if (args.isEmpty()) {
            prompt.append(": ");
        } else {
            for (final Value arg : args) {
                arg.appendUnquotedString(state, prompt, 0);
            }
        }
        try {
            state.prompt(prompt.toString());
            input = state.inReadLine();
        } catch (final JVMIOException ioe) {
            state.errWriteLn("IO error trying to read from stdin!");
        }

        if (input != null) {
            inputValue = new SetlString(input);
        } else {
            inputValue = Om.OM;
        }

        return inputValue;
    }
}


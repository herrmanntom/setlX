package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// stop(id)                      : interrupt the execution and show a prompt with
//                                 some debugging functionality.

public class PD_stop extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION   = new PD_stop();

    private      static boolean             firstTimeUse = true;

    private PD_stop() {
        super();
        addParameter("id");
        allowFewerParameters();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        String input   = null;

        String message = this.getName() + "(";
        if (args.size() == 1) {
            message += args.get(0).toString(state);
        }
        message += ")";

        while (true) {
            String prompt = message;
            if (firstTimeUse) {
                prompt = "Execution interrupted via " + message + ":\n"
                        +"Confirm without any input to continue execution.\n"
                        +"Enter a variable name to display its current value.\n"
                        +"Enter `All' to print term representation of current scope.\n"
                        + message;
                firstTimeUse = false;
            }
            prompt += ": ";

            try {
                state.prompt(prompt);
                input = state.inReadLine();
                if (input != null) {
                    input = input.trim();
                } else {
                    input = "";
                }
            } catch (final JVMIOException ioe) {
                state.errWriteLn("IO error trying to read from stdin!");
            }

            if (input.equals("")) {
                break;
            } else if (input.equals("All")) {
                state.outWriteLn("\tAll == " + state.scopeToTerm().toString(state));
            } else if (input.matches("[a-z][a-zA-z_0-9]*")) {
                state.outWriteLn("\t" + input + " == " + state.findValue(input).getUnquotedString());
            } else {
                state.errWriteLn("\tInput is invalid!");
            }
        }

        return Rational.ZERO;
    }
}


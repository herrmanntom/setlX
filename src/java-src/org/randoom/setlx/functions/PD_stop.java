package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;
import java.util.Map.Entry;

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
                        +"Enter comma separated variable names to display their current value.\n"
                        +"Enter `All' to display all variables in the current scope.\n"
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
            } else {
                final String[] cmds = input.split(",");
                if (cmds.length > 0) {
                    for (String cmd : cmds) {
                        cmd = cmd.trim();
                        if (cmd.equals("All")) {
                            for (final Entry<String, Value> binding : state.getAllVariablesInScope().entrySet()) {
                                state.outWriteLn("    " + binding.getKey() + " == " + binding.getValue().toString(state));
                            }
                        } else if (cmd.matches("[a-z][a-zA-z_0-9]*")) {
                            state.outWriteLn("    " + cmd + " == " + state.findValue(cmd).toString(state));
                        } else {
                            state.errWriteLn("    Input '" + cmd + "' is invalid!");
                        }
                    }
                } else {
                    state.errWriteLn("    Input '" + input + "' is invalid!");
                }
            }
        }

        return Rational.ZERO;
    }
}


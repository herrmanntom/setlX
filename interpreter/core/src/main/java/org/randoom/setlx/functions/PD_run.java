package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;
import java.util.Locale;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * run(command) : Executes a system command and returns the result as a list of output and error messages.
 */
public class PD_run extends PreDefinedProcedure {

    private final static ParameterDefinition COMMAND    = createParameter("command");

    /** Definition of the PreDefinedProcedure `run'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_run();

    private PD_run() {
        super();
        addParameter(COMMAND);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
        if ( ! (args.get(COMMAND) instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Argument '" + args.get(COMMAND).toString(state) + "' is not a string."
            );
        }

        final String command = args.get(COMMAND).getUnquotedString(state);

        try {
            final String   os = System.getProperty("os.name").toLowerCase(Locale.US);
                  String   shell;
                  String   options;
            if (os.contains("nix") || os.contains("nux")) { // Unix/Linux
                shell   = "sh";
                options = "-c";
            } else if (os.contains("mac")) { // MacOS X
                shell   = "sh";
                options = "-c";
            } else if (os.contains("win")) { // Windows
                shell   = "cmd";
                options = "/c";
            } else {
                shell   = "";
                options = "";
            }

            final Process p = Runtime.getRuntime().exec(new String[]{shell, options, command});

            p.waitFor();

            final BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
            final BufferedReader error  = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            final SetlList       out    = new SetlList();
            final SetlList       err    = new SetlList();

            String line;
            while ((line = output.readLine()) != null) {
                out.addMember(state, new SetlString(line));
            }
            output.close();

            while ((line = error.readLine()) != null) {
                err.addMember(state, new SetlString(line));
            }
            error.close();

            final SetlList result = new SetlList(2);
            result.addMember(state, out);
            result.addMember(state, err);

            return result;
        } catch (final Exception e) {
            return Om.OM;
        }
    }
}


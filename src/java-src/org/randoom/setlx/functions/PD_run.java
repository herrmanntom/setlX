package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.utilities.State;

import java.util.List;
import java.util.Locale;
import java.io.InputStreamReader;
import java.io.BufferedReader;

// run(command)                  : executes a system command and returns the result as a list of output and error messages

public class PD_run extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_run();

    private PD_run() {
        super();
        addParameter("command");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        if ( ! (args.get(0) instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Argument '" + args.get(0) + "' is not a string."
            );
        }

        final String command = args.get(0).getUnquotedString();

        try {
            final String   os      = System.getProperty("os.name").toLowerCase(Locale.US);
                  String   shell   = null;
                  String   options = null;
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

            String         line   = null;
            while ((line = output.readLine()) != null) {
                out.addMember(state, new SetlString(line));
            }
            output.close();

            while ((line = error.readLine()) != null) {
                err.addMember(state, new SetlString(line));
            }
            error.close();

            final SetlList       result = new SetlList(2);
            result.addMember(state, out);
            result.addMember(state, err);

            return result;
        } catch (final Exception e) {
            return Om.OM;
        }
    }
}


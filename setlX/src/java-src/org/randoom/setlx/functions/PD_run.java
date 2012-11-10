package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.utilities.State;

import java.util.List;
import java.io.InputStreamReader;
import java.io.BufferedReader;

// run(command)                  : executes a system command and returns the result as a list of output and error messages

public class PD_run extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_run();

    private PD_run() {
        super("run");
        addParameter("command");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        if ( ! (args.get(0) instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Argument '" + args.get(0) + "' is not a string."
            );
        }

        final String command = args.get(0).getUnquotedString();

        try {
            final String   os      = System.getProperty("os.name").toLowerCase();
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

            Process p = Runtime.getRuntime().exec(new String[]{shell, options, command});

            p.waitFor();

            BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error  = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            SetlList       out    = new SetlList();
            SetlList       err    = new SetlList();

            String         line   = null;
            while ((line = output.readLine()) != null) {
                out.addMember(new SetlString(line));
            }
            output.close();

            while ((line = error.readLine()) != null) {
                err.addMember(new SetlString(line));
            }
            error.close();

            SetlList       result = new SetlList(2);
            result.addMember(out);
            result.addMember(err);

            return result;
        } catch (Exception e) {
            return Om.OM;
        }
    }
}


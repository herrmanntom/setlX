package interpreter.functions;

import interpreter.types.SetlOm;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

public class PD_get extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_get();

    private PD_get() {
        super("get");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        if (Environment.isInteractive()) {
            System.out.println("/*");
        }

        BufferedReader br         = new BufferedReader(new InputStreamReader(System.in));
        Value          inputValue = SetlOm.OM;
        String         input      = null;
        try {
            System.out.print(":");
            input = br.readLine();
        } catch (IOException ioe) {
            System.err.println(ioe);
            System.err.println("IO error trying to read from stdin!");
        }
        if (input != null) {
            inputValue = new SetlString(input);
        } else {
            inputValue = SetlOm.OM;
        }

        if (Environment.isInteractive()) {
            System.out.println("*/");
        }

        return inputValue;
    }
}


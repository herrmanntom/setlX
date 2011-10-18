package interpreter.functions;

import interpreter.types.SetlDefinitionParameter;
import interpreter.types.SetlInt;
import interpreter.types.SetlOm;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

public class PD_read extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_read();

    private PD_read() {
        super("read");
        addParameter(new SetlDefinitionParameter("variable", SetlDefinitionParameter.READ_WRITE));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        if (Environment.isInteractive()) {
            System.out.println("/*");
        }
        BufferedReader br         = new BufferedReader(new InputStreamReader(System.in));
        Value          inputValue = SetlOm.OM;
        String         input      = null;
        try {
            do {
                System.out.print(":");
                input = br.readLine().trim();
            } while (input != null && input.equals(""));
        } catch (IOException ioe) {
            System.err.println(ioe);
            System.err.println("IO error trying to read from stdin!");
        }

        if (input != null) {
            if (input.matches("^\\d+$")) {
                inputValue = new SetlInt(input);
            } else {
                inputValue = new SetlString(input);
            }
        } else {
            inputValue = SetlOm.OM;
        }
        // reading the input value is finished, write it back into the outer env
        writeBackVars.add(inputValue);

        if (Environment.isInteractive()) {
            System.out.println("*/");
        }

        return inputValue.clone();
    }
}

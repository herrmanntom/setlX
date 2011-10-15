package interpreter.functions;

import interpreter.Environment;
import interpreter.types.SetlInt;
import interpreter.types.SetlOm;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

public class PD_read extends PreDefinedFunction {
    public final static PD_read DEFINITION = new PD_read();

    private PD_read() {
        super("read");
    }

    public Value call(List<Value> args) {
        if (Environment.isInteractive()) {
            System.out.println("/*");
        }
        BufferedReader br         = new BufferedReader(new InputStreamReader(System.in));
        Value          inputValue = SetlOm.OM;
        for (Value arg: args) {
            String input   = null;
            String varName = ((SetlString) arg).toStringForPrint();
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
            Environment.putValue(varName, inputValue);
        }
        if (Environment.isInteractive()) {
            System.out.println("*/");
        }
        return inputValue.clone();
    }

    public boolean writeVars() {
        return true;
    }
}

package interpreter.functions;

import interpreter.Environment;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.SetlOm;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

public class PD_get extends PreDefinedFunction {
    public final static PD_get DEFINITION = new PD_get();

    private PD_get() {
        super("get");
    }

    public Value call(List<Value> args, boolean returnCollection) throws UndefinedOperationException {
        if (returnCollection) {
            throw new UndefinedOperationException("Incorrect set of brackets for function call.");
        }
        if (Environment.isInteractive()) {
            System.out.println("/*");
        }
        BufferedReader br         = new BufferedReader(new InputStreamReader(System.in));
        Value          inputValue = SetlOm.OM;
        for (Value arg: args) {
            String input   = null;
            String varName = ((SetlString) arg).toStringForPrint();
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

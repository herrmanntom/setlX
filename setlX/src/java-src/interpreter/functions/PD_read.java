package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.SetlInt;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

// read()                  : reads from stdin until user enters at least one character, converts input into integer or real if possible

public class PD_read extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_read();

    private PD_read() {
        super("read");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        BufferedReader br         = new BufferedReader(new InputStreamReader(System.in));
        Value          inputValue = Om.OM;
        String         input      = null;
        try {
            do {
                System.out.print(": ");
                input = br.readLine().trim();
            } while (input != null && input.equals(""));
        } catch (IOException ioe) {
            System.err.println(ioe);
            System.err.println("IO error trying to read from stdin!");
        }

        if (input != null) {
            inputValue = new SetlString(input);
            if (inputValue.toInteger() != Om.OM) {
                inputValue = inputValue.toInteger();
            } else if (inputValue.toReal() != Om.OM) {
                inputValue = inputValue.toReal();
            }
        } else {
            inputValue = Om.OM;
        }

        return inputValue;
    }
}


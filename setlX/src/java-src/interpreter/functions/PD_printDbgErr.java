package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.List;

public class PD_printDbgErr extends PD_printDbg {
    public final static PreDefinedFunction DEFINITION = new PD_printDbgErr();

    private PD_printDbgErr() {
        super("printDbgErr");
    }

    protected void print(String txt) {
        System.err.print(txt);
    }
}


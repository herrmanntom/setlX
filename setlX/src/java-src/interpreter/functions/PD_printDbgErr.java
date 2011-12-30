package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.List;

// printDbgErr(value, ...) : same as print, but does not interprete the string ($-signs) and prints into stderr

public class PD_printDbgErr extends PD_printDbg {
    public final static PreDefinedFunction DEFINITION = new PD_printDbgErr();

    private PD_printDbgErr() {
        super("printDbgErr");
    }

    protected void print(String txt) {
        System.err.print(txt);
    }
}


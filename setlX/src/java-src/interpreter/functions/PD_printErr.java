package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.List;

public class PD_printErr extends PD_print {
    public final static PreDefinedFunction DEFINITION = new PD_printErr();

    private PD_printErr() {
        super("printErr");
    }

    protected void print(String txt) {
        System.err.print(txt);
    }
}


package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.List;

public class PD_printDbg extends PD_print {
    public final static PreDefinedFunction DEFINITION = new PD_printDbg();

    private PD_printDbg() {
        super("printDbg");
    }

    protected void prePrint() {
    }

    protected void print(String txt) {
        System.out.print(txt);
    }

    protected void postPrint() {
    }
}


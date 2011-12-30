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
        interprete = false;
    }

    protected PD_printDbg(String fName) {
        super(fName);
    }

    protected void prePrint() {
        interprete = Environment.isInterpreteStrings();
        Environment.setInterpreteStrings(false);
    }

    protected void print(String txt) {
        System.out.print(txt);
    }

    protected void postPrint() {
        Environment.setInterpreteStrings(interprete);
    }
}


package interpreter.functions;

import interpreter.Environment;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.SetlOm;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.List;

public class PD_print extends PreDefinedFunction {
    public final static PD_print DEFINITION = new PD_print();

    private PD_print() {
        super("print");
    }

    public Value call(List<Value> args, boolean returnCollection) {
        if (returnCollection) {
            throw new UndefinedOperationException("Incorrect set of brackets for function call.");
        }
        if (Environment.isInteractive()) {
            System.out.println("/*");
        }
        for (Value arg : args) {
            System.out.print(arg.toStringForPrint());
        }
        System.out.println();
        if (Environment.isInteractive()) {
            System.out.println("*/");
        }
        return SetlOm.OM;
    }

    public boolean writeVars() {
        return false;
    }
}

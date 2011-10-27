package interpreter.functions;

import interpreter.types.SetlOm;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_print extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_print();

    private PD_print() {
        super("print");
        addParameter(new ParameterDef("firstValue"));
        enableUnlimitedParameters();
        allowFewerParameters();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        for (Value arg : args) {
            System.out.print(arg.toStringForPrint());
        }
        System.out.println();
        return SetlOm.OM;
    }
}


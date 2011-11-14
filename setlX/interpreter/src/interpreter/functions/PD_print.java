package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.List;

public class PD_print extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_print();

    private PD_print() {
        super("print");
        addParameter("value");
        enableUnlimitedParameters();
        allowFewerParameters();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        for (Value arg : args) {
            String text = arg.toString();
            // Strip out double quotes when printing strings
            int length = text.length();
            if (length >= 2 && text.charAt(0) == '"' && text.charAt(length - 1) == '"') {
                text = text.substring(1, length - 1);
            }
            System.out.print(text);
        }
        System.out.println();
        return Om.OM;
    }
}


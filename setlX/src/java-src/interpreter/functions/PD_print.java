package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.List;

public class PD_print extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_print();
//    private             boolean            interprete;

    private PD_print() {
        this("print");
    }

    protected PD_print(String fName) {
        super(fName);
//        interprete = true;
        addParameter("value");
        enableUnlimitedParameters();
        allowFewerParameters();
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        prePrint();
        for (Value arg : args) {
            String text = arg.toString();
            // Strip out double quotes when printing strings
            int length = text.length();
            if (length >= 2 && text.charAt(0) == '"' && text.charAt(length - 1) == '"') {
                text = text.substring(1, length - 1);
            }
            print(text);
        }
        print("\n");
        postPrint();
        return Om.OM;
    }

    protected void prePrint() {
//        interprete = Environment.isInterpreteStrings();
//        Environment.setInterpreteStrings(true);
    }

    protected void print(String txt) {
        System.out.print(txt);
    }

    protected void postPrint() {
//        Environment.setInterpreteStrings(interprete);
    }
}


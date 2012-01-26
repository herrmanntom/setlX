package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;

import java.util.List;

// print(value)            : prints string representation of provided value into stdout

public class PD_print extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_print();
    protected           boolean            interprete;

    private PD_print() {
        this("print");
    }

    protected PD_print(String fName) {
        super(fName);
        interprete = true;
        addParameter("value");
        enableUnlimitedParameters();
        allowFewerParameters();
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        for (Value arg : args) {
            print(arg.getUnquotedString());
        }
        print("\n");
        return Om.OM;
    }

    protected void print(String txt) {
        System.out.print(txt);
    }
}


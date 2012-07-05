package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// print(value, ...)             : prints string representation of provided value into stdout

public class PD_print extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_print();

    private PD_print() {
        this("print");
    }

    protected PD_print(String fName) {
        super(fName);
        addParameter("value");
        enableUnlimitedParameters();
        allowFewerParameters();
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        final StringBuilder out = new StringBuilder();
        for (final Value arg : args) {
            arg.appendUnquotedString(out, 0);
            print(out.toString());
            out.setLength( 0 );
        }
        printEndl();
        return Om.OM;
    }

    protected void print(String txt) {
        Environment.outWrite(txt);
    }

    protected void printEndl() {
        Environment.outWriteLn();
    }
}


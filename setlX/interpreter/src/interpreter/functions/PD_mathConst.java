package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.types.Real;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.List;

public class PD_mathConst extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_mathConst();

    private PD_mathConst() {
        super("mathConst");
        addParameter("name_of_mathematical_constant");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        Value name = args.get(0);
        if (name.equals(new SetlString("e"))) {
            return new Real(Math.E);
        } else if (name.equals(new SetlString("pi"))) {
            return new Real(Math.PI);
        } else {
            throw new IncompatibleTypeException("Name-argument '" + name + "' is not a known constant or not a string.");
        }
    }
}


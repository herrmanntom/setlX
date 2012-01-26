package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.types.Real;
import interpreter.types.SetlBoolean;
import interpreter.types.Value;

import java.util.List;

// mathConst(name)         : get the value of a mathematical constant (currently only pi and e)

public class PD_mathConst extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_mathConst();

    private PD_mathConst() {
        super("mathConst");
        addParameter("name_of_mathematical_constant");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        String name = args.get(0).getUnquotedString();
        if (name.equalsIgnoreCase("e")) {
            return new Real(Math.E);
        } else if (name.equalsIgnoreCase("pi")) {
            return new Real(Math.PI);
        } else {
            throw new IncompatibleTypeException("Name-argument '" + name + "' is not a known constant or not a string.");
        }
    }
}


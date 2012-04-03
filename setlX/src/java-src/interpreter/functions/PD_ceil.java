package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

/*
 * ceil(numberValue)       : returns minumum integer which is greater or equal to numberValue
 */

public class PD_ceil extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_ceil();

    private PD_ceil() {
        super("ceil");
        addParameter("numberValue");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).ceil();
    }
}


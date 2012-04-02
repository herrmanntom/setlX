package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

/*
 * floor(numberValue)      : returns maximum integer which is lower or equal to numberValue
 */

public class PD_floor extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION
                                            = new PD_floor();

    private PD_floor() {
        super("from");
        addParameter("numberValue");
    }

    public Value execute(List<Value> args,
                         List<Value> writeBackVars
    ) throws SetlException {
        return args.get(0).floor();
    }
}


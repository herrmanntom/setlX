package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.List;

// fct(term)               : get functional char of a term

public class PD_fct extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_fct();

    private PD_fct() {
        super("fct");
        addParameter("term");
    }

    public SetlString execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).functionalCharacter();
    }
}


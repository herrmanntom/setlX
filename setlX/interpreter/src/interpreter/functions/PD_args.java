package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlList;
import interpreter.types.Value;

import java.util.List;

public class PD_args extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_args();

    private PD_args() {
        super("args");
        addParameter("term");
    }

    public SetlList execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).arguments();
    }
}


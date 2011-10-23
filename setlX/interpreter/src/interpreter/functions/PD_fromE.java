package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlOm;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_fromE extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_fromE();

    private PD_fromE() {
        super("fromE");
        addParameter(new ParameterDef("compoundValue", ParameterDef.READ_WRITE));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value collection = args.get(0);
        Value element    = collection.lastMember();
        if (element != SetlOm.OM) {
            collection.removeLastMember();
        }

        // write the reduced collection back into the outer environment
        writeBackVars.add(collection);

        return element;
    }
}


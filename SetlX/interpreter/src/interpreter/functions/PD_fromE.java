package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlDefinitionParameter;
import interpreter.types.SetlOm;
import interpreter.types.Value;

import java.util.List;

public class PD_fromE extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_fromE();

    private PD_fromE() {
        super("fromE");
        addParameter(new SetlDefinitionParameter("compoundValue", SetlDefinitionParameter.READ_WRITE));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value collection = args.get(0);
        Value element    = collection.lastMember();
        if (!(element instanceof SetlOm)) {
            collection.removeLastMember();
        }

        // write the reduced collection back into the outer environment
        writeBackVars.add(collection);

        return element;
    }
}

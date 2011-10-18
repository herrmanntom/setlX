package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlDefinitionParameter;
import interpreter.types.SetlOm;
import interpreter.types.Value;

import java.util.List;

public class PD_fromB extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_fromB();

    private PD_fromB() {
        super("fromB");
        addParameter(new SetlDefinitionParameter("compoundValue", SetlDefinitionParameter.READ_WRITE));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value collection = args.get(0);
        Value element    = collection.firstMember();
        if (!(element instanceof SetlOm)) {
            collection.removeFirstMember();
        }

        // write the reduced collection back into the outer environment
        writeBackVars.add(collection);

        return element;
    }
}

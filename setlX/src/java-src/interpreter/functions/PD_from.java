package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

/*
 * from(collectionValue)   : select and removes an arbitrary
 *                           member from collection value
 */

public class PD_from extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION
                                            = new PD_from();

    private PD_from() {
        super("from");
        addParameter("collectionValue", ParameterDef.READ_WRITE);
    }

    public Value execute(List<Value> args,
                         List<Value> writeBackVars
    ) throws SetlException {
        Value collection = args.get(0);
        Value element    = collection.arbitraryMember();
        if (element != Om.OM) {
            collection.removeMember(element);
        }

        /* write the reduced collection back
           into the outer environment        */
        writeBackVars.add(collection);

        return element;
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;

import java.util.List;

/*
 * from(rw collectionValue)      : selects and removes an arbitrary
 *                                 member from `collectionValue'
 */

public class PD_from extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION
                                            = new PD_from();

    private PD_from() {
        super("from");
        addParameter("collectionValue", ParameterDef.READ_WRITE);
    }

    public Value execute(final List<Value> args,
                         final List<Value> writeBackVars
    ) throws SetlException {
        final Value collection = args.get(0);
        // throws exception when `collection' is not a collection
        final Value element    = collection.arbitraryMember();
        if (element != Om.OM) {
            collection.removeMember(element);
        }

        /* write the reduced collection back
           into the outer environment        */
        writeBackVars.add(collection);

        return element;
    }
}


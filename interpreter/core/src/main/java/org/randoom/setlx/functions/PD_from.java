package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * from(rw collectionValue)      : selects and removes an arbitrary
 *                                 member from `collectionValue'
 */
public class PD_from extends PreDefinedProcedure {
    private final static ParameterDefinition COLLECTION_VALUE
                           = createRwParameter("collectionValue");

    /** Definition of the PreDefinedProcedure `from'. */
    public  final static PreDefinedProcedure DEFINITION
                           = new PD_from();

    private PD_from() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(
            final State                        state,
            final HashMap<ParameterDefinition, Value> args
    ) throws SetlException
    {
        final Value collection = args.get(COLLECTION_VALUE);
        final Value element;
        try {
            /* throws exception when `collection' is
             * not a collection                      */
            final int size = collection.size();
            if (size % 2 == 0) {
                element = collection.removeFirstMember(state);
            } else {
                element = collection.removeLastMember(state);
            }
        } catch (final IncompatibleTypeException ite) {
            throw new IncompatibleTypeException(
                "Argument '" + collection +
                "' is not a collection value."
            );
        }

        /* write the reduced collection back into
         * the outer environment                     */
        args.put(COLLECTION_VALUE, collection);

        return element;
    }
}


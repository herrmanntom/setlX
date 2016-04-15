package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlMatrix;
import org.randoom.setlx.types.SetlVector;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * @author Patrick Robinson
 *         <p/>
 *         Creates a new Vector
 */
public class PD_la_vector extends PreDefinedProcedure {

    private final static ParameterDefinition COLLECTION_VALUE = createParameter("collectionValue");

    public  final static PreDefinedProcedure DEFINITION       = new PD_la_vector();

    private PD_la_vector() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        if (args.get(COLLECTION_VALUE) instanceof SetlMatrix) {
            return ((SetlMatrix) args.get(COLLECTION_VALUE)).toVector();
        } else if ((args.get(COLLECTION_VALUE) instanceof CollectionValue)) {
            return new SetlVector(state, (CollectionValue) args.get(COLLECTION_VALUE));
        } else {
            throw new IncompatibleTypeException("Vectors can only be created from collections or matrices.");
        }
    }
}

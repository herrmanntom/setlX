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
 *         Construct a new SetlMatrix
 */
public class PD_la_matrix extends PreDefinedProcedure {

    private final static ParameterDefinition COLLECTION_VALUE = createParameter("collectionValue");

    public  final static PreDefinedProcedure DEFINITION       = new PD_la_matrix();

    private PD_la_matrix() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        if (args.get(COLLECTION_VALUE) instanceof SetlVector) {
            return new SetlMatrix(state, (SetlVector) args.get(COLLECTION_VALUE));
        } else if (args.get(COLLECTION_VALUE) instanceof CollectionValue) {
            return new SetlMatrix(state, (CollectionValue) args.get(COLLECTION_VALUE));
        } else {
            // System.err.println("[DEBUG]: matrix param notcollection");
            throw new IncompatibleTypeException("Matrices can only be created from collections or vectors.");
        }

    }
}

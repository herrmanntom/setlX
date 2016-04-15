package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlMatrix;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * @author Patrick Robinson
 */
public class PD_la_svd extends PreDefinedProcedure {
    private final static ParameterDefinition MATRIX     = createParameter("matrix");

    public  final static PreDefinedProcedure DEFINITION = new PD_la_svd();

    private PD_la_svd() {
        super();
        addParameter(MATRIX);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        if (!(args.get(MATRIX) instanceof SetlMatrix)) {
            throw new IncompatibleTypeException("The parameter needs to be a matrix.");
        }
        return ((SetlMatrix) args.get(MATRIX)).singularValueDecomposition(state);
    }
}

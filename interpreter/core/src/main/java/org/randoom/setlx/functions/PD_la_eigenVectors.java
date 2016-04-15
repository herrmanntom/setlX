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
 *         <p/>
 *         Calculate eigen vector matrix
 */
public class PD_la_eigenVectors extends PreDefinedProcedure {

    private final static ParameterDefinition MATRIX     = createParameter("matrix");

    public  final static PreDefinedProcedure DEFINITION = new PD_la_eigenVectors();

    private PD_la_eigenVectors() {
        super();
        addParameter(MATRIX);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        if ((args.get(MATRIX) instanceof SetlMatrix)) {
            return ((SetlMatrix) args.get(MATRIX)).eigenVectors(state);
        } else {
            throw new IncompatibleTypeException("The parameter needs to be a matrix.");
        }
    }
}

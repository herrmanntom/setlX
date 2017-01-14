package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlMatrix;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * hadamard(a, b) : Returns the Hadamard product of two matrices.
 */
public class PD_la_hadamard extends PreDefinedProcedure {

    private final static ParameterDefinition MATRIX_A     = createParameter("matrixA");
    private final static ParameterDefinition MATRIX_B     = createParameter("matrixB");

    public  final static PreDefinedProcedure DEFINITION = new PD_la_hadamard();

    private PD_la_hadamard() {
        super();
        addParameter(MATRIX_A);
        addParameter(MATRIX_B);
    }

    @Override
    public SetlMatrix execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        if ((args.get(MATRIX_A) instanceof SetlMatrix)) {
            return ((SetlMatrix) args.get(MATRIX_A)).hadamardProduct(state, args.get(MATRIX_B));
        } else {
            throw new IncompatibleTypeException("The first parameter needs to be a matrix.");
        }
    }
}

package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlMatrix;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * la_pseudoInverse(matrix) : Calculate pseudo-inverse of the matrix.
 */
public class PD_la_pseudoInverse extends PreDefinedProcedure {

    private final static ParameterDefinition MATRIX     = createParameter("matrix");

    /** Definition of the PreDefinedProcedure `la_pseudoInverse'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_la_pseudoInverse();

    private PD_la_pseudoInverse() {
        super();
        addParameter(MATRIX);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        if (!(args.get(MATRIX) instanceof SetlMatrix)) {
            throw new IncompatibleTypeException("The Parameter needs to be a Matrix.");
        }
        return ((SetlMatrix) args.get(MATRIX)).pseudoInverse();
    }
}

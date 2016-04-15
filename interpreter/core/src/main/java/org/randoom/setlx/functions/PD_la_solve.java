package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlMatrix;
import org.randoom.setlx.types.SetlVector;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * @author Patrick Robinson
 *         X := solve(A,B)
 *         solves A*X = B
 */
public class PD_la_solve extends PreDefinedProcedure {

    private final static ParameterDefinition MATRIX_A   = createParameter("matrixA");
    private final static ParameterDefinition MATRIX_B   = createParameter("matrixB");

    public  final static PreDefinedProcedure DEFINITION = new PD_la_solve();

    private PD_la_solve() {
        super();
        addParameter(MATRIX_A);
        addParameter(MATRIX_B);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        SetlMatrix a;
        SetlMatrix b;
        if (args.get(MATRIX_A) instanceof SetlMatrix) {
            a = (SetlMatrix) args.get(MATRIX_A);
        } else {
            throw new IncompatibleTypeException("The first parameter needs to be a matrix.");
        }
        if (args.get(MATRIX_B) instanceof SetlMatrix) {
            b = (SetlMatrix) args.get(MATRIX_B);
        } else if (args.get(MATRIX_B) instanceof SetlVector) {
            b = new SetlMatrix(state, (SetlVector) args.get(MATRIX_B));
        } else {
            throw new IncompatibleTypeException("The second parameter needs to be a matrix.");
        }
        return a.solve(b);
    }
}

package org.randoom.setlx.functions;

import java.util.List;
import org.randoom.setlx.exceptions.MatrixException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Matrix;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 * X := solve(A,B)
 * solves A*X = B
 */
public class PD_matrixsolve extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_matrixsolve();
    
    private PD_matrixsolve() {
        super();
        addParameter("MatrixA", ParameterDef.READ_ONLY);
        addParameter("MatrixB", ParameterDef.READ_ONLY);
    }

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        if(!(args.get(0) instanceof Matrix)) throw new MatrixException("The first parameter is not a matrix.");
        if(!(args.get(1) instanceof Matrix)) throw new MatrixException("The second parameter is not a matrix.");
        // TODO check conditions
        Matrix A = (Matrix)args.get(0);
        Matrix B = (Matrix)args.get(1);
        return new Matrix(A.value.solve(B.value));
    }
}

package org.randoom.setlx.functions;

import Jama.EigenvalueDecomposition;
import java.util.List;
import org.randoom.setlx.exceptions.MatrixException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Matrix;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 */
public class PD_eigenVectors extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_eigenVectors();
    
    private PD_eigenVectors() {
        super();
        addParameter("Matrix", ParameterDef.READ_ONLY);
    }    

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        if(!(args.get(0) instanceof Matrix)) throw new MatrixException("The parameter needs to be a matrix.");
        return ((Matrix)args.get(0)).eigenVectors();
    }
}

package org.randoom.setlx.functions;

import Jama.EigenvalueDecomposition;
import java.util.List;
import org.randoom.setlx.exceptions.MatrixException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Matrix;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 */
public class PD_eigenValues extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_eigenValues();
    
    private PD_eigenValues() {
        super();
        addParameter("Matrix", ParameterDef.READ_ONLY);
    }    

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        if(!(args.get(0) instanceof Matrix)) throw new MatrixException("The parameter needs to be a matrix.");
        // TODO check condition
        EigenvalueDecomposition result = ((Matrix)args.get(0)).value.eig();
        // return new Matrix(result.getD()); // TODO right result?
        double[][] values = result.getD().getArray();
        SetlList composition = new SetlList(values.length);
        for(int i = 0; i < values.length; i++) {
            composition.addMember(state, SetlDouble.valueOf(values[i][i]));
        }
        return composition;
    }
}

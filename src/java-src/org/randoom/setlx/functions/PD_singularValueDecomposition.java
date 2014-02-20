package org.randoom.setlx.functions;

import Jama.SingularValueDecomposition;
import java.util.List;
import org.randoom.setlx.exceptions.MatrixException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Matrix;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 */
public class PD_singularValueDecomposition extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_singularValueDecomposition();
    
    private PD_singularValueDecomposition() {
        super();
        addParameter("Matrix", ParameterDef.READ_ONLY);
    }    

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        if(!(args.get(0) instanceof Matrix)) throw new MatrixException("The Parameter needs to be a Matrix.");
        // TODO check condition
        SingularValueDecomposition result = ((Matrix)args.get(0)).value.svd();
        SetlList container = new SetlList(3);
        container.addMember(state, new Matrix(result.getU())); // TODO right format?
        container.addMember(state, new Matrix(result.getS())); // TODO Is this Σ? format?
        container.addMember(state, new Matrix(result.getV())); // TODO right format?
        return container;
    }
}

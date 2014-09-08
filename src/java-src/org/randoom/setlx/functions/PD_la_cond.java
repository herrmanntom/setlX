package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlMatrix;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * la_cond(matrix) : Calculate the condition of the matrix, i.e. ratio of largest to smallest singular value.
 */
public class PD_la_cond extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `la_cond'. */
    public final static PreDefinedProcedure DEFINITION = new PD_la_cond();

    private PD_la_cond() {
        super();
        addParameter("Matrix", ParameterDef.ParameterType.READ_ONLY);
    }

    @Override
    public Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        if (!(args.get(0) instanceof SetlMatrix)) {
            throw new IncompatibleTypeException("The Parameter needs to be a Matrix.");
        }
        return ((SetlMatrix) args.get(0)).condition();
    }
}

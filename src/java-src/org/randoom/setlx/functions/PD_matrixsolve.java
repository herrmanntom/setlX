package org.randoom.setlx.functions;

import java.util.List;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlMatrix;
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

	/**
	 * Solve A * X = B
	 *
	 * @param state
	 * @param args [SetlMatrix A, SetlMatrix B]
	 * @param writeBackVars
	 * @return X
	 * @throws SetlException
	 */
	@Override
	public Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
		if(!(args.get(0) instanceof SetlMatrix)) {
			throw new IncompatibleTypeException("The first parameter needs to be a matrix.");
		}
		if(!(args.get(1) instanceof SetlMatrix)) {
			throw new IncompatibleTypeException("The second parameter needs to be a matrix.");
		}
		SetlMatrix A = (SetlMatrix)args.get(0);
		SetlMatrix B = (SetlMatrix)args.get(1);
		return A.solve(B);
	}
}

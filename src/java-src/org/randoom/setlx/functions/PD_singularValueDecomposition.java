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
 */
public class PD_singularValueDecomposition extends PreDefinedProcedure {

	public final static PreDefinedProcedure DEFINITION = new PD_singularValueDecomposition();

	private PD_singularValueDecomposition() {
		super();
		addParameter("Matrix", ParameterDef.ParameterType.READ_ONLY);
	}

	/**
	 *
	 *
	 * @param state
	 * @param args SetlMatrix
	 * @param writeBackVars
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
		if(!(args.get(0) instanceof SetlMatrix)) {
			throw new IncompatibleTypeException("The Parameter needs to be a Matrix.");
		}
		return ((SetlMatrix)args.get(0)).singularValueDecomposition(state);
	}
}

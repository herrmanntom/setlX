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
public class PD_eigenValues extends PreDefinedProcedure {

	public final static PreDefinedProcedure DEFINITION = new PD_eigenValues();

	private PD_eigenValues() {
		super();
		addParameter("Matrix", ParameterDef.ParameterType.READ_ONLY);
	}

	/**
	 * Calculate eigen values
	 *
	 * @param state
	 * @param args SetlMatrix
	 * @param writeBackVars
	 * @return list of numbers
	 * @throws SetlException
	 */
	@Override
	public Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
		if((args.get(0) instanceof SetlMatrix)) {
			return ((SetlMatrix)args.get(0)).eigenValues(state);
		} else {
			throw new IncompatibleTypeException("The parameter needs to be a matrix.");
		}
	}
}

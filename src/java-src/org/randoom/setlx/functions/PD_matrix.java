/**
 * 
 */
package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Matrix;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * @author autarch
 *
 */
public class PD_matrix extends PreDefinedProcedure {

	/* (non-Javadoc)
	 * @see org.randoom.setlx.functions.PreDefinedProcedure#execute(org.randoom.setlx.utilities.State, java.util.List, java.util.List)
	 */
	@Override
	protected Value execute(State state, List<Value> args,
			List<Value> writeBackVars) throws SetlException {
		
		return new Matrix((CollectionValue)args.get(0));
	}

}

/*
 */
package org.randoom.setlx.functions;

import java.util.List;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlMatrix;
import org.randoom.setlx.types.SetlVector;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 */
public class PD_vector extends PreDefinedProcedure {

	public final static PreDefinedProcedure DEFINITION = new PD_vector();

	private PD_vector() {
		super();
		addParameter("collectionValue", ParameterDef.READ_ONLY);
	}

	/**
	 * Creates a new Vector
	 *
	 * @param state
	 * @param args matrix or collection
	 * @param writeBackVars
	 * @return SetlVector
	 * @throws SetlException
	 */
	@Override
	public Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
		if(args.get(0) instanceof SetlMatrix) {
			return new SetlVector(state, (SetlMatrix)args.get(0));
		} else if((args.get(0) instanceof CollectionValue)) {
			return new SetlVector(state, (CollectionValue)args.get(0));
		} else {
			// System.err.println("[DEBUG]: vector param notcollection");
			throw new IncompatibleTypeException("Vectors can only be created from collections or matrices.");
		}
	}
}

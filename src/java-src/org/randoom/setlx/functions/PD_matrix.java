/**
 * 
 */
package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlMatrix;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 *
 */
public class PD_matrix extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_matrix();
    
    private PD_matrix() {
        super();
        addParameter("collectionValue", ParameterDef.READ_ONLY);
    }
    
    /* (non-Javadoc)
     * @see org.randoom.setlx.functions.PreDefinedProcedure#execute(org.randoom.setlx.utilities.State, java.util.List, java.util.List)
     */
    @Override
    public Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        if(!(args.get(0) instanceof CollectionValue)) System.err.println("[DEBUG]: matrix param notcollection");
        return new SetlMatrix(state, (CollectionValue)args.get(0));
    }
}

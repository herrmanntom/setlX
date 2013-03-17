package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.CachedProcedure;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// clearCache(cachedProcedure)   : clears the cache of supplied function

public class PD_clearCache extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_clearCache();

    private PD_clearCache() {
        super();
        addParameter("cachedProcedure");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value function  = args.get(0);
        if ( ! (function instanceof CachedProcedure)) {
            throw new IncompatibleTypeException(
                "Argument '" + function + "' is not a cached function."
            );
        }

        ((CachedProcedure) function).clearCache();

        return Om.OM;
    }

}


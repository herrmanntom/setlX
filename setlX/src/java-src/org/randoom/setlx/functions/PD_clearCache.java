package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.CachedProcedureDefinition;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;

import java.util.List;

// clearCache(cachedProcedure)   : clears the cache of supplied function

public class PD_clearCache extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_clearCache();

    private PD_clearCache() {
        super("clearCache");
        addParameter("cachedProcedure");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value function  = args.get(0);
        if ( ! (function instanceof CachedProcedureDefinition)) {
            throw new IncompatibleTypeException(
                "Argument '" + function + "' is not a cached function."
            );
        }

        ((CachedProcedureDefinition) function).clearCache();

        return Om.OM;
    }

}


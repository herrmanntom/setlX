package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.CachedProcedure;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * clearCache(cachedProcedure) : Clears the cache of supplied procedure.
 */
public class PD_clearCache extends PreDefinedProcedure {

    private final static ParameterDefinition CACHED_PROCEDURE = createParameter("cachedProcedure");

    /** Definition of the PreDefinedProcedure `clearCache'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_clearCache();

    private PD_clearCache() {
        super();
        addParameter(CACHED_PROCEDURE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
        final Value cachedProcedure = args.get(CACHED_PROCEDURE);
        if ( ! (cachedProcedure instanceof CachedProcedure)) {
            throw new IncompatibleTypeException(
                "Argument '" + cachedProcedure + "' is not a cached procedure."
            );
        }

        ((CachedProcedure) cachedProcedure).clearCache();

        return Om.OM;
    }

}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.CachedProcedure;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * cacheStats(cachedProcedure) : Return a map containing statistics of the given cachedProcedure.
 */
public class PD_cacheStats extends PreDefinedProcedure {

    private final static ParameterDefinition CACHED_PROCEDURE = createParameter("cachedProcedure");

    /** Definition of the PreDefinedProcedure `cacheStats'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_cacheStats();

    private PD_cacheStats() {
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

        final CachedProcedure f      = (CachedProcedure) cachedProcedure;

        final SetlSet         result = new SetlSet();

        final SetlList        hits   = new SetlList(2);
        hits.addMember(state, new SetlString("cache hits"));
        hits.addMember(state, Rational.valueOf(f.getCacheHits()));
        result.addMember(state, hits);

        final SetlList        size   = new SetlList(2);
        size.addMember(state, new SetlString("cached items"));
        size.addMember(state, Rational.valueOf(f.getCacheSize()));
        result.addMember(state, size);

        return result;
    }

}


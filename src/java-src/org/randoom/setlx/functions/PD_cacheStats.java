package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.CachedProcedure;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// cacheStats(cachedProcedure)   : return a map containing statistics of the given cachedFunction

public class PD_cacheStats extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_cacheStats();

    private PD_cacheStats() {
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

        final CachedProcedure f = (CachedProcedure) function;

        final SetlSet  result = new SetlSet();

        final SetlList hits   = new SetlList(2);
        hits.addMember(state, new SetlString("cache hits"));
        hits.addMember(state, Rational.valueOf(f.getCacheHits()));
        result.addMember(state, hits);

        final SetlList size   = new SetlList(2);
        size.addMember(state, new SetlString("cached items"));
        size.addMember(state, Rational.valueOf(f.getCacheSize()));
        result.addMember(state, size);

        return result;
    }

}


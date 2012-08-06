package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.CachedProcedureDefinition;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.List;

// cacheStats(cachedProcedure)   : return a map containing statistics of the given cachedFunction

public class PD_cacheStats extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_cacheStats();

    private PD_cacheStats() {
        super("cacheStats");
        addParameter("cachedProcedure");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value function  = args.get(0);
        if ( ! (function instanceof CachedProcedureDefinition)) {
            throw new IncompatibleTypeException(
                "Argument '" + function + "' is not a cached function."
            );
        }

        final CachedProcedureDefinition f = (CachedProcedureDefinition) function;

        final SetlSet  result = new SetlSet();

        final SetlList hits   = new SetlList(2);
        hits.addMember(new SetlString("cache hits"));
        hits.addMember(Rational.valueOf(f.getCacheHits()));
        result.addMember(hits);

        final SetlList size   = new SetlList(2);
        size.addMember(new SetlString("cached items"));
        size.addMember(Rational.valueOf(f.getCacheSize()));
        result.addMember(size);

        return result;
    }

}


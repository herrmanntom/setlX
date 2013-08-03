package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents an automatically caching function definition.
 *
 * The cache is implemented as a hash map of
 * call-parameters -> value
 * and the values are linked via SoftReferences, which may get collected
 * to prevent the JVM to run out of memory.
 *
 * grammar rule:
 * procedure
 *     : 'cachedProcedure' '(' procedureParameters ')' '{' block '}'
 *     ;
 *
 * implemented here as:
 *                             ===================         =====
 *                                 parameters            statements
 *
 */
public class CachedProcedure extends Procedure {
    // functional character used in terms
    public  final static String                                  FUNCTIONAL_CHARACTER = generateFunctionalCharacter(CachedProcedure.class);

    private final        HashMap<SetlList, SoftReference<Value>> cache;
    private              int                                     cacheHits;

    /**
     * Create new cached procedure definition.
     * @param parameters procedure parameters
     * @param statements statements in the body of the procedure
     */
    public CachedProcedure(final List<ParameterDef> parameters, final Block statements) {
        super(parameters, statements);
        cache     = new HashMap<SetlList, SoftReference<Value>>();
        cacheHits = 0;
    }

    /**
     * Create new cached procedure definition, which replicates the complete
     * internal state of another cached procedure.
     * @param parameters procedure parameters
     * @param statements statements in the body of the procedure
     * @param closure
     * @param cache
     * @param cacheHits
     */
    private CachedProcedure(
        final List<ParameterDef>                      parameters,
        final Block                                   statements,
        final HashMap<String, Value>                  closure,
        final HashMap<SetlList, SoftReference<Value>> cache,
        final int                                     cacheHits
    ) {
        super(parameters, statements, closure);
        this.cache     = cache;
        this.cacheHits = cacheHits;
    }

    @Override
    public CachedProcedure createCopy() {
        return new CachedProcedure(parameters, statements);
    }

    @Override
    public CachedProcedure clone() {
        if (closure != null || object != null) {
            return new CachedProcedure(parameters, statements, closure, cache, cacheHits);
        } else {
            return this;
        }
    }

    /**
     * Remove references from cache, which where garbage collected.
     */
    private void validateCache() {
        final ArrayList<SetlList> rmKeys = new ArrayList<SetlList>();
        for (final SetlList key : cache.keySet()) {
            final SoftReference<Value> valueRef = cache.get(key);
            if (valueRef == null || valueRef.get() == null) {
                rmKeys.add(key);
            }
        }
        for (final SetlList key: rmKeys) {
            cache.remove(key);
        }
    }

    public int getCacheHits() {
        object = null;
        return cacheHits;
    }
    public int getCacheSize() {
        object = null;
        validateCache();
        return cache.size();
    }
    public void clearCache() {
        object = null;
        cache.clear();
        cacheHits = 0;
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        final int size = args.size();
        final SetlObject object = this.object;
        this.object = null;

        if (parameters.size() != size) {
            throw new IncorrectNumberOfParametersException(
                "'" + this + "' is defined with "+ parameters.size()+" instead of " +
                size + " parameters."
            );
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<Value>(size);
        final SetlList         key    = new SetlList(size);
        for (int i = 0; i < size; ++i) {
            if (parameters.get(i).getType() == ParameterDef.READ_WRITE) {
                throw new IncompatibleTypeException(
                    "Procedures using read-write ('rw') parameters can not be cached."
                );
            } else {
                final Value v = args.get(i).eval(state);
                values.add(v);
                key.addMember(state, v);
            }
        }

        Value                      cachedResult = null;
        final SoftReference<Value> result       = cache.get(key);
        if (result != null) {
            cachedResult = result.get();
        }

        if (cachedResult != null) {
            ++cacheHits;
            return cachedResult.clone();
        } else {
            // cache om to prevent recursion loop
            cache.put(key, new SoftReference<Value>(Om.OM));
            // call function
            cachedResult = callAfterEval(state, args, values, object);
            // put value into cache
            cache.put(key, new SoftReference<Value>(cachedResult));
            // return value
            return cachedResult.clone();
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        object = null;
        sb.append("cachedProcedure(");
        final Iterator<ParameterDef> iter = parameters.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") ");
        statements.appendString(state, sb, tabs, /* brackets = */ true);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        object = null;
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList paramList = new SetlList(parameters.size());
        for (final ParameterDef param: parameters) {
            paramList.addMember(state, param.toTerm(state));
        }
        result.addMember(state, paramList);

        result.addMember(state, statements.toTerm(state));

        return result;
    }

    public static CachedProcedure termToValue(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList            paramList   = (SetlList) term.firstMember();
            final List<ParameterDef>  parameters  = new ArrayList<ParameterDef>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(v));
            }
            final Block               block       = TermConverter.valueToBlock(term.lastMember());
            return new CachedProcedure(parameters, block);
        }
    }

    private final static int initHashCode = CachedProcedure.class.hashCode();

    @Override
    public int hashCode() {
        object = null;
        return initHashCode + parameters.size();
    }
}


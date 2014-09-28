package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.ParameterDef.ParameterType;
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
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(CachedProcedure.class);

    private final HashMap<SetlList, SoftReference<Value>> cache;
    private       int                                     cacheHits;

    /**
     * Create new cached procedure definition.
     *
     * @param parameters Procedure parameters.
     * @param statements Statements in the body of the procedure.
     */
    public CachedProcedure(final List<ParameterDef> parameters, final Block statements) {
        super(parameters, statements);
        cache     = new HashMap<SetlList, SoftReference<Value>>();
        cacheHits = 0;
    }

    /**
     * Create new cached procedure definition, which replicates the complete
     * internal state of another cached procedure.
     *
     * @param parameters Procedure parameters.
     * @param statements Statements in the body of the procedure.
     * @param cache      Cache of result values.
     * @param cacheHits  Number of cache hits so far.
     */
    private CachedProcedure(
        final List<ParameterDef>                      parameters,
        final Block                                   statements,
        final HashMap<SetlList, SoftReference<Value>> cache,
        final int                                     cacheHits
    ) {
        super(parameters, statements);
        this.cache     = cache;
        this.cacheHits = cacheHits;
    }

    @Override
    public CachedProcedure clone() {
        if (object != null) {
            return new CachedProcedure(parameters, statements, cache, cacheHits);
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

    /**
     * Returns the number times a cached result could be used instead of
     * recomputing it.
     *
     * @return Number of cache hits.
     */
    public int getCacheHits() {
        object = null;
        return cacheHits;
    }
    /**
     * Returns the number of cached results for this function.
     *
     * @return Number of cached results.
     */
    public int getCacheSize() {
        object = null;
        validateCache();
        return cache.size();
    }
    /**
     * Clears the internal result cache of this function.
     */
    public void clearCache() {
        object = null;
        cache.clear();
        cacheHits = 0;
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        final int        nArguments = args.size();
        final SetlObject object     = this.object;
        this.object = null;

        if (isLastParameterList) {
            if (nArguments < parameters.size() - 1) {
                final StringBuilder error = new StringBuilder();
                error.append("'");
                appendStringWithoutStatements(state, error);
                error.append("' is defined with at least ");
                error.append(parameters.size() - 1);
                error.append(" instead of ");
                error.append(nArguments);
                error.append(" parameters.");
                throw new IncorrectNumberOfParametersException(error.toString());
            }
        } else if (nArguments != parameters.size()) {
            final StringBuilder error = new StringBuilder();
            error.append("'");
            appendStringWithoutStatements(state, error);
            error.append("' is defined with ");
            error.append(parameters.size());
            error.append(" instead of ");
            error.append(nArguments);
            error.append(" parameters.");
            throw new IncorrectNumberOfParametersException(error.toString());
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<Value>(nArguments);
        final SetlList         key    = new SetlList(nArguments);
        for (Expr arg : args) {
            final Value v = arg.eval(state);
            values.add(v);
            key.addMember(state, v);
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
            try {
                // increase callStackDepth
                ++(state.callStackDepth);

                // cache om to prevent recursion loop
                cache.put(key, new SoftReference<Value>(Om.OM));
                // call function
                cachedResult = callAfterEval(state, args, values, object);
                // put value into cache
                cache.put(key, new SoftReference<Value>(cachedResult));
                // return value
                return cachedResult.clone();

            } catch (final StackOverflowError soe) {
                state.storeStackDepthOfFirstCall(state.callStackDepth);
                throw soe;
            } finally {
                // decrease callStackDepth
                --(state.callStackDepth);
            }
        }
    }

    /* string and char operations */

    @Override
    protected void appendStringWithoutStatements(final State state, final StringBuilder sb) {
        object = null;
        sb.append("cachedProcedure(");
        final Iterator<ParameterDef> iter = parameters.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
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

    /**
     * Convert a term representing a CachedProcedure into such a procedure.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting Procedure.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static CachedProcedure termToValue(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || term.firstMember().getClass() != SetlList.class) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList           paramList  = (SetlList) term.firstMember();
            final List<ParameterDef> parameters = new ArrayList<ParameterDef>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(state, v));
            }
            final Block              block      = TermConverter.valueToBlock(state, term.lastMember());
            return new CachedProcedure(parameters, block);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final Value other) {
        object = null;
        if (this == other) {
            return 0;
        } else if (other.getClass() == CachedProcedure.class) {
            final CachedProcedure cachedProcedure = (CachedProcedure) other;
            int cmp = Integer.valueOf(parameters.size()).compareTo(cachedProcedure.parameters.size());
            if (cmp != 0) {
                return cmp;
            }
            for (int index = 0; index < parameters.size(); ++index) {
                cmp = parameters.get(index).compareTo(cachedProcedure.parameters.get(index));
                if (cmp != 0) {
                    return cmp;
                }
            }
            return statements.compareTo(cachedProcedure.statements);
        } else {
            return this.compareToOrdering() - other.compareToOrdering();
        }
    }

    @Override
    public int compareToOrdering() {
        object = null;
        return COMPARE_TO_ORDERING_PROCEDURE_CACHED;
    }

    @Override
    public boolean equalTo(final Object other) {
        object = null;
        if (this == other) {
            return true;
        } else if (other.getClass() == CachedProcedure.class) {
            final CachedProcedure cachedProcedure = (CachedProcedure) other;
            if (parameters.size() == cachedProcedure.parameters.size()) {
                for (int index = 0; index < parameters.size(); ++index) {
                    if ( ! parameters.get(index).equalTo(cachedProcedure.parameters.get(index))) {
                        return false;
                    }
                }
                return statements.equalTo(cachedProcedure.statements);
            }
        }
        return false;
    }

    private final static int initHashCode = CachedProcedure.class.hashCode();

    @Override
    public int hashCode() {
        object = null;
        return (initHashCode + parameters.size()) * 31 + statements.size();
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


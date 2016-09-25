package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.parameters.ParameterList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
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
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(CachedProcedure.class);

    private final HashMap<SetlList, SoftReference<Value>> cache;
    private       int                                     cacheHits;

    /**
     * Create new cached procedure definition.
     *
     * @param parameters Procedure parameters.
     * @param statements Statements in the body of the procedure.
     */
    public CachedProcedure(final ParameterList parameters, final Block statements) {
        super(parameters, statements);
        cache     = new HashMap<>();
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
        final ParameterList                           parameters,
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
        final ArrayList<SetlList> rmKeys = new ArrayList<>();
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
    public Value call(final State state, List<Value> argumentValues, final FragmentList<OperatorExpression> arguments, final Value listValue, final OperatorExpression listArg) throws SetlException {
        final SetlObject object = this.object;
        this.object = null;

        SetlList listArguments = null;
        if (listValue != null) {
            if (listValue.getClass() != SetlList.class) {
                StringBuilder error = new StringBuilder();
                error.append("List argument '");
                listValue.appendString(state, error, 0);
                error.append("' is not a list.");
                throw new UndefinedOperationException(error.toString());
            }
            listArguments = (SetlList) listValue;
        }

        int nArguments = argumentValues.size();
        if (listArguments != null) {
            nArguments += listArguments.size();
        }

        if (!parameters.isAssignableWithThisManyActualArguments(nArguments)) {
            final StringBuilder error = new StringBuilder();
            error.append("'");
            appendStringWithoutStatements(state, error);
            error.append("'");
            parameters.appendIncorrectNumberOfParametersErrorMessage(error, nArguments);
            throw new IncorrectNumberOfParametersException(error.toString());
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<>(nArguments);
        final SetlList key = new SetlList(nArguments);
        for (Value value : argumentValues) {
            values.add(value);
            key.addMember(state, value);
        }
        if (listArguments != null) {
            for (Value listArgument : listArguments) {
                values.add(listArgument);
                key.addMember(state, listArgument);
            }
        }

        Value cachedResult = null;
        final SoftReference<Value> result = cache.get(key);
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
            cachedResult = callAfterEval(state, arguments, values, object);
            // put value into cache
            cache.put(key, new SoftReference<>(cachedResult));
            // return value
            return cachedResult.clone();
        }
    }

    /* string and char operations */

    @Override
    protected void appendStringWithoutStatements(final State state, final StringBuilder sb) {
        object = null;
        sb.append("cachedProcedure(");
        parameters.appendString(state, sb);
        sb.append(")");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        object = null;
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, parameters.toTerm(state));

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
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final ParameterList parameters = ParameterList.termFragmentToParameterList(state, term.firstMember());
            final Block              block      = TermUtilities.valueToBlock(state, term.lastMember());
            return new CachedProcedure(parameters, block);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        object = null;
        if (this == other) {
            return 0;
        } else if (other.getClass() == CachedProcedure.class) {
            final CachedProcedure cachedProcedure = (CachedProcedure) other;
            int cmp = parameters.compareTo(cachedProcedure.parameters);
            if (cmp != 0) {
                return cmp;
            }
            return statements.compareTo(cachedProcedure.statements);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(CachedProcedure.class);

    @Override
    public long compareToOrdering() {
        object = null;
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        object = null;
        if (this == other) {
            return true;
        } else if (other.getClass() == CachedProcedure.class) {
            final CachedProcedure cachedProcedure = (CachedProcedure) other;
            if (parameters.equals(cachedProcedure.parameters)) {
                return statements.equalTo(cachedProcedure.statements);
            }
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return (((int) COMPARE_TO_ORDER_CONSTANT) + parameters.hashCode()) * 31 + statements.size();
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


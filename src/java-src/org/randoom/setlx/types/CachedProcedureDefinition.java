package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

// This class represents an automatically caching function definition

/*
grammar rule:
procedureDefinition
    : 'cachedProcedure' '(' procedureParameters ')' '{' block '}'
    ;

implemented here as:
                            ===================         =====
                                mParameters          mStatements
*/

public class CachedProcedureDefinition extends ProcedureDefinition {
    // functional character used in terms
    public  final static String                                  FUNCTIONAL_CHARACTER = "^cachedProcedure";

    private final        HashMap<SetlList, SoftReference<Value>> mCache;
    private              int                                     mCacheHits;

    public CachedProcedureDefinition(final List<ParameterDef> parameters, final Block statements) {
        super(parameters, statements);
        mCache     = new HashMap<SetlList, SoftReference<Value>>();
        mCacheHits = 0;
    }
    private CachedProcedureDefinition(
        final List<ParameterDef>                      parameters,
        final Block                                   statements,
        final HashMap<Variable, Value>                closure,
        final HashMap<SetlList, SoftReference<Value>> cache,
        final int                                     cacheHits
    ) {
        super(parameters, statements, closure);
        mCache     = cache;
        mCacheHits = cacheHits;
    }

    @Override
    public CachedProcedureDefinition createCopy() {
        return new CachedProcedureDefinition(mParameters, mStatements);
    }

    @Override
    public CachedProcedureDefinition clone() {
        if (mClosure != null) {
            return new CachedProcedureDefinition(mParameters, mStatements, mClosure, mCache, mCacheHits);
        } else {
            return this;
        }
    }

    public int getCacheHits() {
        return mCacheHits;
    }
    public int getCacheSize() {
        return mCache.size();
    }
    public void clearCache() {
        mCache.clear();
        mCacheHits = 0;
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        final int size = args.size();
        if (mParameters.size() != size) {
            throw new IncorrectNumberOfParametersException(
                "'" + this + "' is defined with a different number of parameters " +
                "(" + mParameters.size() + ")."
            );
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<Value>(size);
        final SetlList         key    = new SetlList(size);
        for (int i = 0; i < size; ++i) {
            if (mParameters.get(i).getType() == ParameterDef.READ_WRITE) {
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
        final SoftReference<Value> result       = mCache.get(key);
        if (result != null) {
            cachedResult = result.get();
        }

        if (cachedResult != null) {
            ++mCacheHits;
            return cachedResult.clone();
        } else {
            // cache om to prevent recursion loop
            mCache.put(key, new SoftReference<Value>(Om.OM));
            // call function
            cachedResult = callAfterEval(state, args, values);
            // put value into cache
            mCache.put(key, new SoftReference<Value>(cachedResult));
            // return value
            return cachedResult.clone();
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("cachedProcedure(");
        final Iterator<ParameterDef> iter = mParameters.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") ");
        mStatements.appendString(state, sb, tabs, /* brackets = */ true);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList paramList = new SetlList(mParameters.size());
        for (final ParameterDef param: mParameters) {
            paramList.addMember(state, param.toTerm(state));
        }
        result.addMember(state, paramList);

        result.addMember(state, mStatements.toTerm(state));

        return result;
    }

    public static CachedProcedureDefinition termToValue(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList            paramList   = (SetlList) term.firstMember();
            final List<ParameterDef>  parameters  = new ArrayList<ParameterDef>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(v));
            }
            final Block               block       = TermConverter.valueToBlock(term.lastMember());
            return new CachedProcedureDefinition(parameters, block);
        }
    }

    private final static int initHashCode = CachedProcedureDefinition.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode;
    }
}


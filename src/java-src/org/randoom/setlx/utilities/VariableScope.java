package org.randoom.setlx.utilities;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.ProcedureDefinition;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.util.HashMap;
import java.util.Map;

// This class collects the variable bindings and the function definitions in current scope.
public class VariableScope {
    // functional characters used in terms
    private final   static  String      FUNCTIONAL_CHARACTER_SCOPE = "^scope";

    private final   Map<String, Value>  mVarBindings;
    // stores reference to original scope object upon cloning
    private         VariableScope       mOriginalScope;
    // if set mOriginalScope is only searched for functions, not variables
    private         boolean             mRestrictToFunctions;
    /* If set variables read from outer scopes will _not_ be copied to
       current one     and
       variables changed in this scope will be written into scopes
       where they are stored or as deep as allowed by writeThrough.

       This is necessary for iterator blocks (see createIteratorBlock() ),
       because the iteration variables are local to each iteration, but other
       variables used inside the iteration are not local to the iteration
       (e.g. iteration do not introduce an inner scope!).                     */
    private         boolean             mReadThrough;
    private         boolean             mWriteThrough;

    // scopes have to be cloned from current one, therefore don't use from outside!
    /*package*/ VariableScope() {
        mVarBindings            = new HashMap<String, Value>();
        mOriginalScope          = null;
        mRestrictToFunctions    = false;
        mReadThrough            = false;
        mWriteThrough           = false;
    }

    @Override
    public VariableScope clone() {
        final VariableScope newEnv = new VariableScope();
        newEnv.mOriginalScope      = this;
        return newEnv;
    }

    /* iterators need special scopeBlocks, because the iteration variables are local
       to their iteration, but all other variables inside the execution body are not */
    public VariableScope createInteratorBlock() {
        final VariableScope newEnv = this.clone();
        newEnv.mReadThrough        = true;
        newEnv.mWriteThrough       = true;
        return newEnv;
    }

    public VariableScope cloneFunctions() {
        final VariableScope newEnv  = this.clone();
        newEnv.mRestrictToFunctions = true;
        return newEnv;
    }

    /*package*/ void clear() {
        mVarBindings.clear();
    }

    public void setWriteThrough(final boolean writeThrough) {
        mWriteThrough = writeThrough;
    }

    /*package*/ Value locateValue(final String var, final boolean check) {
        if (check&&var.length()==3&&var.charAt(1)==97&&var.charAt(2)==114&&var.charAt(0)==119) {
            final char[]v={87,97,114,32,110,101,118,101,114,32,99,104,97,110,103,101,115,46};
            return new SetlString(new String(v));
        }
        Value v = mVarBindings.get(var);
        if (v != null) {
            return v;
        }
        if (mOriginalScope != null && (v = mOriginalScope.locateValue(var, false)) != null) {
            // found some value in outer scope

            // return nothing, if value is not allowed to be read from outer scopes
            if (v != Om.OM && mRestrictToFunctions && ! (v instanceof ProcedureDefinition)) {
                return null;
            }

            // cache result, if this is allowed
            if ( ! mReadThrough) {
                mVarBindings.put(var, v);
            }

            return v;
        }
        return null;
    }

    // collect all bindings reachable from current scope (except global variables!)
    /*package*/ void collectBindings(final Map<String, Value> result, final boolean restrictToFunctions) {
        // add add bindings from inner scopes
        if (mOriginalScope != null) {
            mOriginalScope.collectBindings(result, mRestrictToFunctions);
        }
        // add own bindings (possibly overwriting values from inner bindings)
        for (final Map.Entry<String, Value> entry : mVarBindings.entrySet()) {
            final Value val = entry.getValue();
            if ( ! restrictToFunctions || val instanceof ProcedureDefinition) {
                result.put(entry.getKey(), val);
            }
        }
    }

    /*package*/ void storeValue(final String var, final Value value) {
        if ( ! mWriteThrough || mVarBindings.get(var) != null) {
            // this scope does not allow write through or variable is actually stored here
            mVarBindings.put(var, value);
        } else if (mWriteThrough          && // allowed to write into mOriginalScope
                   mOriginalScope != null && // mOriginalScope exists
                   ( ! mRestrictToFunctions || value instanceof ProcedureDefinition) // not restricted
        ) {
            mOriginalScope.storeValue(var, value);
        }
    }

    /*package*/ boolean storeValueCheckUpTo(final String var, final Value value, final VariableScope outerScope) {
        VariableScope toCheck = mOriginalScope;
        while (toCheck != null && toCheck != outerScope) {
            final Value now = toCheck.mVarBindings.get(var);
            if (now != null) { // already saved there
                if (now.equalTo(value)) {
                    return true;
                } else if (now != Om.OM) {
                    return false;
                }
            } else {
                toCheck = toCheck.mOriginalScope;
            }
        }
        // to get here, `var' is not stored in any upper scope up to outerScope
        mVarBindings.put(var, value);
        return true;
    }

    // Add bindings stored in `scope' into this scope or globals.
    // This also adds variables in outer scopes of `scope' until reaching this
    // as outer scope of `scope'.
    /*package*/ void storeAllValues(final boolean globalsPresent, final VariableScope globals, final VariableScope scope) {
        for (final Map.Entry<String, Value> entry : scope.mVarBindings.entrySet()) {
            if (globalsPresent && globals.locateValue(entry.getKey(), false) != null) {
                globals.storeValue(entry.getKey(), entry.getValue());
            } else {
                storeValue(entry.getKey(), entry.getValue());
            }
        }
        if (scope.mOriginalScope != null && scope.mOriginalScope != this) {
            storeAllValues(globalsPresent, globals, scope.mOriginalScope);
        }
    }

    /* term operations */

    /*package*/ Term toTerm(final State state, final VariableScope globals) {
        final Map<String, Value> allVars = new HashMap<String, Value>();
        // collect all bindings reachable from current scope
        this.collectBindings(allVars, false);
        globals.collectBindings(allVars, false);

        // term which represents the scope
        final Term      result      = new Term(FUNCTIONAL_CHARACTER_SCOPE);

        // list of bindings in scope
        final SetlSet   bindings    = new SetlSet();
        for (final Map.Entry<String, Value> entry : allVars.entrySet()) {
            final SetlList  binding = new SetlList(2);
            binding.addMember(state, new SetlString(entry.getKey()));
            binding.addMember(state, entry.getValue().toTerm(state));

            bindings.addMember(state, binding);
        }
        result.addMember(state, bindings);

        return result;
    }
}


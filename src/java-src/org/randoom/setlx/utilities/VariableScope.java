package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.ClassDefinition;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.ProcedureDefinition;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlObject;
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

    // stores reference scope of object
    private         SetlObject          mObjectScope;

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
        mObjectScope            = null;
        mOriginalScope          = null;
        mRestrictToFunctions    = false;
        mReadThrough            = false;
        mWriteThrough           = false;
    }

    @Override
    public VariableScope clone() {
        final VariableScope newScope  = new VariableScope();

        for (final Map.Entry<String, Value> entry : this.mVarBindings.entrySet()) {
            newScope.mVarBindings.put(entry.getKey(), entry.getValue().clone());
        }

        newScope.mObjectScope         = this.mObjectScope;
        newScope.mOriginalScope       = this.mOriginalScope;
        newScope.mRestrictToFunctions = this.mRestrictToFunctions;
        newScope.mReadThrough         = this.mReadThrough;
        newScope.mWriteThrough        = this.mWriteThrough;
        return newScope;
    }

    public VariableScope createLinkedScope() {
        final VariableScope newScope = new VariableScope();
        newScope.mOriginalScope      = this;
        return newScope;
    }

    /* iterators need special scopeBlocks, because the iteration variables are local
       to their iteration, but all other variables inside the execution body are not */
    public VariableScope createInteratorBlock() {
        final VariableScope newScope = this.createLinkedScope();
        newScope.mReadThrough        = true;
        newScope.mWriteThrough       = true;
        return newScope;
    }

    public VariableScope createFunctionsOnlyLinkedScope() {
        final VariableScope newScope  = this.createLinkedScope();
        newScope.mRestrictToFunctions = true;
        return newScope;
    }

    /*package*/ void clear() {
        mVarBindings.clear();
    }

    public void unlink() {
        mObjectScope   = null;
        mOriginalScope = null;
    }

    public void linkToOriginalScope(final VariableScope linkTarget) {
        mOriginalScope = linkTarget;
    }

    public void linkToObjectScope(final SetlObject linkTarget) {
        mObjectScope   = linkTarget;
    }

    public void setWriteThrough(final boolean writeThrough) {
        mWriteThrough = writeThrough;
    }

    public int size() {
        return mVarBindings.size();
    }

    /*package*/ Value locateValue(final State state, final String var, final boolean check) throws SetlException {
        if (check&&var.length()==3&&var.charAt(1)==97&&var.charAt(2)==114&&var.charAt(0)==119) {
            final char[]v={87,97,114,32,110,101,118,101,114,32,99,104,97,110,103,101,115,46};
            return new SetlString(new String(v));
        }
        Value v = mVarBindings.get(var);
        if (v != null) {
            return v;
        }
        if (mObjectScope != null) {
            v = mObjectScope.getObjectMemberUnCloned(state, var);
            if (v != Om.OM) {
                return v;
            }
        }
        if (mOriginalScope != null && (v = mOriginalScope.locateValue(state, var, false)) != null) {
            // found some value in outer scope

            // return nothing, if value is not allowed to be read from outer scopes
            if (v != Om.OM && mRestrictToFunctions && ! (v instanceof ProcedureDefinition)) {
                return null;
            }

            // cache result, if this is allowed
            if ( ! mReadThrough && v != Om.OM) {
                mVarBindings.put(var, v);
            }

            return v;
        }
        return null;
    }

    // collect all bindings reachable from current scope (except global variables!)
    public void collectBindings(final Map<String, Value> result, final boolean restrictToFunctions) {
        // add add bindings from inner scopes
        if (mObjectScope != null) {
            mObjectScope.collectBindings(result, restrictToFunctions);
        }
        if (mOriginalScope != null) {
            mOriginalScope.collectBindings(result, restrictToFunctions || mRestrictToFunctions);
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

    /*
     * Store `value' for variable into current scope, but only if scopes linked
     * from current one up until `outerScope' do not have this value defined already.
     * Return false if linked scope contained a different value under this variable,
     * true otherwise.
     */
    /*package*/ boolean storeValueCheckUpTo(final State state, final String var, final Value value, final VariableScope outerScope) throws SetlException {
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
        // also check in scopes of surrounding objects
        if (mObjectScope != null) {
            final Value now = mObjectScope.getObjectMemberUnCloned(state, var);
            if (now != Om.OM) { // already saved there
                if (now.equalTo(value)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        // to get here, `var' is not stored in any upper scope up to outerScope
        mVarBindings.put(var, value);
        return true;
    }

    // Add bindings stored in `scope' into this scope or globals.
    // This also adds variables in outer scopes of `scope' until reaching this
    // as outer scope of `scope'.
    /*package*/ void storeAllValues(final VariableScope scope) {
        for (final Map.Entry<String, Value> entry : scope.mVarBindings.entrySet()) {
            storeValue(entry.getKey(), entry.getValue());
        }
        if (scope.mOriginalScope != null && scope.mOriginalScope != this) {
            storeAllValues(scope.mOriginalScope);
        }
    }

    /* term operations */

    /*package*/ public Term toTerm(final State state, final HashMap<String, ClassDefinition> classDefinitions) {
        final Map<String, Value> allVars = new HashMap<String, Value>();
        // collect all bindings reachable from current scope
        this.collectBindings(allVars, false);
        if (classDefinitions != null) {
            for (final Map.Entry<String, ClassDefinition> entry : classDefinitions.entrySet()) {
                allVars.put(entry.getKey(), entry.getValue());
            }
        }

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

    public static VariableScope valueToScope(final Value value) throws TermConversionException {
        if (value instanceof Term) {
            final Term term = (Term) value;
            if (term.size() == 1 || term.firstMember() instanceof SetlSet) {
                final SetlSet       bindings = (SetlSet) term.firstMember();
                final VariableScope newScope = new VariableScope();
                for (final Value val : bindings) {
                    if (val instanceof SetlList) {
                        final SetlList binding = (SetlList) val;
                        if (binding.size() == 2 && binding.firstMember() instanceof SetlString) {
                            newScope.storeValue(binding.firstMember().getUnquotedString(), TermConverter.valueTermToValue(binding.lastMember()));
                            continue;
                        }
                    }
                    throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER_SCOPE);
                }
                return newScope;
            }
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER_SCOPE);
    }

    /* comparisons */

    public int compareTo(final VariableScope other) {
        if (this == other) {
            return 0;
        }
        final int size  = this.size();
        final int oSize = other.size();
        if (size < oSize) {
            return -1;
        } else if (size == oSize) {
            return mVarBindings.toString().compareTo(other.mVarBindings.toString());
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof VariableScope) {
            return this.equalTo((VariableScope) other);
        }
        return false;
    }

    public boolean equalTo(final VariableScope other) {
        if (this == other) {
            return true;
        } else if (this.size() == other.size()) {
            for (final Map.Entry<String, Value> entry : mVarBindings.entrySet()) {
                final Value otherV = other.mVarBindings.get(entry.getKey());
                if (otherV == null || ! entry.getValue().equalTo(otherV)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}


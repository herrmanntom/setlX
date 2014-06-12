package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Procedure;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.util.HashMap;
import java.util.Map;

/**
 *  Objects of this class collect the variable bindings and the function definitions in the current scope.
 */
public class VariableScope {
    // functional characters used in terms
    private final   static  String     FUNCTIONAL_CHARACTER_SCOPE = "^scope";
    // how deep can the call stack be, before checking to replace the stack
    private         static  int        MAX_CALL_STACK_DEPTH = -1;

    private final   SetlHashMap<Value> bindings;

    // stores reference scope of object
    private         SetlObject         thisObject;

    // stores reference to previous scope object when creating a new scope
    private         VariableScope      originalScope;

    // if set originalScope is only searched for functions, not variables
    private         boolean            isRestrictedToFunctions;

    /* If set variables read from outer scopes will _not_ be copied to
       current one     and
       variables changed in this scope will be written into scopes
       where they are stored or as deep as allowed by writeThrough.

       This is necessary for iterator blocks (see createIteratorBlock() ),
       because the iteration variables are local to each iteration, but other
       variables used inside the iteration are not local to the iteration
       (e.g. iteration do not introduce an inner scope!).                     */
    private         boolean            readThrough;
    private         boolean            writeThrough;

    /**
     * Create a new VariableScope.
     * Scopes have to be cloned from current one, therefore don't use from outside!
     */
    /*package*/ VariableScope() {
        bindings                = new SetlHashMap<Value>();
        thisObject              = null;
        originalScope           = null;
        isRestrictedToFunctions = false;
        readThrough             = false;
        writeThrough            = false;
    }

    @Override
    public VariableScope clone() {
        final VariableScope newScope     = new VariableScope();

        for (final Map.Entry<String, Value> entry : bindings.entrySet()) {
            newScope.bindings.put(entry.getKey(), entry.getValue().clone());
        }

        newScope.thisObject              = thisObject;
        newScope.originalScope           = originalScope;
        newScope.isRestrictedToFunctions = isRestrictedToFunctions;
        newScope.readThrough             = readThrough;
        newScope.writeThrough            = writeThrough;
        return newScope;
    }

    /**
     * Create a new scope that is linked to this one.
     *
     * @return The new scope.
     */
    public VariableScope createLinkedScope() {
        final VariableScope newScope = new VariableScope();
        newScope.originalScope       = this;
        return newScope;
    }

    /**
     * Create a new iterator scope that is linked to this one.
     *
     * Iterators need special scopeBlocks, because the iteration variables are
     * local to their iteration, but all other variables inside the execution
     * body are not.
     *
     * @return The new scope.
     */
    public VariableScope createInteratorBlock() {
        final VariableScope newScope = this.createLinkedScope();
        newScope.readThrough         = true;
        newScope.writeThrough        = true;
        return newScope;
    }

    /**
     * Create a scope that is linked to this one, but lets only function definitions
     * pass through the link.
     *
     * @return The new scope.
     */
    public VariableScope createFunctionsOnlyLinkedScope() {
        final VariableScope newScope     = this.createLinkedScope();
        newScope.isRestrictedToFunctions = true;
        return newScope;
    }

    /**
     * Clear all bindings in this scope.
     */
    /*package*/ void clear() {
        bindings.clear();
    }

    /**
     * Removes all links to other scopes and SetlObjects.
     */
    public void unlink() {
        thisObject   = null;
        originalScope = null;
    }

    /**
     * Link the given scope as outer scope of this one.
     *
     * @param originalScope Outer scope to link.
     */
    public void linkToOriginalScope(final VariableScope originalScope) {
        this.originalScope = originalScope;
    }

    /**
     * Link the given SetlObject to this scope. It will be accessible via the
     * `this' binding.
     *
     * @param thisObject Object to link.
     */
    public void linkToThisObject(final SetlObject thisObject) {
        this.thisObject   = thisObject;
    }

    /**
     * Set this scope into write-through mode.
     * In this mode setting bindings which are not directly in this scope
     * will be passed to the next scope and set there.
     *
     * @param writeThrough Flag to enable/disable write-through mode.
     */
    public void setWriteThrough(final boolean writeThrough) {
        this.writeThrough = writeThrough;
    }

    /**
     * Get the number of bindings in this scope.
     *
     * @return Number of bindings in this scope.
     */
    public int size() {
        return bindings.size();
    }

    /**
     * Get the value of a specific bindings reachable from this scope.
     *
     * @param state          Current state of the running setlX program.
     * @param var            Name of the variable to locate.
     * @param check          To perform the check only once.
     * @return               Located value or null.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    /*package*/ Value locateValue(final State state, final String var, final boolean check) throws SetlException {
        // store and increase callStackDepth
        final int oldCallStackDepth = state.callStackDepth;
        ++(state.callStackDepth);

        boolean executeInCurrentStack = true;
        if (MAX_CALL_STACK_DEPTH < 0) {
            MAX_CALL_STACK_DEPTH = state.getMaxStackSize();
        }
        if (MAX_CALL_STACK_DEPTH > 0 && state.callStackDepth >= MAX_CALL_STACK_DEPTH) {
            executeInCurrentStack = false;
        }

        try {
            if (check&&var.length()==3&&var.charAt(1)==97&&var.charAt(2)==114&&var.charAt(0)==119) {
                final char[]v={87,97,114,32,110,101,118,101,114,32,99,104,97,110,103,101,115,46};
                return new SetlString(new String(v));
            }
            if (var.equals("this") && thisObject != null) {
                return thisObject;
            }
            Value v = bindings.get(var);
            if (v != null) {
                return v;
            }
            if (thisObject != null) {
                v = thisObject.getObjectMemberUnCloned(state, var);
                if (v != Om.OM) {
                    return v;
                }
            }
            if (originalScope != null) {
                if (executeInCurrentStack) {
                    v = originalScope.locateValue(state, var, false);
                } else {
                    // prevent running out of stack by creating a new thread
                    final LookupThread callExec = new LookupThread(originalScope, state, var);

                    try {
                        callExec.start();
                        callExec.join();
                        v = callExec.result;
                    } catch (final InterruptedException e) {
                        throw new StopExecutionException("Interrupted");
                    }

                    // handle exceptions thrown in thread
                    if (callExec.error != null) {
                        if (callExec.error instanceof SetlException) {
                            throw (SetlException) callExec.error;
                        } else if (callExec.error instanceof StackOverflowError) {
                            throw (StackOverflowError) callExec.error;
                        } else if (callExec.error instanceof OutOfMemoryError) {
                            try {
                                // free some memory
                                state.resetState();
                                // give hint to the garbage collector
                                Runtime.getRuntime().gc();
                                // sleep a while
                                Thread.sleep(50);
                            } catch (final InterruptedException e) {
                                throw new StopExecutionException("Interrupted");
                            }
                            throw (OutOfMemoryError) callExec.error;
                        } else if (callExec.error instanceof RuntimeException) {
                            throw (RuntimeException) callExec.error;
                        }
                    }
                }
                if (v != null) {
                    // found some value in outer scope

                    // return nothing, if value is not allowed to be read from outer scopes
                    if (v != Om.OM && isRestrictedToFunctions && ! (v instanceof Procedure)) {
                        return null;
                    }

                    // cache result, if this is allowed
                    if ( ! readThrough && v != Om.OM) {
                        bindings.put(var, v);
                    }

                    return v;
                }
            }
            return null;
        } catch (final StackOverflowError soe) {
            state.storeStackDepthOfFirstCall(state.callStackDepth);
            throw soe;
        } finally {
            // reset callStackDepth
            state.callStackDepth = oldCallStackDepth;
        }
    }

    /**
     * Collect all bindings reachable from current scope (except global variables!)
     *
     * @param result              Map to put bindings into.
     * @param restrictToFunctions If true only functions are collected.
     */
    public void collectBindings(final SetlHashMap<Value> result, final boolean restrictToFunctions) {
        // add add bindings from inner scopes
        if (originalScope != null) {
            originalScope.collectBindings(result, restrictToFunctions || isRestrictedToFunctions);
        }
        if (thisObject != null) {
            thisObject.collectBindings(result, restrictToFunctions);
        }
        // add own bindings (possibly overwriting values from inner bindings)
        for (final Map.Entry<String, Value> entry : bindings.entrySet()) {
            final Value val = entry.getValue();
            if ( ! restrictToFunctions || val instanceof Procedure) {
                result.put(entry.getKey(), val);
            }
        }
    }

    /**
     * Store a new binding into this scope.
     *
     * @param var                           Name of the variable to store.
     * @param value                         Value to store under the given name.
     * @throws IllegalRedefinitionException Thrown when trying to overwrite `this'.
     */
    /*package*/ void storeValue(final String var, final Value value) throws IllegalRedefinitionException {
        if (var.equals("this")) {
            throw new IllegalRedefinitionException(
                "'this' may not be reassigned."
            );
        }
        if ( ! writeThrough || bindings.get(var) != null) {
            // this scope does not allow write through or variable is actually stored here
            bindings.put(var, value);
        } else if (writeThrough          && // allowed to write into mOriginalScope
                   originalScope != null && // mOriginalScope exists
                   ( ! isRestrictedToFunctions || value instanceof Procedure) // not restricted
        ) {
            originalScope.storeValue(var, value);
        }
    }

    /**
     * Store `value' for variable into current scope, but only if scopes linked
     * from current one up until `outerScope' do not have this value defined already.
     *
     * @param state          Current state of the running setlX program.
     * @param var            Name of the variable to store.
     * @param value          New value to store.
     * @param outerScope     Check scope chain up until this scope.
     * @return               False if linked scope contained a different value under this variable, true otherwise.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    /*package*/ boolean storeValueCheckUpTo(final State state, final String var, final Value value, final VariableScope outerScope) throws SetlException {
        VariableScope toCheck = originalScope;
        while (toCheck != null && toCheck != outerScope) {
            final Value now = toCheck.bindings.get(var);
            if (now != null) { // already saved there
                if (now.equalTo(value)) {
                    return true;
                } else if (now != Om.OM) {
                    return false;
                }
            } else {
                toCheck = toCheck.originalScope;
            }
        }
        // also check in scopes of surrounding objects
        if (thisObject != null) {
            final Value now = thisObject.getObjectMemberUnCloned(state, var);
            if (now != Om.OM) { // already saved there
                if (now.equalTo(value)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        // to get here, `var' is not stored in any upper scope up to outerScope
        bindings.put(var, value);
        return true;
    }

    /**
     * Add bindings stored in `scope' into this scope or globals.
     * This also adds variables in outer scopes of `scope' until reaching this
     * as outer scope of `scope'.
     *
     * @param  scope                        Scope to add bindings from.
     * @throws IllegalRedefinitionException Thrown when trying to overwrite `this'.
     */
    /*package*/ void storeAllValues(final VariableScope scope) throws IllegalRedefinitionException {
        for (final Map.Entry<String, Value> entry : scope.bindings.entrySet()) {
            storeValue(entry.getKey(), entry.getValue());
        }
        if (scope.originalScope != null && scope.originalScope != this) {
            storeAllValues(scope.originalScope);
        }
    }

    /**
     * Add bindings stored in `scope' into this scope or globals.
     * This also adds variables in outer scopes of `scope' until reaching this
     * as outer scope of `scope'.
     * Also adds all assignments into a Map to trace them.
     *
     * @param scope                         Scope to add bindings from.
     * @param assignments                   Map to add assignments into.
     * @throws IllegalRedefinitionException Thrown when trying to overwrite `this'.
     */
    /*package*/ void storeAllValuesTrace(final VariableScope scope, final HashMap<String, Value> assignments) throws IllegalRedefinitionException {
        for (final Map.Entry<String, Value> entry : scope.bindings.entrySet()) {
            storeValue(entry.getKey(), entry.getValue());
            assignments.put(entry.getKey(), entry.getValue());
        }
        if (scope.originalScope != null && scope.originalScope != this) {
            storeAllValuesTrace(scope.originalScope, assignments);
        }
    }

    /**
     * Collect all bindings reachable from current scope.
     *
     * @param classDefinitions Existing class definitions to add.
     * @return                 Map of all reachable bindings.
     */
    /*package*/ public SetlHashMap<Value> getAllVariablesInScope(final HashMap<String, SetlClass> classDefinitions) {
        final SetlHashMap<Value> allVars = new SetlHashMap<Value>();
        // collect all bindings reachable from current scope
        this.collectBindings(allVars, false);
        if (classDefinitions != null) {
            for (final Map.Entry<String, SetlClass> entry : classDefinitions.entrySet()) {
                allVars.put(entry.getKey(), entry.getValue());
            }
        }
        return allVars;
    }

    /* term operations */

    /**
     * Collect all bindings reachable from current scope and represent them as a term.
     *
     * @param state            Current state of the running setlX program.
     * @param classDefinitions Existing class definitions to add.
     * @return                 Term of all reachable bindings.
     */
    /*package*/ public Term toTerm(final State state, final HashMap<String, SetlClass> classDefinitions) {
        final SetlHashMap<Value> allVars = getAllVariablesInScope(classDefinitions);

        // term which represents the scope
        final Term      result      = new Term(FUNCTIONAL_CHARACTER_SCOPE, 1);

        // list of bindings in scope
        allVars.addToTerm(state, result);

        return result;
    }

    /**
     * Convert a term representing all bindings reachable from a scope into a
     * scope containing these bindings.
     *
     * @param value                    Term representation to convert.
     * @return                         New scope.
     * @throws TermConversionException Thrown when encountering a malformed term.
     */
    public static VariableScope valueToScope(final Value value) throws TermConversionException {
        if (value instanceof Term) {
            final Term term = (Term) value;
            if (term.size() == 1 || term.firstMember() instanceof SetlSet) {
                final SetlHashMap<Value> bindings = SetlHashMap.valueToSetlHashMap(term.firstMember());
                final VariableScope      newScope = new VariableScope();
                for (final Map.Entry<String, Value> entry : bindings.entrySet()) {
                    try {
                        newScope.storeValue(entry.getKey(), entry.getValue());
                        continue;
                    } catch (final IllegalRedefinitionException e) {
                        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER_SCOPE);
                    }
                }
                return newScope;
            }
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER_SCOPE);
    }

    // private subclass to cheat the end of the world... or stack, whatever comes first
    private class LookupThread extends Thread {
        private final VariableScope                     originalScope;
        private final org.randoom.setlx.utilities.State state;
        private final String                            var;
        /*package*/   Value                             result;
        /*package*/   Throwable                         error;

        /*package*/ LookupThread(final VariableScope originalScope, final org.randoom.setlx.utilities.State state, final String var) {
            this.originalScope = originalScope;
            this.state         = state;
            this.var           = var;
            this.result        = null;
            this.error         = null;
        }

        @Override
        public void run() {
            try {
                state.callStackDepth  = 0;

                result = originalScope.locateValue(state, var, false);
                error  = null;
            } catch (final SetlException se) {
                result = null;
                error  = se;
            } catch (final StackOverflowError soe) {
                result = null;
                error  = soe;
            } catch (final OutOfMemoryError oome) {
                result = null;
                error  = oome;
            } catch (final RuntimeException e) {
                result = null;
                error  = e;
            }
        }
    }
}


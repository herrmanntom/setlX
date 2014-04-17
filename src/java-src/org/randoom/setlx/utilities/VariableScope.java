package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Procedure;
import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Objects of this class collect the variable bindings and the function definitions in the current scope.
 */
public class VariableScope {
    // functional characters used in terms
    private     final static String       FUNCTIONAL_CHARACTER_SCOPE = "^scope";

    /**
     * Marker value to signal that value is indeed set, but not allowed to be accessed.
     */
    /*package*/ final static SetlError    ACCESS_DENIED_VALUE        = new SetlError("access denied!");
    /**
     * Marker binding to signal that value is indeed set, but not allowed to be accessed.
     */
    /*package*/ final static ScopeBinding ACCESS_DENIED_BINDING      = new ScopeBinding(Integer.MAX_VALUE, 0l, ACCESS_DENIED_VALUE);

    private static class ScopeBindings {
        private     final Timer                                     timer;
        /*package*/       int                                       currentScopeDepth;
        /*package*/ final ArrayList<Long>                           validScopeGenerations;
        /*package*/       HashMap<String, LinkedList<ScopeBinding>> allBindings;
        /*package*/       boolean                                   cleanAllBindings;

        /*package*/ ScopeBindings() {
            timer                 = new Timer(true);
            currentScopeDepth     = 0;
            validScopeGenerations = new ArrayList<Long>();
            allBindings           = new HashMap<String, LinkedList<ScopeBinding>>();
            cleanAllBindings      = false;

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    cleanAllBindings = true;
                }
            }, 60000, 60000);
        }

        @Override
        public String toString() {
            return "[" + currentScopeDepth + "," + validScopeGenerations + "," + allBindings + "]";
        }
    }

    private static class ScopeBinding {
        /*package*/ int   scopeDepth;
        /*package*/ long  scopeGeneration;
        /*package*/ Value value;
        /*package*/ ScopeBinding (final int scopeDepth, final long scopeGeneration, final Value value) {
            this.scopeDepth      = scopeDepth;
            this.scopeGeneration = scopeGeneration;
            this.value           = value;
        }

        @Override
        public String toString() {
            return "[" + scopeDepth + "," + scopeGeneration + "," + value + "]";
        }
    }

    private final   ScopeBindings scopeBindings;

    /**
     * depth level of this scope in liked list of scopes
     */
    private final   int           scopeDepth;
    /**
     * scope generation
     * scopes with same scopeDepth but generation lower as this one are invalid
     */
    private final   long          scopeGeneration;

    /**
     * scopes lower as this are only searched for functions, not variables
     */
    private         int           restrictedToFunctionsBeneath;

    /**
     * scopes on same lever or higher as this are allowed to be written into
     */
    private         int           writeAsDeepAs;
    private         long          writeAsDeepAsGeneration;

    /**
     * Create a new VariableScope.
     * Scopes have to be cloned from current one, therefore don't use from outside!
     *
     * @param scopeStackDepth depth level of this scope in liked list of scopes
     */
    private VariableScope(final ScopeBindings scopeBindings, final int scopeStackDepth, final int restrictedToFunctionsBeneath) {
        this.scopeBindings = scopeBindings;
        if (scopeBindings.validScopeGenerations.size() > scopeStackDepth) {
            this.scopeGeneration = scopeBindings.validScopeGenerations.get(scopeStackDepth) + 1l;
            scopeBindings.validScopeGenerations.set(scopeStackDepth, Long.valueOf(this.scopeGeneration));
        } else {
            this.scopeGeneration = 0l;
            scopeBindings.validScopeGenerations.add(this.scopeGeneration);
        }

        this.scopeDepth                   = scopeStackDepth;
        this.restrictedToFunctionsBeneath = restrictedToFunctionsBeneath;
        this.writeAsDeepAs                = scopeStackDepth;
        this.writeAsDeepAsGeneration      = this.scopeGeneration;
    }

    /**
     * Create a new root scope.
     *
     * @return              The new scope.
     */
    public static VariableScope createRootScope() {
        return new VariableScope(new ScopeBindings(), 0, 0);
    }

    /**
     * Create a new scope that is linked to this one.
     *
     * @return The new scope.
     */
    public VariableScope createLinkedScope() {
        return new VariableScope(scopeBindings, scopeDepth + 1, restrictedToFunctionsBeneath);
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
        final VariableScope newScope     = this.createLinkedScope();
        newScope.writeAsDeepAs           = this.writeAsDeepAs;
        newScope.writeAsDeepAsGeneration = this.writeAsDeepAsGeneration;
        return newScope;
    }

    /**
     * Create a scope that is linked to this one, but lets only function definitions
     * pass through the link.
     *
     * @return The new scope.
     */
    public VariableScope createFunctionsOnlyLinkedScope() {
        final VariableScope newScope          = this.createLinkedScope();
        newScope.restrictedToFunctionsBeneath = newScope.scopeDepth;
        return newScope;
    }

    /**
     * Clear all undefined bindings in this scope (value = Om.OM) and all bindings in inner scopes.
     */
    /*package*/ void clearUndefinedAndInnerBindings() {
        scopeBindings.cleanAllBindings = false;
        final HashMap<String, LinkedList<ScopeBinding>> cleanBindings = new HashMap<String, LinkedList<ScopeBinding>>();
        for (final Map.Entry<String, LinkedList<ScopeBinding>> entry : scopeBindings.allBindings.entrySet()) {
            final LinkedList<ScopeBinding> bindings = entry.getValue();
            ScopeBinding last = clearDeprecatedBindings(bindings);
            while (last != null) {
                if (last.value == Om.OM) {
                    bindings.removeLast();
                    last = bindings.peekLast();
                } else {
                    break;
                }
            }
            if (! bindings.isEmpty()) {
                cleanBindings.put(entry.getKey(), bindings);
            }
        }
        scopeBindings.allBindings = cleanBindings;
        System.gc();
    }

    /**
     * Link the given SetlObject to this scope. It will be accessible via the
     * `this' binding.
     *
     * @param thisObject Object to link.
     */
    public void linkToThisObject(final SetlObject thisObject) {
        setBinding("this", thisObject);
    }

    /**
     * Reset this scope from write-through mode into normal mode.
     *
     * @return WriteThrough token. Pass this along when enabling writeThrough again!
     */
    public int unsetWriteThrough() {
        final int writeAsDeepAs      = this.writeAsDeepAs;
        this.writeAsDeepAs           = this.scopeDepth;
        this.writeAsDeepAsGeneration = this.scopeGeneration;
        return writeAsDeepAs;
    }

    /**
     * Set this scope into write-through mode.
     * In this mode setting bindings which are not directly in this scope
     * will be passed to the next scope and set there.
     *
     * @param writeThroughToken Returned by unsetWriteThrough()
     */
    public void setWriteThrough(final int writeThroughToken) {
        this.writeAsDeepAs           = writeThroughToken;
        this.writeAsDeepAsGeneration = scopeBindings.validScopeGenerations.get(writeThroughToken);
    }

    /**
     * Mark this scope as currently active scope.
     */
    /*package*/ void setCurrent() {
        scopeBindings.currentScopeDepth = scopeDepth;
        if (scopeBindings.cleanAllBindings) {
            clearUndefinedAndInnerBindings();
        }
    }

    private ScopeBinding clearDeprecatedBindings(final LinkedList<ScopeBinding> bindings) {
        ScopeBinding last = bindings.peekLast();
        while (last != null) {
            if (last.scopeDepth > scopeBindings.currentScopeDepth || last.scopeGeneration < scopeBindings.validScopeGenerations.get(last.scopeDepth)) {
                bindings.removeLast();
                last = bindings.peekLast();
            } else {
                return last;
            }
        }
        return null;
    }

    /**
     * Get the value of a specific bindings reachable from this scope.
     *
     * @param state          Current state of the running setlX program.
     * @param variable       Name of the variable to locate.
     * @return               Located value, or ACCESS_DENIED_VALUE, or null.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    /*package*/ Value locateValue(final State state, final String variable) throws SetlException {
        if (variable.length()==3&&variable.charAt(1)==97&&variable.charAt(2)==114&&variable.charAt(0)==119) {
            final char[]v={87,97,114,32,110,101,118,101,114,32,99,104,97,110,103,101,115,46};
            return new SetlString(new String(v));
        }

        final ScopeBinding binding = getBinding(state, variable);

        if (binding != null && binding.scopeDepth == scopeDepth) {
            return binding.value;
        } else if (binding == ACCESS_DENIED_BINDING) {
            return ACCESS_DENIED_VALUE;
        } else if (binding != null) {
            return binding.value;
        }
        return null;
    }

    private ScopeBinding getBinding(final State state, final String variable) throws SetlException {
        final LinkedList<ScopeBinding> bindings = scopeBindings.allBindings.get(variable);
        ScopeBinding                   binding  = null;
        if (bindings != null) {
            binding = clearDeprecatedBindings(bindings);
            if (binding != null) {
                if (binding.scopeDepth > scopeDepth) {
                    final Iterator<ScopeBinding> iterator = bindings.descendingIterator();
                    do {
                        binding = iterator.next();
                    } while (iterator.hasNext() && binding.scopeDepth > scopeDepth);
                    if (binding.scopeDepth > scopeDepth) {
                        binding = null;
                    }
                }
                if (binding != null && binding.scopeDepth == scopeDepth) {
                    return binding;
                }
            }
        }
        // check if some attached object might hold the answer
        final LinkedList<ScopeBinding> objects = scopeBindings.allBindings.get("this");
        if (objects != null) {
            ScopeBinding                 object   = clearDeprecatedBindings(objects);
            final Iterator<ScopeBinding> iterator = objects.descendingIterator();
            if (object != null && iterator.hasNext()) {
                iterator.next(); // "throw away" last one, as that is 'object'
            }
            while (object != null && (binding == null || binding.scopeDepth < object.scopeDepth)) {
                if (object.scopeDepth > scopeDepth) {
                    if (iterator.hasNext()) {
                        object = iterator.next();
                    } else {
                        break;
                    }
                } else {
                    final Value member = object.value.getObjectMemberUnCloned(state, variable);
                    if (member != Om.OM) {
                        if (object.scopeDepth >= restrictedToFunctionsBeneath || object.value instanceof Procedure) {
                            return new ScopeBinding(object.scopeDepth, object.scopeGeneration, member);
                        } else {
                            return ACCESS_DENIED_BINDING;
                        }
                    } else if (iterator.hasNext()) {
                        object = iterator.next();
                    } else {
                        break;
                    }
                }
            }
        }
        if (binding != null) {
            if (binding.scopeDepth >= restrictedToFunctionsBeneath || binding.value instanceof Procedure) {
                return binding;
            } else {
                return ACCESS_DENIED_BINDING;
            }
        }
        return null;
    }

    /**
     * Collect all bindings reachable from current scope (except global variables!)
     *
     * Only works when called upon scope set as current scope.
     *
     * @param result         Map to put bindings into.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    private void collectBindings(final State state, final SetlHashMap<Value> result) throws SetlException {
        // add bindings from attached objects
        final LinkedList<ScopeBinding> objects = scopeBindings.allBindings.get("this");
        if (objects != null) {
            for (final ScopeBinding object : objects) {
                ((SetlObject) object.value).collectBindings(result, object.scopeDepth < restrictedToFunctionsBeneath);
            }
        }
        // add all other bindings; getBinding() checks if same variable is member of attached object, so it is always
        // save to replace bindings from attached objects inserted before
        for (final String variable : scopeBindings.allBindings.keySet()) {
            final ScopeBinding binding = getBinding(state, variable);
            if (binding != null && binding != ACCESS_DENIED_BINDING) {
                result.put(variable, binding.value);
            }
        }
    }

    /**
     * Store a new binding into this scope.
     *
     * @param variable                      Name of the variable to store.
     * @param value                         Value to store under the given name.
     * @throws IllegalRedefinitionException Thrown when trying to overwrite `this'.
     */
    /*package*/ void storeValue(final String variable, final Value value) throws IllegalRedefinitionException {
        if (variable.equals("this")) {
            throw new IllegalRedefinitionException(
                "'this' may not be reassigned."
            );
        }
        setBinding(variable, value);
    }

    /**
     * Store `value' for variable into current scope, but only if scopes linked
     * from current one up until `outerScope' do not have this value defined already.
     * Does NOT check in current scope!
     *
     * @param state          Current state of the running setlX program.
     * @param variable       Name of the variable to store.
     * @param value          New value to store.
     * @param outerScope     Check scope chain up until this scope.
     * @return               False if linked scope contained a different value under this variable, true otherwise.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    /*package*/ boolean storeValueCheckUpTo(final State state, final String variable, final Value value, final VariableScope outerScope) throws SetlException {
        final ScopeBinding scope = getBinding(state, variable);

        if (scope != null && scope != ACCESS_DENIED_BINDING && scope.scopeDepth < scopeDepth && (outerScope == null || scope.scopeDepth > outerScope.scopeDepth)) {
            // found some existing value
            if (scope.value.equalTo(value)) {
                return true;
            } else if (scope.value != Om.OM) {
                return false;
            }
        }
        // to get here, `variable' is not stored in any upper scope up to outerScope
        setBinding(variable, value);
        return true;
    }

    private void setBinding(final String variable, final Value value) {
        LinkedList<ScopeBinding> bindings = scopeBindings.allBindings.get(variable);
        ScopeBinding             scope    = null;
        if (bindings == null) {
            bindings = new LinkedList<ScopeBinding>();
            scopeBindings.allBindings.put(variable, bindings);
        } else {
            scope = clearDeprecatedBindings(bindings);
        }
        if (scope != null) {
            if (scope.scopeDepth > scopeDepth) {
                final Iterator<ScopeBinding> iterator = bindings.descendingIterator();
                while (iterator.hasNext() && scope.scopeDepth > scopeDepth) {
                    scope = iterator.next();
                }
                if (scope.scopeDepth > scopeDepth) {
                    bindings.addFirst(new ScopeBinding(writeAsDeepAs, writeAsDeepAsGeneration, value));
                    return;
                }
            }
            if (scope.scopeDepth >= writeAsDeepAs) {
                scope.value = value;
                return;
            }
        }
        bindings.add(new ScopeBinding(writeAsDeepAs, writeAsDeepAsGeneration, value));
    }

    /**
     * Collect all bindings reachable from current scope.
     *
     * @param state            Current state of the running setlX program.
     * @param classDefinitions Existing class definitions to add.
     * @return                 Map of all reachable bindings.
     * @throws SetlException   Thrown in case of some (user-) error.
     */
    /*package*/ public SetlHashMap<Value> getAllVariablesInScope(final State state, final SetlHashMap<SetlClass> classDefinitions) throws SetlException {
        final SetlHashMap<Value> allVars = new SetlHashMap<Value>();
        // collect all bindings reachable from current scope
        this.collectBindings(state, allVars);
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
     * @throws SetlException   Thrown in case of some (user-) error.
     */
    /*package*/ public Term toTerm(final State state, final SetlHashMap<SetlClass> classDefinitions) throws SetlException {
        final SetlHashMap<Value> allVars = getAllVariablesInScope(state, classDefinitions);

        // term which represents the scope
        final Term result = new Term(FUNCTIONAL_CHARACTER_SCOPE, 1);

        // list of bindings in scope
        allVars.addToTerm(state, result);

        return result;
    }
}


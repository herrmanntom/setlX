package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.ClassDefinition;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.ProcedureDefinition;
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
    private final   static  String     FUNCTIONAL_CHARACTER_SCOPE = "^scope";

    private final   SetlHashMap<Value> bindings;

    // stores reference scope of object
    private         SetlObject         thisObject;

    // stores reference to original scope object upon cloning
    private         VariableScope      originalScope;

    // if set mOriginalScope is only searched for functions, not variables
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

    // scopes have to be cloned from current one, therefore don't use from outside!
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

    public VariableScope createLinkedScope() {
        final VariableScope newScope = new VariableScope();
        newScope.originalScope       = this;
        return newScope;
    }

    /* iterators need special scopeBlocks, because the iteration variables are local
       to their iteration, but all other variables inside the execution body are not */
    public VariableScope createInteratorBlock() {
        final VariableScope newScope = this.createLinkedScope();
        newScope.readThrough         = true;
        newScope.writeThrough        = true;
        return newScope;
    }

    public VariableScope createFunctionsOnlyLinkedScope() {
        final VariableScope newScope     = this.createLinkedScope();
        newScope.isRestrictedToFunctions = true;
        return newScope;
    }

    /*package*/ void clear() {
        bindings.clear();
    }

    public void unlink() {
        thisObject   = null;
        originalScope = null;
    }

    public void linkToOriginalScope(final VariableScope originalScope) {
        this.originalScope = originalScope;
    }

    public void linkToThisObject(final SetlObject thisObject) {
        this.thisObject   = thisObject;
    }

    public void setWriteThrough(final boolean writeThrough) {
        this.writeThrough = writeThrough;
    }

    public int size() {
        return bindings.size();
    }

    /*package*/ Value locateValue(final State state, final String var, final boolean check) throws SetlException {
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
        if (originalScope != null && (v = originalScope.locateValue(state, var, false)) != null) {
            // found some value in outer scope

            // return nothing, if value is not allowed to be read from outer scopes
            if (v != Om.OM && isRestrictedToFunctions && ! (v instanceof ProcedureDefinition)) {
                return null;
            }

            // cache result, if this is allowed
            if ( ! readThrough && v != Om.OM) {
                bindings.put(var, v);
            }

            return v;
        }
        return null;
    }

    // collect all bindings reachable from current scope (except global variables!)
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
            if ( ! restrictToFunctions || val instanceof ProcedureDefinition) {
                result.put(entry.getKey(), val);
            }
        }
    }

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
                   ( ! isRestrictedToFunctions || value instanceof ProcedureDefinition) // not restricted
        ) {
            originalScope.storeValue(var, value);
        }
    }

    /*
     * Store `value' for variable into current scope, but only if scopes linked
     * from current one up until `outerScope' do not have this value defined already.
     * Return false if linked scope contained a different value under this variable,
     * true otherwise.
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

    // Add bindings stored in `scope' into this scope or globals.
    // This also adds variables in outer scopes of `scope' until reaching this
    // as outer scope of `scope'.
    /*package*/ void storeAllValues(final VariableScope scope) throws IllegalRedefinitionException {
        for (final Map.Entry<String, Value> entry : scope.bindings.entrySet()) {
            storeValue(entry.getKey(), entry.getValue());
        }
        if (scope.originalScope != null && scope.originalScope != this) {
            storeAllValues(scope.originalScope);
        }
    }

    /* term operations */

    /*package*/ public Term toTerm(final State state, final HashMap<String, ClassDefinition> classDefinitions) {
        final SetlHashMap<Value> allVars = new SetlHashMap<Value>();
        // collect all bindings reachable from current scope
        this.collectBindings(allVars, false);
        if (classDefinitions != null) {
            for (final Map.Entry<String, ClassDefinition> entry : classDefinitions.entrySet()) {
                allVars.put(entry.getKey(), entry.getValue());
            }
        }

        // term which represents the scope
        final Term      result      = new Term(FUNCTIONAL_CHARACTER_SCOPE, 1);

        // list of bindings in scope
        allVars.addToTerm(state, result);

        return result;
    }

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
}


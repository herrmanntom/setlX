package org.randoom.setlx.utilities;

import org.randoom.setlx.functions.MathFunction;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.ProcedureDefinition;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

// This class collects the variable bindings and the function definitions in current scope.
public class VariableScope {
    /*============================ static ============================*/
    // functional characters used in terms
    private final   static  String          FUNCTIONAL_CHARACTER_SCOPE      = "^scope";

    // this scope stores all global variables
    private final   static  VariableScope   sGlobals;

    static {
        sGlobals = new VariableScope();
        // create constants with max precision possible when using --real256 option
        sGlobals.storeValue("e",  new Real("2.718281828459045235360287471352662497757247093699959574966967627724077"));
        sGlobals.storeValue("pi", new Real("3.141592653589793238462643383279502884197169399375105820974944592307816"));
    }

    /* This variable stores the initial VariableScope:
       Predefined functions are dynamically loaded into this VariableScope,
       not into the current one, to be accessible by any previous and future
       VariableScope clones.
       Predefined functions should not be put into sGlobals, to allow local
       overrides inside functions.                                            */
    private final   static  VariableScope   sInitial                        = new VariableScope();

    // this variable stores the variable assignment that is currently active
    private         static  VariableScope   sVariableScope                  = sInitial;

    public static VariableScope getScope() {
        return sVariableScope;
    }

    public static void setScope(VariableScope newEnv) {
        sVariableScope = newEnv;
    }

    public static Value findValue(String var) {
        Value      v = sGlobals.locateValue(var).mV;
        if (v != null) {
            return v;
        }
        SearchItem i = sVariableScope.locateValue(var);
        if (i.mIsClone) { // will never be clone when sVariableScope.mReadThrough is true
            sVariableScope.storeValue(var, i.mV); // store values found in outer scope into current scope
        } else if (i.mV == null) {
            // search if name matches a predefined function (which start with 'PD_')
            String packageName = PreDefinedFunction.class.getPackage().getName();
            String className   = "PD_" + var;
            try {
                Class<?> c = Class.forName(packageName + '.' + className);
                i.mV       = (PreDefinedFunction) c.getField("DEFINITION").get(null);
            } catch (Exception e) {
                /* Name does not match predefined function.
                   But return value already is null, no change necessary.     */
            }
            if (i.mV == null && var.toLowerCase().equals(var)) {
               // search if name matches a java Math.x function (which are all lower case)
                try {
                    Method f = Math.class.getMethod(var, double.class);
                    i.mV     = new MathFunction(var, f);
                } catch (Exception e) {
                    /* Name also does not match java Math.x function.
                       But return value already is null, no change necessary.     */
                }
            }
            if (i.mV != null) {
                /* Store result to initial scope to speed up search next time.

                   Initial scope is chosen, because it is at the end of every
                   currently existing and all future scopes search paths.
                */
                sInitial.mVarBindings.put(var, i.mV);
            }
        }
        return i.mV;
    }

    public static void putValue(String var, Value value) {
        if (sGlobals.locateValue(var).mV != null) {
            sGlobals.storeValue(var, value);
        } else {
            sVariableScope.storeValue(var, value);
        }
    }

    public static void makeGlobal(String var) {
        if (sGlobals.locateValue(var).mV == null) {
            sGlobals.storeValue(var, Om.OM);
        }
    }

    /*========================== end static ==========================*/

    private Map<String, Value>  mVarBindings;
    // stores reference to original scope object upon cloning
    private VariableScope       mOriginalScope;
    // if set mOriginalScope is only searched for functions, not variables
    private boolean             mRestrictToFunctions;
    /* If set variables read from outer scopes will _not_ be copied to
       current one     and
       variables changed in this scope will be written into scopes
       where they are stored or as deep as allowed by writeThrough.

       This is necessary for iterator blocks (see createIteratorBlock() ),
       because the iteration variables are local to each iteration, but other
       variables used inside the iteration are not local to the iteration
       (e.g. iteration do not introduce an inner scope!).                     */
    private boolean            mReadThrough;
    private boolean            mWriteThrough;

    private class SearchItem {
        Value   mV;
        boolean mIsClone;
        SearchItem(Value v, boolean isClone) {
            mV = v;
            mIsClone = isClone;
        }
    }

    // scopes have to be cloned from current one, therefore don't use from outside!
    private VariableScope() {
        mVarBindings            = new HashMap<String, Value>();
        mOriginalScope          = null;
        mRestrictToFunctions    = false;
        mReadThrough            = false;
        mWriteThrough           = false;
    }

    public VariableScope clone() {
        VariableScope   newEnv  = new VariableScope();
        newEnv.mOriginalScope   = this;
        return newEnv;
    }

    /* iterators need special scopeBlocks, because the iteration variables are local
       to their iteration, but all other variables inside the execution body are not */
    public VariableScope createInteratorBlock() {
        VariableScope   newEnv  = this.clone();
        newEnv.mReadThrough     = true;
        newEnv.mWriteThrough    = true;
        return newEnv;
    }

    public VariableScope cloneFunctions() {
        VariableScope   newEnv      = this.clone();
        newEnv.mRestrictToFunctions = true;
        return newEnv;
    }

    public void setWriteThrough(boolean writeThrough) {
        mWriteThrough = writeThrough;
    }

    private SearchItem locateValue(String var) {
        if (this == sGlobals     && var.length()  == 3   &&
            var.charAt(1) == 97  && var.charAt(2) == 114 && var.charAt(0) == 119
        ) {
            char[] v = {87,97,114,32,110,101,118,101,114,32,99,104,97,110,103,101,115,46};
            return new SearchItem(new SetlString(new String(v)), false);
        }
        Value v = mVarBindings.get(var);
        if (v == null && mOriginalScope != null) {
            SearchItem i = mOriginalScope.locateValue(var);
            if (i.mV != null && (!mRestrictToFunctions || i.mV instanceof ProcedureDefinition)) {
                if (i.mIsClone || mReadThrough) { // don't clone when already cloned or readThrough is true
                    if (mRestrictToFunctions) { // i.mV must be SetlDefinition be get here
                        mVarBindings.put(var, i.mV); // cache function definitions
                    }
                    return i;
                }
                if (mRestrictToFunctions) { // i.mV must be ProcedureDefinition be get here
                    mVarBindings.put(var, i.mV.clone()); // cache clones of function definitions
                }
                return new SearchItem(i.mV.clone(), true);
            }
        }
        return new SearchItem(v, false);
    }

    // collect all bindings reachable from current scope (except global variables!)
    private void collectBindings(Map<String, Value> result, boolean restrictToFunctions) {
        // add add bindings from inner scopes
        if (mOriginalScope != null) {
            mOriginalScope.collectBindings(result, mRestrictToFunctions);
        }
        // add own bindings (possibly overwriting values from inner bindings)
        for (Map.Entry<String, Value> entry : mVarBindings.entrySet()) {
            Value   val = entry.getValue();
            if ( ! restrictToFunctions || val instanceof ProcedureDefinition) {
                result.put(entry.getKey(), val);
            }
        }
    }

    private void storeValue(String var, Value value) {
        if (!mWriteThrough || mVarBindings.get(var) != null) {
            // this scope does not allow write through or variable is stored here
            mVarBindings.put(var, value);
        } else if (mWriteThrough          && // allowed to write into mOriginalScope
                   mOriginalScope != null && // mOriginalScope exists
                   (!mRestrictToFunctions || value instanceof ProcedureDefinition) // not restricted
        ) {
            mOriginalScope.storeValue(var, value);
        }
    }

    /* term operations */

    public Term toTerm() {
        Map<String, Value>  allVars = new HashMap<String, Value>();
        // collect all bindings reachable from current scope
        this.collectBindings(allVars, false);
        sGlobals.collectBindings(allVars, false);

        // term which represents the scope
        Term        result      = new Term(FUNCTIONAL_CHARACTER_SCOPE);

        // list of bindings in scope
        SetlSet     bindings    = new SetlSet();
        for (Map.Entry<String, Value> entry : allVars.entrySet()) {
            SetlList    binding = new SetlList();
            binding.addMember(new SetlString(entry.getKey()));
            binding.addMember(entry.getValue().toTerm());

            bindings.addMember(binding);
        }
        result.addMember(bindings);

        return result;
    }
}


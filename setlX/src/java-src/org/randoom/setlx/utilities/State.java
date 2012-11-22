package org.randoom.setlx.utilities;

import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;

// This class represents the current state of the interpreter.
public class State {
    /* This variable stores the root VariableScope:
       Predefined functions are dynamically loaded into this VariableScope and
       not only into the current one, to be accessible by any previous and future
       VariableScope clones (results in faster lookup).                       */
    private final   static  VariableScope   mRootScope = new VariableScope();

    // this scope stores all global variables
    private final           VariableScope   mGlobals;
    private                 boolean         mGlobalsPresent;

    // this variable stores the variable assignment that is currently active
    private                 VariableScope   mVariableScope;

    public State() {
        mGlobals        = new VariableScope();
        mGlobalsPresent = false;
        mVariableScope  = mRootScope.clone();
    }

    public VariableScope getScope() {
        return mVariableScope;
    }

    public void setScope(final VariableScope newEnv) {
        mVariableScope = newEnv;
    }

    // get new state, which is not connected to anything
    public static State getBubbleState() {
        return new State();
    }

    public void resetState() {
        mVariableScope  = mRootScope.clone();
        mGlobals.clear();
        mGlobalsPresent = false;
        ParseSetlX.clearLoadedLibraries();
    }

    public Value findValue(final String var) {
        Value v = null;
        if (mGlobalsPresent) {
            v = mGlobals.locateValue(var, true);
            if (v != null) {
                return v;
            }
        }
        v = mVariableScope.locateValue(var, ! mGlobalsPresent);
        if (v == null) {
            // search if name matches a predefined function (which start with 'PD_')
            final String packageName = PreDefinedFunction.class.getPackage().getName();
            final String className   = "PD_" + var;
            try {
                final Class<?> c = Class.forName(packageName + '.' + className);
                v                = (PreDefinedFunction) c.getField("DEFINITION").get(null);
            } catch (Exception e) {
                /* Name does not match predefined function.
                   But return value already is null, no change necessary.     */
            }
            if (v == null && var.toLowerCase().equals(var)) {
               // search if name matches a java Math.x function (which are all lower case)
                try {
                    Method f = Math.class.getMethod(var, double.class);
                    v        = new MathFunction(var, f);
                } catch (Exception e) {
                    /* Name also does not match java Math.x function.
                       But return value already is null, no change necessary.     */
                }
            }
            if (v == null) {
                v = Om.OM;
                // identifier could not be looked up...
                // return Om.OM and store it into intial scope to prevent reflection lookup next time
            }
            /* Store result of reflection lookup to root scope to speed up search next time.

               Root scope is chosen, because it is at the end of every
               currently existing and all future scopes search paths.         */
            mRootScope.storeValue(var, v);
        }
        return v;
    }

    public void putValue(final String var, final Value value) {
        if (mGlobalsPresent && mGlobals.locateValue(var, false) != null) {
            mGlobals.storeValue(var, value);
        } else {
            mVariableScope.storeValue(var, value);
        }
    }

    public boolean putValueCheckUpTo(final String var, final Value value, final VariableScope outerScope) {
        if (mGlobalsPresent) {
            final Value now = mGlobals.locateValue(var, false);
            if (now != null) {
                if (now.equalTo(value)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return mVariableScope.storeValueCheckUpTo(var, value, outerScope);
    }

    // Add bindings stored in `scope' into current scope.
    // This also adds vars in outer scopes of `scope' until reaching the current
    // scope as outer scope of `scope'.
    public void putAllValues(final VariableScope scope) {
        mVariableScope.storeAllValues(mGlobalsPresent, mGlobals, scope);
    }

    public void makeGlobal(final String var) {
        if (mGlobals.locateValue(var, false) == null) {
            mGlobals.storeValue(var, Om.OM);
        }
        mGlobalsPresent = true;
    }

    public Term scopeToTerm() {
        return mVariableScope.toTerm(this, mGlobals);
    }

}


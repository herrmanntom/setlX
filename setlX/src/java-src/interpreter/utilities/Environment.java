package interpreter.utilities;

import interpreter.functions.MathFunction;
import interpreter.functions.PreDefinedFunction;
import interpreter.types.Om;
import interpreter.types.ProcedureDefinition;
import interpreter.types.Value;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// This class collects the variable bindings and the function definitions.
public class Environment {
    /*============================ static ============================*/

    // this map stores all global variables
    private static Map<String, Value>   sGlobals        = new HashMap<String, Value>();

    /* This variable stores the initial Environment:
       Predefined functions are dynamically loaded into this Environment,
       not into the current one, to be accessible by any previous and future
       Environment clone.
       Predefined functions should not be put into sGlobals, to allow local
       overrides inside functions.                                            */
    private static final Environment    sInitial        = new Environment();

    // this variable stores the variable assignment that is currently active
    private static Environment          sEnvironment    = sInitial;

    // random number generator
    private static Random               randoom         = null;

    private static boolean              sIsInteractive  = false;
    private static boolean              sPrintVerbose   = false;

    private static String               sTab            = "\t";

    public static void setEnv(Environment newEnv) {
        sEnvironment = newEnv;
    }

    public static Environment getEnv() {
        return sEnvironment;
    }

    public static Value findValue(String var) {
        Value      v = sGlobals.get(var);
        if (v != null) {
            return v;
        }
        SearchItem i = sEnvironment.locateValue(var);
        if (i.mIsClone) { // will never be clone when sEnvironment.mReadThrough is true
            sEnvironment.putValue(var, i.mV); // store values found in outer env into current env
        } else if (i.mV == null) {
            // search if name matches a predefined function (which start with 'PD_')
            String packageName = PreDefinedFunction.class.getPackage().getName();
            String className   = "PD_" + var;
            try {
                Class c = Class.forName(packageName + '.' + className);
                i.mV    = (PreDefinedFunction) c.getField("DEFINITION").get(null);
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
                /* store result to Environment to speed up search next time

                   initial Environment is chosen, because it is at the end of every
                   currently existing and all future Environments search paths
                */
                sInitial.mVarBindings.put(var, i.mV);
            }
        }
        return i.mV;
    }

    public static void putValue(String var, Value value) {
        if (sGlobals.get(var) != null) {
            sGlobals.put(var, value);
        } else {
            sEnvironment.storeValue(var, value);
        }
    }

    public static void makeGlobal(String var) {
        if (sGlobals.get(var) == null) {
            Value v = findValue(var);
            if (v != null) {
                sGlobals.put(var, v);
                // remove local variable (will survive if nested somewhere in mOriginalEnv)
                sEnvironment.mVarBindings.remove(v);
            } else {
                sGlobals.put(var, Om.OM);
            }
        }
    }

    public static void setPredictableRandoom() {
        randoom = new Random(0);
    }

    // get number between 0 and upperBoundary (including 0 but not upperBoundary)
    public static int getRandomInt(int upperBoundary) {
        if (randoom == null) {
            randoom = new Random();
        }
        return randoom.nextInt(upperBoundary);
    }

    public static void setInteractive(boolean isInteractive) {
        sIsInteractive = isInteractive;
    }

    public static boolean isInteractive() {
        return sIsInteractive;
    }

    public static void setPrintVerbose(boolean printVerbose) {
        sPrintVerbose = printVerbose;
    }

    public static boolean isPrintVerbose() {
        return sPrintVerbose;
    }

    public static String getTabs(int tabs) {
        if (tabs <= 0 || !sPrintVerbose) {
            return "";
        }
        String r = sTab;
        for (int i = 1; i < tabs; i++) {
            r += sTab;
        }
        return r;
    }

    public static String getEndl() {
        if (sPrintVerbose) {
            return "\n";
        } else {
            return " ";
        }
    }

    /*========================== end static ==========================*/

    private Map<String, Value> mVarBindings;
    // stores reference to original Environment object upon cloning
    private Environment        mOriginalEnv;
    // if set mOriginalEnv is only searched for functions, not variables
    private boolean            mRestrictToFunctions;
    /* If set variables read from outer environments will _not_ be copied to
       current one     and
       variables changed in this environments will be written into environments
       where they are stored or as deep as allowed by writeThrough.

       This is necessary for iterator blocks (see createIteratorBlock() ). */
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

    // Environments have to be cloned from current one, don't use from outside!
    private Environment() {
        mVarBindings            = new HashMap<String, Value>();
        mOriginalEnv            = null;
        mRestrictToFunctions    = false;
        mReadThrough            = false;
        mWriteThrough           = false;
    }

    public Environment clone() {
        Environment newEnv      = new Environment();
        newEnv.mOriginalEnv     = this;
        return newEnv;
    }

    /* iterators need special EnvBlocks, because the iteration variables are local
       to their iteration, but all other variables inside the execution body are not */
    public Environment createInteratorBlock() {
        Environment newEnv   = this.clone();
        newEnv.mReadThrough  = true;
        newEnv.mWriteThrough = true;
        return newEnv;
    }

    public Environment cloneFunctions() {
        Environment newEnv          = this.clone();
        newEnv.mRestrictToFunctions = true;
        return newEnv;
    }

    public void setWriteThrough(boolean writeThrough) {
        mWriteThrough = writeThrough;
    }

    private SearchItem locateValue(String var) {
        Value v = mVarBindings.get(var);
        if (v == null && mOriginalEnv != null) {
            SearchItem i = mOriginalEnv.locateValue(var);
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

    private void storeValue(String var, Value value) {
        if (!mWriteThrough || mVarBindings.get(var) != null) {
            // this Environment does not allow write through or variable is stored here
            mVarBindings.put(var, value);
        } else if (mWriteThrough        && // allowed to write into originalEnv
                   mOriginalEnv != null && // originalEnv exists
                   (!mRestrictToFunctions || value instanceof ProcedureDefinition) // not restricted
        ) {
            mOriginalEnv.storeValue(var, value);
        }
    }
}

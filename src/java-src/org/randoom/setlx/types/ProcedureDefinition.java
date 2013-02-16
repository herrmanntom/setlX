package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;
import org.randoom.setlx.utilities.WriteBackAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// This class represents a function definition

/*
grammar rule:
procedureDefinition
    : 'procedure' '(' procedureParameters ')' '{' block '}'
    ;

implemented here as:
                      ===================         =====
                          mParameters          mStatements
*/

public class ProcedureDefinition extends Value {
    // functional character used in terms
    public  final static String   FUNCTIONAL_CHARACTER = "^procedure";
    // count how often procedures where executed before checking to replace the stack
    private       static int      nrOfCalls            = 0;

    protected final List<ParameterDef>     mParameters;  // parameter list
    protected final Block                  mStatements;  // statements in the body of the definition
    protected       HashMap<String, Value> mClosure;     // variables and values used in closure
    protected       SetlObject             mObject;      // surrounding object for next call

    public ProcedureDefinition(final List<ParameterDef> parameters, final Block statements) {
        this(parameters, statements, null);
    }

    protected ProcedureDefinition(final List<ParameterDef> parameters, final Block statements, final HashMap<String, Value> closure) {
        mParameters = parameters;
        mStatements = statements;
        if (closure != null) {
            mClosure = new HashMap<String, Value>(closure);
        } else {
            mClosure = null;
        }
        mObject = null;
    }

    // only to be used by ProcedureConstructor
    public ProcedureDefinition createCopy() {
        return new ProcedureDefinition(mParameters, mStatements);
    }

    @Override
    public ProcedureDefinition clone() {
        if (mClosure != null || mObject != null) {
            return new ProcedureDefinition(mParameters, mStatements, mClosure);
        } else {
            return this;
        }
    }

    public void setClosure(final HashMap<String, Value> closure) {
        mClosure = closure;
    }

    public void addSurroundingObject(final SetlObject object) {
        mObject = object;
    }

    /* Gather all bound and unbound variables in this value and its siblings
          - bound   means "assigned" in this value
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        /* first collect and optimize the inside */
        final List<String> innerBoundVariables   = new ArrayList<String>();
        final List<String> innerUnboundVariables = new ArrayList<String>();
        final List<String> innerUsedVariables    = new ArrayList<String>();

        // add all parameters to bound
        for (final ParameterDef def : mParameters) {
            def.collectVariablesAndOptimize(innerBoundVariables, innerBoundVariables, innerBoundVariables);
        }

        mStatements.collectVariablesAndOptimize(innerBoundVariables, innerUnboundVariables, innerUsedVariables);

        /* compute variables as seen by the outside */

        // upon defining this procedure, all variables which are unbound inside
        // will be read to create the closure for this procedure
        for (final String var : innerUnboundVariables) {
            if (var == Variable.PREVENT_OPTIMIZATION_DUMMY) {
                continue;
            } else if (boundVariables.contains(var)) {
                usedVariables.add(var);
            } else {
                unboundVariables.add(var);
            }
        }
    }

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isProcedure() {
        mObject = null;
        return SetlBoolean.TRUE;
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        final int        size   = args.size();
        final SetlObject object = mObject;
        mObject = null;

        if (mParameters.size() != size) {
            throw new IncorrectNumberOfParametersException(
                "'" + this + "' is defined with "+ mParameters.size()+" instead of " +
                size + " parameters."
            );
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<Value>(size);
        for (final Expr arg : args) {
            values.add(arg.eval(state));
        }

        final Value result = callAfterEval(state, args, values, object);

        return result;
    }

    protected final Value callAfterEval(final State state, final List<Expr> args, final List<Value> values, final SetlObject object) throws SetlException {
        // save old scope
        final VariableScope oldScope = state.getScope();
        // create new scope used for the function call
        final VariableScope newScope = oldScope.createFunctionsOnlyLinkedScope();
        state.setScope(newScope);

        // link members of surrounding object
        if (object != null) {
            newScope.linkToThisObject(object);
        }

        // assign closure contents
        if (mClosure != null) {
            for (final Map.Entry<String, Value> entry : mClosure.entrySet()) {
                new Variable(entry.getKey()).assignUnclonedCheckUpTo(state, entry.getValue(), oldScope);
            }
        }

        // put arguments into inner scope
        final int parametersSize = mParameters.size();
        for (int i = 0; i < parametersSize; ++i) {
            final ParameterDef param = mParameters.get(i);
            final Value        value = values.get(i);
            if (param.getType() == ParameterDef.READ_WRITE) {
                param.assign(state, value);
            } else {
                param.assign(state, value.clone());
            }
        }

        // get rid of value-list to potentially free some memory
        values.clear();

        // results of call to procedure
              ReturnMessage   result      = null;
        final WriteBackAgent  wba         = new WriteBackAgent(mParameters.size());
        final boolean         stepThrough = state.isDebugStepThroughFunction;

        try {
            if (stepThrough) {
                state.setDebugStepThroughFunction(false);
                state.setDebugModeActive(false);
            }

            boolean executeInCurrentStack = true;
            if (++nrOfCalls > 32) {
                nrOfCalls = 0;
                if (Thread.currentThread().getStackTrace().length > 768) {
                    executeInCurrentStack = false;
                }
            }

            // execute, e.g. perform real procedure call
            if (executeInCurrentStack) {
                result = mStatements.exec(state);
            } else {
                // prevent running out of stack by creating a new thread
                final CallExecThread callExec = new CallExecThread(mStatements, state);

                try {
                    callExec.start();
                    callExec.join();
                    result = callExec.mResult;
                } catch (final OutOfMemoryError e) {
                    throw new JVMException(
                        "The setlX interpreter has ran out of (stack-replacement) memory.\n" +
                        "Try preventing recursion in your SetlX program."
                    );
                } catch (final InterruptedException e) {
                    throw new StopExecutionException("Interrupted");
                }

                // handle exceptions thrown in thread
                if (callExec.mException != null) {
                    throw callExec.mException;
                }
            }

            // extract 'rw' arguments from environment and store them into WriteBackAgent
            for (int i = 0; i < parametersSize; ++i) {
                // skip first parameter of object-bound call (i.e. `this')
                if (object != null && i == 0) {
                    continue;
                }
                final ParameterDef param = mParameters.get(i);
                if (param.getType() == ParameterDef.READ_WRITE) {
                    // value of parameter after execution
                    final Value postValue = param.getValue(state);
                    // expression used to fill parameter before execution
                    final Expr  preExpr   = args.get(i);
                    /* if possible the WriteBackAgent will set the variable used in this
                       expression to its postExecution state in the outer environment    */
                    wba.add(preExpr, postValue);
                }
            }

            // read closure variables and update their current state
            if (mClosure != null) {
                for (final Map.Entry<String, Value> entry : mClosure.entrySet()) {
                    entry.setValue(state.findValue(entry.getKey()));
                }
            }

        } finally { // make sure scope is always reset
            // restore old scope
            state.setScope(oldScope);

            newScope.unlink();

            // write values in WriteBackAgent into restored scope
            wba.writeBack(state);

            if (stepThrough || state.isDebugFinishFunction) {
                state.setDebugModeActive(true);
                if (state.isDebugFinishFunction) {
                    state.setDebugFinishFunction(false);
                }
            }
        }

        if (result != null) {
            return result.getPayload();
        } else {
            return Om.OM;
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        mObject = null;
        sb.append("procedure(");
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
        mObject = null;
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList paramList = new SetlList(mParameters.size());
        for (final ParameterDef param: mParameters) {
            paramList.addMember(state, param.toTerm(state));
        }
        result.addMember(state, paramList);

        result.addMember(state, mStatements.toTerm(state));

        return result;
    }

    public static ProcedureDefinition termToValue(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList            paramList   = (SetlList) term.firstMember();
            final List<ParameterDef>  parameters  = new ArrayList<ParameterDef>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(v));
            }
            final Block               block       = TermConverter.valueToBlock(term.lastMember());
            return new ProcedureDefinition(parameters, block);
        }
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     */
    @Override
    public int compareTo(final Value v) {
        mObject = null;
        if (this == v) {
            return 0;
        } else if (v instanceof ProcedureDefinition) {
            final ProcedureDefinition other = (ProcedureDefinition) v;
            if (this instanceof PreDefinedFunction && other instanceof PreDefinedFunction) {
                final PreDefinedFunction _this  = (PreDefinedFunction) this;
                final PreDefinedFunction _other = (PreDefinedFunction) other;
                return _this.getName().compareTo(_other.getName());
            } else {
                final int cmp = mParameters.toString().compareTo(other.mParameters.toString());
                if (cmp != 0) {
                    return cmp;
                }
                return mStatements.toString().compareTo(other.mStatements.toString());
            }
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    /* To compare "incomparable" values, e.g. of different types, the following
     * order is established and used in compareTo():
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real
     * < SetlString < SetlSet < SetlList < Term < ProcedureDefinition
     * < SetlObject < ConstructorDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    @Override
    protected int compareToOrdering() {
        mObject = null;
        return 1000;
    }

    @Override
    public boolean equalTo(final Value v) {
        mObject = null;
        if (this == v) {
            return true;
        } else if (v instanceof PreDefinedFunction) {
            if (this instanceof PreDefinedFunction) {
                final PreDefinedFunction _this  = (PreDefinedFunction) this;
                final PreDefinedFunction _other = (PreDefinedFunction) v;
                return _this.getName().equals(_other.getName());
            } else {
                return false;
            }
        } else if (v instanceof ProcedureDefinition) {
            final ProcedureDefinition other = (ProcedureDefinition) v;
            if (mParameters.toString().equals(other.mParameters.toString())) {
                return mStatements.toString().equals(other.mStatements.toString());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private final static int initHashCode = ProcedureDefinition.class.hashCode();

    @Override
    public int hashCode() {
        mObject = null;
        return initHashCode;
    }

    // private subclass to cheat the end of the world... or stack, whatever comes first
    private class CallExecThread extends Thread {
        private final Block                             mStatements;
        private final org.randoom.setlx.utilities.State mState;
        /*package*/   ReturnMessage                     mResult;
        /*package*/   SetlException                     mException;

        public CallExecThread(final Block statements, final org.randoom.setlx.utilities.State state) {
            this.mStatements = statements;
            this.mState      = state;
            this.mResult     = null;
            this.mException  = null;
        }

        @Override
        public void run() {
            try {
                this.mResult    = this.mStatements.exec(this.mState);
                this.mException = null;
            } catch (final SetlException e) {
                this.mResult    = null;
                this.mException = e;
            }
        }
    }

}


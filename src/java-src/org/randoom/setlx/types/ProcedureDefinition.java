package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.ParameterDef;
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
    public  final static String  FUNCTIONAL_CHARACTER = "^procedure";
    // execute this function continuesly in debug mode until it returns. MAY ONLY BE SET BY STATE CLASS!
    public        static boolean sStepThroughFunction = false;
    // continue execution of this function in debug mode until it returns. MAY ONLY BE SET BY STATE CLASS!
    public        static boolean sFinishFunction      = false;

    protected final List<ParameterDef>       mParameters;   // parameter list
    protected final Block                    mStatements;  // statements in the body of the definition
    protected       HashMap<Variable, Value> mClosure;     // variables and values used in closure

    public ProcedureDefinition(final List<ParameterDef> parameters, final Block statements) {
        mParameters = parameters;
        mStatements = statements;
        mClosure    = null;
    }
    protected ProcedureDefinition(final List<ParameterDef> parameters, final Block statements, final HashMap<Variable, Value> closure) {
        mParameters = parameters;
        mStatements = statements;
        if (closure != null) {
            mClosure = new HashMap<Variable, Value>(closure);
        } else {
            mClosure = null;
        }
    }

    // only to be used by ProcedureConstructor
    public ProcedureDefinition createCopy() {
        return new ProcedureDefinition(mParameters, mStatements);
    }

    @Override
    public ProcedureDefinition clone() {
        if (mClosure != null) {
            return new ProcedureDefinition(mParameters, mStatements, mClosure);
        } else {
            return this;
        }
    }

    public void addClosure(final HashMap<Variable, Value> closure) {
        mClosure = closure;
    }

    /* Gather all bound and unbound variables in this value and its siblings
          - bound   means "assigned" in this value
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        /* first collect and optimize the inside */
        final List<Variable> innerBoundVariables   = new ArrayList<Variable>();
        final List<Variable> innerUnboundVariables = new ArrayList<Variable>();
        final List<Variable> innerUsedVariables    = new ArrayList<Variable>();

        // add all parameters to bound
        for (final ParameterDef def : mParameters) {
            def.collectVariablesAndOptimize(innerBoundVariables, innerBoundVariables, innerBoundVariables);
        }

        mStatements.collectVariablesAndOptimize(innerBoundVariables, innerUnboundVariables, innerUsedVariables);

        /* compute variables as seen by the outside */

        // upon defining this procedure, all variables which are unbound inside
        // will be read to create the closure for this procedure
        for (final Variable var : innerUnboundVariables) {
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
        return SetlBoolean.TRUE;
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
        for (final Expr arg : args) {
            values.add(arg.eval(state));
        }

        return callAfterEval(state, args, values);
    }

    protected Value callAfterEval(final State state, final List<Expr> args, final List<Value> values) throws SetlException {
        // save old scope
        final VariableScope oldScope = state.getScope();
        // create new scope used for the function call
        state.setScope(oldScope.cloneFunctions());

        // assign closure contents
        if (mClosure != null) {
            for (final Map.Entry<Variable, Value> entry : mClosure.entrySet()) {
                entry.getKey().assignUnclonedCheckUpTo(state, entry.getValue(), oldScope);
            }
        }

        // put arguments into inner scope
        final int size = values.size();
        for (int i = 0; i < size; ++i) {
            final ParameterDef param = mParameters.get(i);
            if (param.getType() == ParameterDef.READ_WRITE) {
                param.assign(state, values.get(i));
            } else {
                param.assign(state, values.get(i).clone());
            }
        }

        // get rid of value-list to potentionally free some memory
        values.clear();

        // results of call to procedure
              Value           result      = null;
        final WriteBackAgent  wba         = new WriteBackAgent(mParameters.size());
        final boolean         stepThrough = sStepThroughFunction;

        try {
            if (stepThrough) {
                state.setDebugStepThroughFunction(false);
                state.setDebugModeActive(false);
            }

            // execute, e.g. perform real procedure call
            result = mStatements.execute(state);

            // extract 'rw' arguments from environment and store them into WriteBackAgent
            for (int i = 0; i < mParameters.size(); ++i) {
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
                for (final Map.Entry<Variable, Value> entry : mClosure.entrySet()) {
                    entry.setValue(entry.getKey().eval(state));
                }
            }

        } finally { // make sure scope is always reset
            // restore old scope
            state.setScope(oldScope);

            // write values in WriteBackAgent into restored scope
            wba.writeBack(state);

            if (stepThrough || sFinishFunction) {
                state.setDebugModeActive(true);
                if (sFinishFunction) {
                    state.setDebugFinishFunction(false);
                }
            }
        }

        if (result != null) {
            return result;
        } else {
            return Om.OM;
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
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
     * "incomparable" values, e.g. of different types are ranked as follows:
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real < SetlString
     * < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    @Override
    public int compareTo(final Value v){
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
        } else if (v == Infinity.POSITIVE) {
            // only +Infinity is bigger
            return -1;
        } else {
            // everything else is smaller
            return 1;
        }
    }

    @Override
    public boolean equalTo(final Value v) {
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
        return initHashCode;
    }
}


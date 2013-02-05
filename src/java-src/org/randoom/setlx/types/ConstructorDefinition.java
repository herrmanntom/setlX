package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Assignment;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.ValueExpr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.ExpressionStatement;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;
import org.randoom.setlx.utilities.WriteBackAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// This class represents a definition of a constructor for objects

/*
grammar rule:
objectConstructor
    : 'constructor' '(' procedureParameters ')' '{' block ('static' '{' block '}')? '}'
    ;

implemented here as:
                        ===================         =====               =====
                            mParameters             mInit               mStatic
*/

public class ConstructorDefinition extends Value {
    // functional character used in terms
    public  final static String FUNCTIONAL_CHARACTER = "^constructor";

    private final List<ParameterDef> mParameters; // parameter list
    private final Block              mInit;       // statements in the body of the definition
    private       Block              mStatic;     // statements in the static block
    private       VariableScope      mStaticDefs; // definitions from static block

    public ConstructorDefinition(final List<ParameterDef> parameters,
                                 final Block              init,
                                 final Block              staticBlock
    ) {
        this(parameters, init, staticBlock, null);
    }

    private ConstructorDefinition(final List<ParameterDef> parameters,
                                 final Block              init,
                                 final Block              staticBlock,
                                 final VariableScope      staticDefs
    ) {
        mParameters = parameters;
        mInit       = init;
        mStatic     = staticBlock;
        mStaticDefs = staticDefs;
    }

    @Override
    public ConstructorDefinition clone() {
        Block staticBlock = null;
        if (mStatic != null) {
            staticBlock = mStatic.clone();
        }
        VariableScope staticDefs = null;
        if (mStaticDefs != null) {
            staticDefs = mStaticDefs.clone();
        }
        return new ConstructorDefinition(mParameters, mInit, staticBlock, staticDefs);
    }

    /* Gather all bound and unbound variables in this value and its siblings
          - bound   means "assigned" in this value
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use collectVariablesAndOptimize() when adding variables from
             sub-expressions
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        /* collect and optimize the inside */
        final List<Variable> innerBoundVariables   = new ArrayList<Variable>();
        final List<Variable> innerUnboundVariables = new ArrayList<Variable>();
        final List<Variable> innerUsedVariables    = new ArrayList<Variable>();

        // add all parameters to bound
        for (final ParameterDef def : mParameters) {
            def.collectVariablesAndOptimize(innerBoundVariables, innerBoundVariables, innerBoundVariables);
        }

        mInit.collectVariablesAndOptimize(innerBoundVariables, innerUnboundVariables, innerUsedVariables);

        if (mStatic != null) {
            mStatic.collectVariablesAndOptimize(innerBoundVariables, innerUnboundVariables, innerUsedVariables);
        }
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        final int nArguments = args.size();
        if (mParameters.size() != nArguments) {
            throw new IncorrectNumberOfParametersException(
                "'" + this + "' is defined with a different number of parameters " +
                "(" + mParameters.size() + ")."
            );
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<Value>(nArguments);
        for (final Expr arg : args) {
            values.add(arg.eval(state));
        }

        // save old scope
        final VariableScope oldScope    = state.getScope();
        // create new scope used for the static definitions
        final VariableScope newScope    = oldScope.createFunctionsOnlyLinkedScope();
        state.setScope(newScope);

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

        final WriteBackAgent wba         = new WriteBackAgent(mParameters.size());
        final boolean        stepThrough = state.isDebugStepThroughFunction;

        try {
            if (stepThrough) {
                state.setDebugStepThroughFunction(false);
                state.setDebugModeActive(false);
            }

            // execute, e.g. compute member definition
            mInit.exec(state);

            // extract 'rw' arguments from scope, store them into WriteBackAgent
            // and remove all parameters from scope
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
                // remove parameter from scope
                param.assign(state, Om.OM);
            }

            // compute static definition, if not already done
            if (mStaticDefs == null) {
                mStaticDefs = computeStaticDefinitions(state);
            }

            newScope.unlink();
            newScope.pruneOM();
            newScope.linkToOriginalScope(mStaticDefs);

            return SetlObject.createNew(mStaticDefs, newScope);

        } finally { // make sure scope is always reset
            // restore old scope
            state.setScope(oldScope);

            // write values in WriteBackAgent into restored scope
            wba.writeBack(state);

            if (stepThrough || state.isDebugFinishFunction) {
                state.setDebugModeActive(true);
                if (state.isDebugFinishFunction) {
                    state.setDebugFinishFunction(false);
                }
            }
        }
    }

    private VariableScope computeStaticDefinitions(final State state) throws SetlException {
        // save old scope
        final VariableScope oldScope = state.getScope();
        // create new scope used for the static definitions
        final VariableScope newScope = oldScope.createFunctionsOnlyLinkedScope();
        state.setScope(newScope);

        try {
            // execute, e.g. compute static definition
            if (mStatic != null) {
                mStatic.exec(state);
            }

            newScope.unlink();
            newScope.pruneOM();

            return newScope;

        } finally { // make sure scope is always reset
            // restore old scope
            state.setScope(oldScope);
        }
    }

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isConstructor() {
        return SetlBoolean.TRUE;
    }

    /* features of objects */

    @Override
    public Value getObjectMember(final State state, final Variable variable) throws SetlException {
        return getObjectMemberUnCloned(state, variable).clone();
    }

    @Override
    public Value getObjectMemberUnCloned(final State state, final Variable variable) throws SetlException {
        if (mStaticDefs == null) {
            mStaticDefs = computeStaticDefinitions(state);
        }
        final VariableScope oldScope = state.getScope();
        state.setScope(mStaticDefs);
        try {
            return variable.eval(state);
        } finally {
            state.setScope(oldScope);
        }
    }

    @Override
    public void setObjectMember(final State state, final Variable variable, final Value value) {
        if (mStatic == null) {
            mStatic = new Block();
        }
        mStatic.add(new ExpressionStatement(new Assignment(variable, new ValueExpr(value))));
        if (mStaticDefs != null) {
            final VariableScope oldScope = state.getScope();
            state.setScope(mStaticDefs);
            try {
                variable.assignUncloned(state, value);
            } finally {
                state.setScope(oldScope);
            }
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        final String endl = state.getEndl();
        sb.append("constructor(");
        final Iterator<ParameterDef> iter = mParameters.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") {");
        sb.append(endl);
        mInit.appendString(state, sb, tabs + 1, /* brackets = */ false);
        if (mStatic != null) {
            sb.append(endl);
            state.getLineStart(sb, tabs + 1);
            sb.append("static ");
            mStatic.appendString(state, sb, tabs + 1, /* brackets = */ true);
        }
        sb.append(endl);
        state.getLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);

        final SetlList paramList = new SetlList(mParameters.size());
        for (final ParameterDef param: mParameters) {
            paramList.addMember(state, param.toTerm(state));
        }
        result.addMember(state, paramList);

        result.addMember(state, mInit.toTerm(state));
        if (mStatic != null) {
            result.addMember(state, mInit.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }

        return result;
    }

    public static ConstructorDefinition termToValue(final Term term) throws TermConversionException {
        if (term.size() != 3 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final SetlList            paramList   = (SetlList) term.firstMember();
                final List<ParameterDef>  parameters  = new ArrayList<ParameterDef>(paramList.size());
                for (final Value v : paramList) {
                    parameters.add(ParameterDef.valueToParameterDef(v));
                }
                final Block               init        = TermConverter.valueToBlock(term.getMember(2));
                      Block               staticBlock = null;
                if (! term.lastMember().equals(new SetlString("nil"))) {
                    staticBlock    = TermConverter.valueToBlock(term.lastMember());
                }
                return new ConstructorDefinition(parameters, init, staticBlock);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     */
    @Override
    public int compareTo(final Value v){
        if (this == v) {
            return 0;
        } else if (v instanceof ConstructorDefinition) {
            final ConstructorDefinition other = (ConstructorDefinition) v;
            int cmp = mParameters.toString().compareTo(other.mParameters.toString());
            if (cmp != 0) {
                return cmp;
            }
            cmp = mInit.toString().compareTo(other.mInit.toString());
            if (cmp != 0) {
                return cmp;
            }
            if (mStatic != null) {
                if (other.mStatic != null) {
                    return mStatic.toString().compareTo(other.mStatic.toString());
                } else {
                    return 1;
                }
            } else {
                if (other.mStatic != null) {
                    return -1;
                } else {
                    return 0;
                }
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
        return 1200;
    }

    @Override
    public boolean equalTo(final Value v) {
        if (this == v) {
            return true;
        } else if (v instanceof ConstructorDefinition) {
            final ConstructorDefinition other = (ConstructorDefinition) v;
            if (mParameters.toString().equals(other.mParameters.toString())) {
                if (mInit.toString().equals(other.mInit.toString())) {
                    return mStatic.toString().equals(other.mStatic.toString());
                }
            }
        }
        return false;
    }

    private final static int initHashCode = ConstructorDefinition.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode;
    }

}


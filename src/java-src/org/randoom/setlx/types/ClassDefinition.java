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
import org.randoom.setlx.utilities.SetlHashMap;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;
import org.randoom.setlx.utilities.WriteBackAgent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

// This class represents a definition of a constructor for objects

/*
grammar rule:
classDefinition
    : 'class' ID '(' procedureParameters ')' '{' block ('static' '{' block '}')? '}'
    ;

implemented here as:
                     ===================         =====               =====
                         parameters            initBlock           staticBlock
*/

public class ClassDefinition extends Value {
    // functional character used in terms
    public  final static String FUNCTIONAL_CHARACTER = "^constructor";

    private final List<ParameterDef> parameters;  // parameter list
    private final Block              initBlock;   // statements in the body of the definition
    private       HashSet<String>    initVars;    // member variables defined in the body
    private       Block              staticBlock; // statements in the static block
    private       HashSet<String>    staticVars;  // variables defined in the static block
    private       SetlHashMap<Value> staticDefs;  // definitions from static block

    public ClassDefinition(final List<ParameterDef> parameters,
                           final Block              init,
                           final Block              staticBlock
    ) {
        this(parameters, init, null, staticBlock, null, null);
    }

    private ClassDefinition(final List<ParameterDef> parameters,
                            final Block              init,
                            final HashSet<String>    initVars,
                            final Block              staticBlock,
                            final HashSet<String>    staticVars,
                            final SetlHashMap<Value> staticDefs
    ) {
        this.parameters  = parameters;
        this.initBlock   = init;
        this.initVars    = initVars;
        this.staticBlock = staticBlock;
        this.staticVars  = staticVars;
        this.staticDefs  = staticDefs;
    }

    @Override
    public ClassDefinition clone() {
        HashSet<String> initVars = null;
        if (this.initVars != null) {
            initVars = new HashSet<String>(this.initVars);
        }
        Block staticBlock = null;
        if (this.staticBlock != null) {
            staticBlock = this.staticBlock.clone();
        }
        HashSet<String> staticVars = null;
        if (this.staticVars != null) {
            staticVars = new HashSet<String>(this.staticVars);
        }
        SetlHashMap<Value> staticDefs = null;
        if (this.staticDefs != null) {
            staticDefs = new SetlHashMap<Value>();
            for (final Entry<String, Value> entry: this.staticDefs.entrySet()) {
                staticDefs.put(entry.getKey(), entry.getValue().clone());
            }
        }
        return new ClassDefinition(parameters, initBlock, initVars, staticBlock, staticVars, staticDefs);
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
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        /* collect and optimize the inside */
        final List<String> innerBoundVariables   = new ArrayList<String>();
        final List<String> innerUnboundVariables = new ArrayList<String>();
        final List<String> innerUsedVariables    = new ArrayList<String>();

        // add all parameters to bound
        for (final ParameterDef def : parameters) {
            def.collectVariablesAndOptimize(innerBoundVariables, innerBoundVariables, innerBoundVariables);
        }

        int preBound = innerBoundVariables.size();
        initBlock.collectVariablesAndOptimize(innerBoundVariables, innerUnboundVariables, innerUsedVariables);
        final HashSet<String> initVars = new HashSet<String>(innerBoundVariables.subList(preBound, innerBoundVariables.size()));

        preBound = innerBoundVariables.size();
        if (staticBlock != null) {
            staticBlock.collectVariablesAndOptimize(innerBoundVariables, innerUnboundVariables, innerUsedVariables);
        }
        final HashSet<String> staticVars = new HashSet<String>(innerBoundVariables.subList(preBound, innerBoundVariables.size()));

        this.initVars   = initVars;
        this.staticVars = staticVars;
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        if (staticVars == null) {
            optimize();
        }

        // compute static definition, if not already done
        if (staticDefs == null) {
            staticDefs = computeStaticDefinitions(state);
        }

        final int nArguments = args.size();
        if (parameters.size() != nArguments) {
            throw new IncorrectNumberOfParametersException(
                "'" + this + "' is defined with "+ parameters.size()+" instead of " +
                nArguments + " parameters."
            );
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<Value>(nArguments);
        for (final Expr arg : args) {
            values.add(arg.eval(state));
        }

        // save old scope
        final VariableScope oldScope    = state.getScope();
        // create new scope used for the member definitions
        final VariableScope newScope    = oldScope.createFunctionsOnlyLinkedScope().createLinkedScope();
        state.setScope(newScope);

        // put arguments into inner scope
        final int size = values.size();
        for (int i = 0; i < size; ++i) {
            final ParameterDef param = parameters.get(i);
            if (param.getType() == ParameterDef.READ_WRITE) {
                param.assign(state, values.get(i));
            } else {
                param.assign(state, values.get(i).clone());
            }
        }

        final SetlHashMap<Value> members     = new SetlHashMap<Value>();
        final SetlObject         newObject   = SetlObject.createNew(members, this);

        newScope.linkToThisObject(newObject);

        final WriteBackAgent     wba         = new WriteBackAgent(parameters.size());
        final boolean            stepThrough = state.isDebugStepThroughFunction;

        if (stepThrough) {
            state.setDebugStepThroughFunction(false);
            state.setDebugModeActive(false);
        }

        try {

            // execute, e.g. compute member definition
            initBlock.exec(state);

            // extract 'rw' arguments from scope, store them into WriteBackAgent
            for (int i = 0; i < parameters.size(); ++i) {
                final ParameterDef param = parameters.get(i);
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

            newScope.unlink();

            members.putAll(extractBindings(state, initVars));

            return newObject;

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

    private SetlHashMap<Value> computeStaticDefinitions(final State state) throws SetlException {
        if (staticVars == null) {
            optimize();
        }

        // save old scope
        final VariableScope oldScope = state.getScope();
        // create new scope used for the static definitions
        final VariableScope newScope = oldScope.createFunctionsOnlyLinkedScope().createLinkedScope();
        state.setScope(newScope);

        try {
            // execute, e.g. compute static definition
            if (staticBlock != null) {
                staticBlock.exec(state);
            }

            newScope.unlink();

            return extractBindings(state, staticVars);

        } finally { // make sure scope is always reset
            // restore old scope
            state.setScope(oldScope);
        }
    }

    private SetlHashMap<Value> extractBindings(final State state, final HashSet<String> vars) throws SetlException {
        final SetlHashMap<Value> bindings = new SetlHashMap<Value>();

        for (final String var : vars) {
            final Value value = state.findValue(var);
            if (value instanceof ProcedureDefinition) {
                ((ProcedureDefinition) value).setClosure(null);
            }
            if (value != Om.OM) {
                bindings.put(var, value.clone());
            }
        }
        return bindings;
    }

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isConstructor() {
        return SetlBoolean.TRUE;
    }

    /* features of objects */

    @Override
    public Value getObjectMember(final State state, final String variable) throws SetlException {
        return getObjectMemberUnCloned(state, variable).clone();
    }

    @Override
    public Value getObjectMemberUnCloned(final State state, final String variable) throws SetlException {
        if (staticDefs == null) {
            staticDefs = computeStaticDefinitions(state);
        }
        final Value result = staticDefs.get(variable);
        if (result != null) {
            return result;
        } else {
            return Om.OM;
        }
    }

    @Override
    public void setObjectMember(final State state, final String variable, final Value value) throws SetlException {
        if (staticVars == null) {
            optimize();
        }

        if (value instanceof ProcedureDefinition) {
            ((ProcedureDefinition) value).setClosure(null);
        }

        if (staticDefs == null) {
            staticDefs = computeStaticDefinitions(state);
        }

        staticDefs.put(variable, value);

        staticVars.add(variable);

        // rebuild static block
        final Block sBlock = new Block();
        for (final Entry<String, Value> entry : staticDefs.entrySet()) {
            sBlock.add(new ExpressionStatement(new Assignment(new Variable(entry.getKey()), new ValueExpr(entry.getValue()))));
        }
        staticBlock = sBlock;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        appendString(null, state, sb, tabs);
    }

    public void appendString(final String className, final State state, final StringBuilder sb, final int tabs) {
        final String endl = state.getEndl();
        sb.append("class");
        if (className != null) {
            sb.append(" ");
            sb.append(className);
        }
        sb.append(" (");
        final Iterator<ParameterDef> iter = parameters.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") {");
        sb.append(endl);
        initBlock.appendString(state, sb, tabs + 1, /* brackets = */ false);
        if (staticBlock != null) {
            sb.append(endl);
            state.getLineStart(sb, tabs + 1);
            sb.append("static ");
            staticBlock.appendString(state, sb, tabs + 1, /* brackets = */ true);
        }
        sb.append(endl);
        state.getLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);

        final SetlList paramList = new SetlList(parameters.size());
        for (final ParameterDef param: parameters) {
            paramList.addMember(state, param.toTerm(state));
        }
        result.addMember(state, paramList);

        result.addMember(state, initBlock.toTerm(state));
        if (staticBlock != null) {
            result.addMember(state, initBlock.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }

        return result;
    }

    public static ClassDefinition termToValue(final Term term) throws TermConversionException {
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
                return new ClassDefinition(parameters, init, staticBlock);
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
        } else if (v instanceof ClassDefinition) {
            final ClassDefinition other = (ClassDefinition) v;
            int cmp = parameters.toString().compareTo(other.parameters.toString());
            if (cmp != 0) {
                return cmp;
            }
            cmp = initBlock.toString().compareTo(other.initBlock.toString());
            if (cmp != 0) {
                return cmp;
            }
            if (staticBlock != null) {
                if (other.staticBlock != null) {
                    return staticBlock.toString().compareTo(other.staticBlock.toString());
                } else {
                    return 1;
                }
            } else {
                if (other.staticBlock != null) {
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
        } else if (v instanceof ClassDefinition) {
            final ClassDefinition other = (ClassDefinition) v;
            if (parameters.toString().equals(other.parameters.toString())) {
                if (initBlock.toString().equals(other.initBlock.toString())) {
                    return staticBlock.toString().equals(other.staticBlock.toString());
                }
            }
        }
        return false;
    }

    private final static int initHashCode = ClassDefinition.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode;
    }

    public void collectBindings(final Map<String, Value> result, final boolean restrictToFunctions) {
        for (final Map.Entry<String, Value> entry : staticDefs.entrySet()) {
            final Value val = entry.getValue();
            if ( ! restrictToFunctions || val instanceof ProcedureDefinition) {
                result.put(entry.getKey(), val);
            }
        }
    }

}


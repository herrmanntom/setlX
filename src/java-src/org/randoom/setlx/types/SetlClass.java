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

/**
 * This class represents a definition of a constructor for objects.
 *
 * grammar rule:
 * classDefinition
 *     : 'class' ID '(' procedureParameters ')' '{' block ('static' '{' block '}')? '}'
 *     ;
 *
 * implemented here as:
 *                      ===================         =====               =====
 *                          parameters            initBlock           staticBlock
 */
public class SetlClass extends Value {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(SetlClass.class);

    private final static Block  REBUILD_MARKER       = new Block(0);

    private final List<ParameterDef> parameters;  // parameter list
    private final Block              initBlock;   // statements in the body of the definition
    private       HashSet<String>    initVars;    // member variables defined in the body
    private       Block              staticBlock; // statements in the static block
    private       HashSet<String>    staticVars;  // variables defined in the static block
    private       SetlHashMap<Value> staticDefs;  // definitions from static block

    /**
     * Create a new instance of this data type.
     *
     * @param parameters  Parameters of the "constructor" to execute when creating objects.
     * @param init        Statements to execute when creating objects.
     * @param staticBlock Statements to execute once.
     */
    public SetlClass(final List<ParameterDef> parameters,
                     final Block              init,
                     final Block              staticBlock
    ) {
        this(parameters, init, null, staticBlock, null, null);
    }

    private SetlClass(final List<ParameterDef> parameters,
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

    private Block getStaticBlock() {
        if (staticBlock == REBUILD_MARKER) {
            // rebuild static block
            final Block sBlock = new Block();
            for (final Entry<String, Value> entry : staticDefs.entrySet()) {
                sBlock.add(new ExpressionStatement(new Assignment(new Variable(entry.getKey()), new ValueExpr(entry.getValue()))));
            }
            staticBlock = sBlock;
        }
        return staticBlock;
    }

    /**
     * Gather all static bindings set in this class.
     *
     * @param result              Map to append static bindings to.
     * @param restrictToFunctions Only collect bindings of functions.
     */
    public void collectBindings(final Map<String, Value> result, final boolean restrictToFunctions) {
        for (final Map.Entry<String, Value> entry : staticDefs.entrySet()) {
            final Value val = entry.getValue();
            if ( ! restrictToFunctions || val instanceof Procedure) {
                result.put(entry.getKey(), val);
            }
        }
    }

    @Override
    public SetlClass clone() {
        HashSet<String> initVars = null;
        if (this.initVars != null) {
            initVars = new HashSet<String>(this.initVars);
        }
        Block staticBlock = null;
        if (getStaticBlock() != null) {
            staticBlock = getStaticBlock().clone();
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
        return new SetlClass(parameters, initBlock, initVars, staticBlock, staticVars, staticDefs);
    }

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
        if (getStaticBlock() != null) {
            getStaticBlock().collectVariablesAndOptimize(innerBoundVariables, innerUnboundVariables, innerUsedVariables);
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
                param.assign(state, values.get(i), FUNCTIONAL_CHARACTER);
            } else {
                param.assign(state, values.get(i).clone(), FUNCTIONAL_CHARACTER);
            }
        }

        final SetlHashMap<Value> members     = new SetlHashMap<Value>();
        final SetlObject         newObject   = SetlObject.createNew(members, this);

        newScope.linkToThisObject(newObject);

        final WriteBackAgent     wba         = new WriteBackAgent(parameters.size());

        try {

            // execute, e.g. compute member definition
            initBlock.execute(state);

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
            wba.writeBack(state, FUNCTIONAL_CHARACTER);
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
            if (getStaticBlock() != null) {
                getStaticBlock().execute(state);
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
            if (value instanceof Procedure) {
                ((Procedure) value).setClosure(null);
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
    public void setObjectMember(final State state, final String variable, final Value value, final String context) throws SetlException {
        if (staticVars == null) {
            optimize();
        }

        if (value instanceof Procedure) {
            ((Procedure) value).setClosure(null);
        }

        if (staticDefs == null) {
            staticDefs = computeStaticDefinitions(state);
        }

        staticDefs.put(variable, value);
        if (state.traceAssignments) {
            state.printTrace(variable, value, FUNCTIONAL_CHARACTER);
        }

        staticVars.add(variable);

        // mark static block to be rebuild on access
        staticBlock = REBUILD_MARKER;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        appendString(null, state, sb, tabs);
    }

    /**
     * Appends a string representation of this class to the given StringBuilder
     * object.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString(State)
     *
     * @param className Name bound to this class, or null.
     * @param state     Current state of the running setlX program.
     * @param sb        StringBuilder to append to.
     * @param tabs      Number of tabs to use as indentation for statements.
     */
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
        if (getStaticBlock() != null) {
            sb.append(endl);
            state.appendLineStart(sb, tabs + 1);
            sb.append("static ");
            getStaticBlock().appendString(state, sb, tabs + 1, /* brackets = */ true);
        }
        sb.append(endl);
        state.appendLineStart(sb, tabs);
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
        if (getStaticBlock() != null) {
            result.addMember(state, staticBlock.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }

        return result;
    }

    /**
     * Convert a term representing a SetlClass into such a value.
     *
     * @param term                     Term to convert.
     * @return                         Resulting SetlClass.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static SetlClass termToValue(final Term term) throws TermConversionException {
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
                return new SetlClass(parameters, init, staticBlock);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v){
        if (this == v) {
            return 0;
        } else if (v instanceof SetlClass) {
            final SetlClass other = (SetlClass) v;
            int cmp = parameters.toString().compareTo(other.parameters.toString());
            if (cmp != 0) {
                return cmp;
            }
            cmp = initBlock.toString().compareTo(other.initBlock.toString());
            if (cmp != 0) {
                return cmp;
            }
            if (getStaticBlock() != null) {
                if (other.getStaticBlock() != null) {
                    return getStaticBlock().toString().compareTo(other.getStaticBlock().toString());
                } else {
                    return 1;
                }
            } else {
                if (other.getStaticBlock() != null) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    @Override
    protected int compareToOrdering() {
        return 1200;
    }

    @Override
    public boolean equalTo(final Value v) {
        if (this == v) {
            return true;
        } else if (v instanceof SetlClass) {
            final SetlClass other = (SetlClass) v;
            if (parameters.toString().equals(other.parameters.toString())) {
                if (initBlock.toString().equals(other.initBlock.toString())) {
                    return getStaticBlock().toString().equals(other.getStaticBlock().toString());
                }
            }
        }
        return false;
    }

    private final static int initHashCode = SetlClass.class.hashCode();

    @Override
    public int hashCode() {
        int hash = initHashCode + parameters.size();
        if (initVars != null) {
            hash = hash * 31 + initVars.hashCode();
        }
        if (staticVars != null) {
            hash = hash * 31 + staticVars.hashCode();
        }
        if (staticDefs != null) {
            hash = hash * 31 + staticDefs.hashCode();
        }
        return hash;
    }

    /**
     * Get the functional character of this value type used in terms.
     *
     * @return Functional character of this value type.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


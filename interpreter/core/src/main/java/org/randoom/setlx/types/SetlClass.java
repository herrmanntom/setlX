package org.randoom.setlx.types;

import org.randoom.setlx.assignments.AssignableVariable;
import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operators.AOperator;
import org.randoom.setlx.operators.Assignment;
import org.randoom.setlx.operators.ValueOperator;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.ExpressionStatement;
import org.randoom.setlx.statements.Return;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.parameters.ParameterList;
import org.randoom.setlx.utilities.SetlHashMap;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;
import org.randoom.setlx.utilities.VariableScope;
import org.randoom.setlx.utilities.WriteBackAgent;

import java.util.ArrayList;
import java.util.HashSet;
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
    private final static String FUNCTIONAL_CHARACTER      = TermUtilities.generateFunctionalCharacter(SetlClass.class);
    private final static long   COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetlClass.class);
    private final static Block  REBUILD_MARKER            = new Block(new Return(new OperatorExpression(new ValueOperator(new SetlString(FUNCTIONAL_CHARACTER + COMPARE_TO_ORDER_CONSTANT)))));

    private final ParameterList      parameters;          // parameter list
    private final Block              initBlock;           // statements in the body of the definition
    private       HashSet<String>    initVars;            // member variables defined in the body
    private       Block              staticBlock;         // statements in the static block
    private       HashSet<String>    staticVars;          // variables defined in the static block
    private       SetlHashMap<Value> staticDefs;          // definitions from static block

    /**
     * Create a new instance of this data type.
     *
     * @param parameters  Parameters of the "constructor" to execute when creating objects.
     * @param init        Statements to execute when creating objects.
     * @param staticBlock Statements to execute once.
     */
    public SetlClass(final ParameterList parameters,
                     final Block         init,
                     final Block         staticBlock
    ) {
        this(parameters, init, null, staticBlock, null, null);
    }

    private SetlClass(final ParameterList      parameters,
                      final Block              init,
                      final HashSet<String>    initVars,
                      final Block              staticBlock,
                      final HashSet<String>    staticVars,
                      final SetlHashMap<Value> staticDefs
    ) {
        this.parameters          = parameters;
        this.initBlock           = init;
        this.initVars            = initVars;
        this.staticBlock         = staticBlock;
        this.staticVars          = staticVars;
        this.staticDefs          = staticDefs;
    }

    private Block getStaticBlock() {
        if (staticBlock == REBUILD_MARKER) {
            // rebuild static block
            final FragmentList<Statement> sBlock = new FragmentList<>(staticDefs.size());
            for (final Entry<String, Value> entry : staticDefs.entrySet()) {
                FragmentList<AOperator> assignment = new FragmentList<>();
                assignment.add(new ValueOperator(entry.getValue()));
                assignment.add(new Assignment(new AssignableVariable(entry.getKey())));
                sBlock.add(new ExpressionStatement(new OperatorExpression(assignment)));
            }
            staticBlock = new Block(sBlock);
        }
        return staticBlock;
    }

    /**
     * Gather all static bindings set in this class.
     *
     * @param result              Map to append static bindings to.
     * @param restrictToFunctions Only collect bindings of functions.
     */
    public void collectBindings(final SetlHashMap<Value> result, final boolean restrictToFunctions) {
        for (final Map.Entry<String, Value> entry : staticDefs.entrySet()) {
            final Value val = entry.getValue();
            if ( ! restrictToFunctions || val.isProcedure() == SetlBoolean.TRUE) {
                result.put(entry.getKey(), val);
            }
        }
    }

    @Override
    public SetlClass clone() {
        HashSet<String> initVars = null;
        if (this.initVars != null) {
            initVars = new HashSet<>(this.initVars);
        }
        Block staticBlock = null;
        if (getStaticBlock() != null) {
            staticBlock = REBUILD_MARKER;
        }
        HashSet<String> staticVars = null;
        if (this.staticVars != null) {
            staticVars = new HashSet<>(this.staticVars);
        }
        SetlHashMap<Value> staticDefs = null;
        if (this.staticDefs != null) {
            staticDefs = new SetlHashMap<>();
            for (final Entry<String, Value> entry: this.staticDefs.entrySet()) {
                staticDefs.put(entry.getKey(), entry.getValue().clone());
            }
        }
        return new SetlClass(parameters, initBlock, initVars, staticBlock, staticVars, staticDefs);
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        /* collect and optimize the inside */
        final List<String> innerBoundVariables   = new ArrayList<>();
        final List<String> innerUnboundVariables = new ArrayList<>();
        final List<String> innerUsedVariables    = new ArrayList<>();

        // add all parameters to bound
        parameters.collectVariablesAndOptimize(state, innerBoundVariables, innerBoundVariables, innerBoundVariables);

        int preBound = innerBoundVariables.size();
        initBlock.collectVariablesAndOptimize(state, innerBoundVariables, innerUnboundVariables, innerUsedVariables);
        final HashSet<String> initVars = new HashSet<>(innerBoundVariables.subList(preBound, innerBoundVariables.size()));

        preBound = innerBoundVariables.size();
        if (getStaticBlock() != null) {
            getStaticBlock().collectVariablesAndOptimize(state, innerBoundVariables, innerUnboundVariables, innerUsedVariables);
        }
        final HashSet<String> staticVars = new HashSet<>(innerBoundVariables.subList(preBound, innerBoundVariables.size()));

        this.initVars   = initVars;
        this.staticVars = staticVars;

        return false;
    }

    /* function call */

    @Override
    public Value call(final State state, List<Value> argumentValues, final FragmentList<OperatorExpression> arguments, final Value listValue, final OperatorExpression listArg) throws SetlException {
        if (staticVars == null) {
            optimize(state);
        }

        // compute static definition, if not already done
        if (staticDefs == null) {
            staticDefs = computeStaticDefinitions(state);
        }

        SetlList listArguments = null;
        if (listValue != null) {
            if (listValue.getClass() != SetlList.class) {
                StringBuilder error = new StringBuilder();
                error.append("List argument '");
                listValue.appendString(state, error, 0);
                error.append("' is not a list.");
                throw new UndefinedOperationException(error.toString());
            }
            listArguments = (SetlList) listValue;
        }

        int nArguments = argumentValues.size();
        if (listArguments != null) {
            nArguments += listArguments.size();
        }
        if (! parameters.isAssignableWithThisManyActualArguments(nArguments)) {
            final StringBuilder error = new StringBuilder();
            error.append("'");
            appendString(state, error, 0);
            error.append("'");
            parameters.appendIncorrectNumberOfParametersErrorMessage(error, nArguments);
            throw new IncorrectNumberOfParametersException(error.toString());
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<>(nArguments);
        values.addAll(argumentValues);
        if (listArguments != null) {
            for (Value listArgument : listArguments) {
                values.add(listArgument);
            }
        }

        // save old scope
        final VariableScope oldScope = state.getScope();
        // create new scope used for the member definitions
        final VariableScope newScope = oldScope.createFunctionsOnlyLinkedScope().createLinkedScope();
        state.setScope(newScope);

        // put arguments into inner scope
        final boolean            rwParameters = parameters.putParameterValuesIntoScope(state, values, FUNCTIONAL_CHARACTER);

              WriteBackAgent     wba          = null;
        final SetlHashMap<Value> members      = new SetlHashMap<>();
        final SetlObject         newObject    = SetlObject.createNew(members, this);

        newScope.linkToThisObject(newObject);

        try {

            // execute, e.g. compute member definition
            initBlock.execute(state);

            // extract 'rw' arguments from scope, store them into WriteBackAgent
            if (rwParameters) {
                wba = parameters.extractRwParametersFromScope(state, arguments);
            }

            members.putAll(extractBindings(state, initVars));

            return newObject;

        } finally { // make sure scope is always reset
            // restore old scope
            state.setScope(oldScope);

            // write values in WriteBackAgent into restored scope
            if (wba != null) {
                wba.writeBack(state, FUNCTIONAL_CHARACTER);
            }
        }
    }

    private SetlHashMap<Value> computeStaticDefinitions(final State state) throws SetlException {
        if (staticVars == null) {
            optimize(state);
        }

        // save old scope
        final VariableScope oldScope = state.getScope();
        // create new scope used for the static definitions
        final VariableScope newScope = oldScope.createFunctionsOnlyLinkedScope();
        state.setScope(newScope);

        try {
            // execute, e.g. compute static definition
            if (getStaticBlock() != null) {
                getStaticBlock().execute(state);
            }

            return extractBindings(state, staticVars);

        } finally { // make sure scope is always reset
            // restore old scope
            state.setScope(oldScope);
        }
    }

    private SetlHashMap<Value> extractBindings(final State state, final HashSet<String> vars) throws SetlException {
        final SetlHashMap<Value> bindings = new SetlHashMap<>();

        for (final String var : vars) {
            final Value value = state.findValue(var);
            if (value instanceof Closure) {
                ((Closure) value).setClosure(null);
            }
            if (value != Om.OM) {
                bindings.put(var, value.clone());
            }
        }
        return bindings;
    }

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isClass() {
        return SetlBoolean.TRUE;
    }

    /* features of objects */

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
            optimize(state);
        }

        if (value instanceof Closure) {
            ((Closure) value).setClosure(null);
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
        appendString(state, null, sb, tabs);
    }

    /**
     * Appends a string representation of this class to the given StringBuilder
     * object.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString(State)
     *
     * @param state     Current state of the running setlX program.
     * @param className Name bound to this class, or null.
     * @param sb        StringBuilder to append to.
     * @param tabs      Number of tabs to use as indentation for statements.
     */
    public void appendString(final State state, final String className, final StringBuilder sb, final int tabs) {
        final String endl = state.getEndl();
        sb.append("class");
        if (className != null) {
            sb.append(" ");
            sb.append(className);
        }
        sb.append(" (");
        parameters.appendString(state, sb);
        sb.append(") {");
        sb.append(endl);
        if (initBlock.size() > 0 || (getStaticBlock() != null && getStaticBlock().size() > 0)) {
            initBlock.appendString(state, sb, tabs + 1, /* brackets = */ false);
            if (getStaticBlock() != null) {
                sb.append(endl);
                state.appendLineStart(sb, tabs + 1);
                sb.append("static ");
                getStaticBlock().appendString(state, sb, tabs + 1, /* brackets = */ true);
            }
            sb.append(endl);
        }
        state.appendLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);

        result.addMember(state, parameters.toTerm(state));

        result.addMember(state, initBlock.toTerm(state));
        if (getStaticBlock() != null) {
            result.addMember(state, staticBlock.toTerm(state));
        } else {
            result.addMember(state, SetlString.NIL);
        }

        return result;
    }

    /**
     * Convert a term representing a SetlClass into such a value.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting SetlClass.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static SetlClass termToValue(final State state, final Term term) throws TermConversionException {
        if (term.size() != 3) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final ParameterList parameters  = ParameterList.termFragmentToParameterList(state, term.firstMember());
                final Block         init        = TermUtilities.valueToBlock(state, term.getMember(2));
                      Block         staticBlock = null;
                if (! term.lastMember().equals(SetlString.NIL)) {
                    staticBlock = TermUtilities.valueToBlock(state, term.lastMember());
                }
                return new SetlClass(parameters, init, staticBlock);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER, se);
            }
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other){
        if (this == other) {
            return 0;
        } else if (other.getClass() == SetlClass.class) {
            final SetlClass setlClass = (SetlClass) other;
            int cmp = parameters.compareTo(setlClass.parameters);
            if (cmp != 0) {
                return cmp;
            }
            cmp = initBlock.compareTo(setlClass.initBlock);
            if (cmp != 0) {
                return cmp;
            }
            if (getStaticBlock() != null) {
                if (setlClass.getStaticBlock() != null) {
                    return getStaticBlock().compareTo(setlClass.getStaticBlock());
                } else {
                    return 1;
                }
            } else {
                if (setlClass.getStaticBlock() != null) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        if (this == other) {
            return true;
        } else if (other.getClass() == SetlClass.class) {
            final SetlClass otherClass = (SetlClass) other;
            if (parameters.equals(otherClass.parameters)) {
                if (initBlock.equalTo(otherClass.initBlock)) {
                    if (getStaticBlock() != null) {
                        if (otherClass.getStaticBlock() != null) {
                            return getStaticBlock().equalTo(otherClass.getStaticBlock());
                        }
                    } if (otherClass.getStaticBlock() == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + parameters.hashCode();
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


package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Assignment;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.ValueExpr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.ExpressionStatement;
import org.randoom.setlx.utilities.*;

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
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(SetlClass.class);

    private final static Block  REBUILD_MARKER       = new Block(null, 0);

    private final ParameterList      parameters;          // parameter list
    private final Block              initBlock;           // statements in the body of the definition
    private       HashSet<String>    initVars;            // member variables defined in the body
    private       Block              staticBlock;         // statements in the static block
    private       HashSet<String>    staticVars;          // variables defined in the static block
    private       SetlHashMap<Value> staticDefs;          // definitions from static block

    private       State              state;               // state used during rebuild of staticBlock;

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
            final Block sBlock = new Block(state);
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
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        /* collect and optimize the inside */
        final List<String> innerBoundVariables   = new ArrayList<String>();
        final List<String> innerUnboundVariables = new ArrayList<String>();
        final List<String> innerUsedVariables    = new ArrayList<String>();

        // add all parameters to bound
        parameters.collectVariablesAndOptimize(state, innerBoundVariables, innerBoundVariables, innerBoundVariables);

        int preBound = innerBoundVariables.size();
        initBlock.collectVariablesAndOptimize(state, innerBoundVariables, innerUnboundVariables, innerUsedVariables);
        final HashSet<String> initVars = new HashSet<String>(innerBoundVariables.subList(preBound, innerBoundVariables.size()));

        preBound = innerBoundVariables.size();
        if (getStaticBlock() != null) {
            getStaticBlock().collectVariablesAndOptimize(state, innerBoundVariables, innerUnboundVariables, innerUsedVariables);
        }
        final HashSet<String> staticVars = new HashSet<String>(innerBoundVariables.subList(preBound, innerBoundVariables.size()));

        this.initVars   = initVars;
        this.staticVars = staticVars;
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args, final Expr listArg) throws SetlException {
        if (staticVars == null) {
            optimize(state);
        }

        // compute static definition, if not already done
        if (staticDefs == null) {
            staticDefs = computeStaticDefinitions(state);
        }

        SetlList listArguments = null;
        if (listArg != null) {
            Value listArgument = listArg.eval(state);
            if (listArgument.getClass() != SetlList.class) {
                throw new UndefinedOperationException("List argument '" + listArg.toString(state) + "' is not a list.");
            }
            listArguments = (SetlList) listArgument;
        }

        int nArguments = args.size();
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
        final ArrayList<Value> values = new ArrayList<Value>(nArguments);
        for (final Expr arg : args) {
            values.add(arg.eval(state));
        }
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
        final SetlHashMap<Value> members      = new SetlHashMap<Value>();
        final SetlObject         newObject    = SetlObject.createNew(members, this);

        newScope.linkToThisObject(newObject);

        try {

            // execute, e.g. compute member definition
            initBlock.execute(state);

            // extract 'rw' arguments from scope, store them into WriteBackAgent
            if (rwParameters) {
                wba = parameters.extractRwParametersFromScope(state, args);
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
        final SetlHashMap<Value> bindings = new SetlHashMap<Value>();

        for (final String var : vars) {
            final Value value = state.findValue(var);
            if (value.getClass() == Closure.class) {
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
            optimize(state);
        }

        if (value.getClass() == Closure.class) {
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
        this.state  = state;
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
        parameters.appendString(state, sb, 0);
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
                final Block         init        = TermConverter.valueToBlock(state, term.getMember(2));
                      Block         staticBlock = null;
                if (! term.lastMember().equals(SetlString.NIL)) {
                    staticBlock = TermConverter.valueToBlock(state, term.lastMember());
                }
                return new SetlClass(parameters, init, staticBlock);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final Value other){
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

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetlClass.class);

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


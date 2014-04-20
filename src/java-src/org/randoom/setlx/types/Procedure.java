package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.functions.PreDefinedProcedure;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.ParameterDef.ParameterType;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.SetlHashMap;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;
import org.randoom.setlx.utilities.WriteBackAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class represents a function definition.
 *
 * grammar rule:
 * procedure
 *     : 'procedure' '(' procedureParameters ')' '{' block '}'
 *     ;
 *
 * implemented here as:
 *                       ===================         =====
 *                            parameters           statements
 */
public class Procedure extends Value {
    // functional character used in terms
    private   final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Procedure.class);

    /**
     * List of parameters.
     */
    protected final List<ParameterDef> parameters;
    /**
     * Statements in the body of the procedure.
     */
    protected final Block              statements;
    /**
     * Variables and values used in closure.
     */
    protected       SetlHashMap<Value> closure;
    /**
     * Surrounding object for next call.
     */
    protected       SetlObject         object;

    /**
     * Create new procedure definition.
     *
     * @param parameters List of parameters.
     * @param statements Statements in the body of the procedure.
     */
    public Procedure(final List<ParameterDef> parameters, final Block statements) {
        this(parameters, statements, null);
    }

    /**
     * Create new procedure definition, which replicates the complete internal
     * state of another procedure.
     *
     * @param parameters procedure parameters
     * @param statements statements in the body of the procedure
     * @param closure    Attached closure variables.
     */
    protected Procedure(final List<ParameterDef> parameters, final Block statements, final SetlHashMap<Value> closure) {
        this.parameters = parameters;
        this.statements = statements;
        if (closure != null) {
            this.closure = new SetlHashMap<Value>(closure);
        } else {
            this.closure = null;
        }
        this.object = null;
    }

    /**
     * Create a separate instance of this procedure.
     *
     * Note: Only to be used by ProcedureConstructor.
     *
     * @return Copy of this procedure definition.
     */
    public Procedure createCopy() {
        return new Procedure(parameters, statements);
    }

    @Override
    public Procedure clone() {
        if (closure != null || object != null) {
            return new Procedure(parameters, statements, closure);
        } else {
            return this;
        }
    }

    /**
     * Attach closure variables and their values.
     *
     * @param closure Closure variables to attach.
     */
    public void setClosure(final SetlHashMap<Value> closure) {
        this.closure = closure;
    }

    /**
     * Attach surrounding object, when this procedure is part of an object/class
     * definition.
     *
     * @param object Object to attach.
     */
    public void addSurroundingObject(final SetlObject object) {
        this.object = object;
    }

    @Override
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        /* first collect and optimize the inside */
        final List<String> innerBoundVariables   = new ArrayList<String>();
        final List<String> innerUnboundVariables = new ArrayList<String>();
        final List<String> innerUsedVariables    = new ArrayList<String>();

        // add all parameters to bound
        for (final ParameterDef def : parameters) {
            def.collectVariablesAndOptimize(state, innerBoundVariables, innerBoundVariables, innerBoundVariables);
        }

        statements.collectVariablesAndOptimize(state, innerBoundVariables, innerUnboundVariables, innerUsedVariables);

        /* compute variables as seen by the outside */

        // upon defining this procedure, all variables which are unbound inside
        // will be read to create the closure for this procedure
        for (final String var : innerUnboundVariables) {
            if (var == Variable.getPreventOptimizationDummy()) {
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
        object = null;
        return SetlBoolean.TRUE;
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        try {
            // increase callStackDepth
            ++(state.callStackDepth);

            final int        size   = args.size();
            final SetlObject object = this.object;
            this.object = null;

            if (parameters.size() != size) {
                final StringBuilder error = new StringBuilder();
                error.append("'");
                appendStringWithoutStatements(state, error);
                error.append("' is defined with ");
                error.append(parameters.size());
                error.append(" instead of ");
                error.append(size);
                error.append(" parameters.");
                throw new IncorrectNumberOfParametersException(error.toString());
            }

            // evaluate arguments
            final ArrayList<Value> values = new ArrayList<Value>(size);
            for (final Expr arg : args) {
                values.add(arg.eval(state));
            }

            final Value result = callAfterEval(state, args, values, object);

            return result;

        } catch (final StackOverflowError soe) {
            state.storeStackDepthOfFirstCall(state.callStackDepth);
            throw soe;
        } finally {
            // decrease callStackDepth
            --(state.callStackDepth);
        }
    }

    /**
     * Perform the actual function call using the statement in this definition,
     * after all parameters where evaluated.
     *
     * @param state          Current state of the running setlX program.
     * @param args           Expressions used to set the procedures parameters.
     * @param values         Results of the evaluated parameters.
     * @param object         Surrounding object for this call.
     * @return               Return value of this function call.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    protected final Value callAfterEval(final State state, final List<Expr> args, final List<Value> values, final SetlObject object) throws SetlException {
        // increase callStackDepth
        ++(state.callStackDepth);

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
        if (closure != null) {
            for (final Map.Entry<String, Value> entry : closure.entrySet()) {
                new Variable(entry.getKey()).assignUnclonedCheckUpTo(state, entry.getValue(), null, FUNCTIONAL_CHARACTER);
            }
        }

        // put arguments into inner scope
        final int parametersSize = parameters.size();
        for (int i = 0; i < parametersSize; ++i) {
            final ParameterDef param = parameters.get(i);
            final Value        value = values.get(i);
            if (param.getType() == ParameterType.READ_WRITE) {
                param.assign(state, value, FUNCTIONAL_CHARACTER);
            } else {
                param.assign(state, value.clone(), FUNCTIONAL_CHARACTER);
            }
        }

        // get rid of value-list to potentially free some memory
        values.clear();

        // results of call to procedure
              ReturnMessage   result = null;
        final WriteBackAgent  wba    = new WriteBackAgent(parameters.size());

        try {

            // execute, e.g. perform actual procedure call
            result = statements.execute(state);

            // extract 'rw' arguments from environment and store them into WriteBackAgent
            for (int i = 0; i < parametersSize; ++i) {
                // skip first parameter of object-bound call (i.e. `this')
                if (object != null && i == 0) {
                    continue;
                }
                final ParameterDef param = parameters.get(i);
                if (param.getType() == ParameterType.READ_WRITE) {
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
            if (closure != null) {
                for (final Map.Entry<String, Value> entry : closure.entrySet()) {
                    entry.setValue(state.findValue(entry.getKey()));
                }
            }

        } catch (final StackOverflowError soe) {
            state.storeStackDepthOfFirstCall(state.callStackDepth);
            throw soe;
        } finally { // make sure scope is always reset
            // restore old scope
            state.setScope(oldScope);

            // write values in WriteBackAgent into restored scope
            wba.writeBack(state, FUNCTIONAL_CHARACTER);

            // decrease callStackDepth
            --(state.callStackDepth);
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
        appendStringWithoutStatements(state, sb);
        sb.append(" ");
        statements.appendString(state, sb, tabs, /* brackets = */ true);
    }

    /**
     * Appends a string representation of this Procedure to the given
     * StringBuilder object, but does not append the statements inside the procedure.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString(State)
     *
     * @param state Current state of the running setlX program.
     * @param sb    StringBuilder to append to.
     */
    protected void appendStringWithoutStatements(final State state, final StringBuilder sb) {
        object = null;
        sb.append("procedure(");
        final Iterator<ParameterDef> iter = parameters.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        object = null;
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList paramList = new SetlList(parameters.size());
        for (final ParameterDef param: parameters) {
            paramList.addMember(state, param.toTerm(state));
        }
        result.addMember(state, paramList);

        result.addMember(state, statements.toTerm(state));

        return result;
    }

    /**
     * Convert a term representing a Procedure into such a procedure.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting Procedure.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Procedure termToValue(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList           paramList  = (SetlList) term.firstMember();
            final List<ParameterDef> parameters = new ArrayList<ParameterDef>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(state, v));
            }
            final Block              block      = TermConverter.valueToBlock(state, term.lastMember());
            return new Procedure(parameters, block);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        object = null;
        if (this == v) {
            return 0;
        } else if (v instanceof Procedure) {
            final Procedure other = (Procedure) v;
            int cmp = Integer.compare(parameters.size(), other.parameters.size());
            if (cmp != 0) {
                return cmp;
            }
            for (int index = 0; index < parameters.size(); ++index) {
                cmp = parameters.get(index).compareTo(other.parameters.get(index));
                if (cmp != 0) {
                    return cmp;
                }
            }
            return statements.compareTo(other.statements);
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    @Override
    public int compareToOrdering() {
        object = null;
        return 1000;
    }

    @Override
    public boolean equalTo(final Object v) {
        object = null;
        if (this == v) {
            return true;
        } else if (v instanceof Procedure && ! (v instanceof PreDefinedProcedure)) {
            final Procedure other = (Procedure) v;
            if (parameters.size() == other.parameters.size()) {
                for (int index = 0; index < parameters.size(); ++index) {
                    if ( ! parameters.get(index).equalTo(other.parameters.get(index))) {
                        return false;
                    }
                }
                return statements.equalTo(other.statements);
            }
        }
        return false;
    }

    private final static int initHashCode = Procedure.class.hashCode();

    @Override
    public int hashCode() {
        object = null;
        return (initHashCode + parameters.size()) * 31 + statements.size();
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}


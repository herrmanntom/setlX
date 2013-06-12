package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.functions.PreDefinedProcedure;
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
    public  final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Procedure.class);

    protected final List<ParameterDef>     parameters;      // parameter list
    protected final Block                  statements;      // statements in the body of the definition
    protected       HashMap<String, Value> closure;         // variables and values used in closure
    protected       SetlObject             object;          // surrounding object for next call

    public Procedure(final List<ParameterDef> parameters, final Block statements) {
        this(parameters, statements, null);
    }

    protected Procedure(final List<ParameterDef> parameters, final Block statements, final HashMap<String, Value> closure) {
        this.parameters = parameters;
        this.statements = statements;
        if (closure != null) {
            this.closure = new HashMap<String, Value>(closure);
        } else {
            this.closure = null;
        }
        this.object = null;
    }

    // only to be used by ProcedureConstructor
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

    public void setClosure(final HashMap<String, Value> closure) {
        this.closure = closure;
    }

    public void addSurroundingObject(final SetlObject object) {
        this.object = object;
    }

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
        for (final ParameterDef def : parameters) {
            def.collectVariablesAndOptimize(innerBoundVariables, innerBoundVariables, innerBoundVariables);
        }

        statements.collectVariablesAndOptimize(innerBoundVariables, innerUnboundVariables, innerUsedVariables);

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
        object = null;
        return SetlBoolean.TRUE;
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        final int        size   = args.size();
        final SetlObject object = this.object;
        this.object = null;

        if (parameters.size() != size) {
            throw new IncorrectNumberOfParametersException(
                "'" + this + "' is defined with "+ parameters.size()+" instead of " +
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

    protected Value callAfterEval(final State state, final List<Expr> args, final List<Value> values, final SetlObject object) throws SetlException {
        // increase callStackDepth
        state.callStackDepth += 2;

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
                new Variable(entry.getKey()).assignUnclonedCheckUpTo(state, entry.getValue(), oldScope, FUNCTIONAL_CHARACTER);
            }
        }

        // put arguments into inner scope
        final int parametersSize = parameters.size();
        for (int i = 0; i < parametersSize; ++i) {
            final ParameterDef param = parameters.get(i);
            final Value        value = values.get(i);
            if (param.getType() == ParameterDef.READ_WRITE) {
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

            // execute, e.g. perform real procedure call
            result = statements.execute(state);

            // extract 'rw' arguments from environment and store them into WriteBackAgent
            for (int i = 0; i < parametersSize; ++i) {
                // skip first parameter of object-bound call (i.e. `this')
                if (object != null && i == 0) {
                    continue;
                }
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

            // read closure variables and update their current state
            if (closure != null) {
                for (final Map.Entry<String, Value> entry : closure.entrySet()) {
                    entry.setValue(state.findValue(entry.getKey()));
                }
            }

        } catch (final StackOverflowError soe) {
            state.storeFirstCallStackDepth();
            throw soe;
        } finally { // make sure scope is always reset
            // restore old scope
            state.setScope(oldScope);

            newScope.unlink();

            // write values in WriteBackAgent into restored scope
            wba.writeBack(state, FUNCTIONAL_CHARACTER);

            // decrease callStackDepth
            state.callStackDepth -= 2;
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
        object = null;
        sb.append("procedure(");
        final Iterator<ParameterDef> iter = parameters.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") ");
        statements.appendString(state, sb, tabs, /* brackets = */ true);
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

    public static Procedure termToValue(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList            paramList   = (SetlList) term.firstMember();
            final List<ParameterDef>  parameters  = new ArrayList<ParameterDef>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(v));
            }
            final Block               block       = TermConverter.valueToBlock(term.lastMember());
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
            if (this instanceof PreDefinedProcedure && other instanceof PreDefinedProcedure) {
                final PreDefinedProcedure _this  = (PreDefinedProcedure) this;
                final PreDefinedProcedure _other = (PreDefinedProcedure) other;
                return _this.getName().compareTo(_other.getName());
            } else {
                final int cmp = parameters.toString().compareTo(other.parameters.toString());
                if (cmp != 0) {
                    return cmp;
                }
                return statements.toString().compareTo(other.statements.toString());
            }
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    @Override
    protected int compareToOrdering() {
        object = null;
        return 1000;
    }

    @Override
    public boolean equalTo(final Value v) {
        object = null;
        if (this == v) {
            return true;
        } else if (v instanceof PreDefinedProcedure) {
            if (this instanceof PreDefinedProcedure) {
                final PreDefinedProcedure _this  = (PreDefinedProcedure) this;
                final PreDefinedProcedure _other = (PreDefinedProcedure) v;
                return _this.getName().equals(_other.getName());
            } else {
                return false;
            }
        } else if (v instanceof Procedure) {
            final Procedure other = (Procedure) v;
            if (parameters.toString().equals(other.parameters.toString())) {
                return statements.toString().equals(other.statements.toString());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private final static int initHashCode = Procedure.class.hashCode();

    @Override
    public int hashCode() {
        object = null;
        return initHashCode + parameters.size();
    }
}


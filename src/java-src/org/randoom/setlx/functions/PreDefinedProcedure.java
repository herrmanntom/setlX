package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.Procedure;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.ParameterDef.ParameterType;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.WriteBackAgent;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all procedures, which can be loaded at runtime by setlX.
 */
public abstract class PreDefinedProcedure extends Procedure {
    // functional characters used in terms
    private final static String  FUNCTIONAL_CHARACTER = generateFunctionalCharacter(PreDefinedProcedure.class);

    private String  name;
    private int     nameHashCode;
    private boolean unlimitedParameters;
    private int     minimumNumberOfParameters;

    /**
     * Initialize a new predefined procedure.
     *
     * Note: This class is abstract - no object will be created using this
     *       constructor directly.
     */
    protected PreDefinedProcedure() {
        super(new ArrayList<ParameterDef>(), new Block(null));
        this.name                      = null;
        this.nameHashCode              = -1;
        this.unlimitedParameters       = false;
        this.minimumNumberOfParameters = 0;
    }

    /**
     * Get the name of this procedure, as shown in the interpreter.
     *
     * @return Name of this procedure.
     */
    public final String getName() {
        if (name == null) {
            name         = this.getClass().getSimpleName().substring(3);
            nameHashCode = name.hashCode();
        }
        return name;
    }

    /**
     * Set the name of this procedure.
     *
     * Only to be used by MathFunction.java & MathFunction2.java !
     * Name of `normal' PreDefinedProcedures is determined automatically for the class name.
     *
     * @param name Name to set.
     */
    protected final void setName(final String name) {
        this.name = name;
    }

    /**
     * Add parameters to this definition.
     *
     * @param param Parameter name to add.
     */
    protected final void addParameter(final String param) {
        addParameter(param, ParameterType.READ_ONLY);
    }

    /**
     * Add parameters to this definition.
     * See ParameterDef for type constants.
     *
     * @see org.randoom.setlx.utilities.ParameterDef
     *
     * @param param Parameter name to add.
     * @param type  Type of the parameter (RW, RO).
     */
    protected final void addParameter(final String param, final ParameterType type) {
        parameters.add(new ParameterDef(param, type));
        ++minimumNumberOfParameters;
    }

    /**
     * Allow call with unlimited number of parameters.
     */
    protected final void enableUnlimitedParameters() {
        unlimitedParameters = true;
    }

    /**
     * Allow call with more than this number of parameters, up to a maximum of the number of parameters.
     *
     * @param minimumNumberOfParameters Minimum parameters that must be supplied to the call.
     */
    protected final void setMinimumNumberOfParameters(final int minimumNumberOfParameters) {
        if (minimumNumberOfParameters > parameters.size() || minimumNumberOfParameters < 0) {
            throw new InvalidParameterException("minimumNumberOfParameters in invalid");
        }
        this.minimumNumberOfParameters = minimumNumberOfParameters;
    }

    /**
     * Function to be implemented by specific predefined procedures.
     *
     * @param state          Current state of the running setlX program.
     * @param args           Values of the call-parameters in the same order as defined.
     * @param writeBackVars  List to append Values for RW parameters in the same order as defined to.
     * @return               Resulting value of the call.
     * @throws SetlException Can be thrown in case of some (user-) error.
     */
    protected abstract Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException;

    // this function is called from within SetlX
    @Override
    public final Value call(final State state, final List<Expr> args) throws SetlException {
        try {
            // increase callStackDepth
            state.callStackDepth += 2; // this method + the overloaded execute()
                                       // after that all bets are off

            final int paramSize = parameters.size();
            final int argsSize  = args.size();
            if (argsSize > paramSize && ! unlimitedParameters) {
                final StringBuilder error = new StringBuilder();
                error.append("'");
                error.append(getName());
                appendParameters(state, error);
                error.append("' is defined with ");
                if (minimumNumberOfParameters != paramSize) {
                    error.append("between ");
                    error.append(minimumNumberOfParameters);
                    error.append(" and ");
                }
                error.append(paramSize);
                error.append(" instead of ");
                error.append(argsSize);
                error.append(" parameters.");
                throw new IncorrectNumberOfParametersException(error.toString());
            } else if (argsSize < minimumNumberOfParameters) {
                final StringBuilder error = new StringBuilder();
                error.append("'");
                error.append(getName());
                appendParameters(state, error);
                error.append("' is defined with ");
                if (minimumNumberOfParameters != paramSize) {
                    error.append("at least ");
                }
                error.append(minimumNumberOfParameters);
                error.append(" instead of ");
                error.append(argsSize);
                error.append(" parameters.");
                throw new IncorrectNumberOfParametersException(error.toString());
            }

            // evaluate arguments
            final ArrayList<Value> values = new ArrayList<Value>(argsSize);
            for (final Expr arg : args) {
                values.add(arg.eval(state).clone());
            }

            // List of writeBack-values, which should be stored into the outer scope
            final ArrayList<Value> writeBackVars = new ArrayList<Value>(paramSize);

            // call predefined function (which may add writeBack-values to List)
            final Value result  = this.execute(state, values, writeBackVars);

            // extract 'rw' arguments from writeBackVars list and store them into WriteBackAgent
            if (writeBackVars.size() > 0) {
                final WriteBackAgent wba = new WriteBackAgent(writeBackVars.size());
                for (int i = 0; i < paramSize; ++i) {
                    final ParameterDef param = parameters.get(i);
                    if (param.getType() == ParameterType.READ_WRITE && writeBackVars.size() > 0) {
                        // value of parameter after execution
                        final Value postValue = writeBackVars.remove(0);
                        // expression used to fill parameter before execution
                        final Expr  preExpr   = args.get(i);
                        /* if possible the WriteBackAgent will set the variable used in
                           this expression to its postExecution state in the outer scope */
                        wba.add(preExpr, postValue);
                    }
                }
                // assign variables
                wba.writeBack(state, FUNCTIONAL_CHARACTER);
            }

            return result;

        } catch (final StackOverflowError soe) {
            state.storeStackDepthOfFirstCall(state.callStackDepth);
            throw soe;
        } finally {
            // decrease callStackDepth
            state.callStackDepth -= 2;
        }
    }

    /* string and char operations */

    @Override
    public final void appendString(final State state, final StringBuilder sb, final int tabs) {
        final String endl = state.getEndl();
        sb.append("procedure");
        appendParameters(state, sb);
        sb.append(" {");
        sb.append(endl);
        state.appendLineStart(sb, tabs + 1);
        sb.append("/* predefined procedure `");
        sb.append(getName());
        sb.append("' */");
        sb.append(endl);
        state.appendLineStart(sb, tabs);
        sb.append("}");
    }

    private void appendParameters(final State state, final StringBuilder sb) {
        sb.append("(");
        final int     numberOfParameters = parameters.size();
        final boolean optionalParameters = minimumNumberOfParameters < numberOfParameters;
        for (int index = 0; index < numberOfParameters; ++index) {
            if (optionalParameters && minimumNumberOfParameters == 0) {
                sb.append("[");
            }
            parameters.get(index).appendString(state, sb, 0);
            if (optionalParameters && index == (minimumNumberOfParameters - 1)) {
                sb.append(" [");
            }
            if (index < (numberOfParameters - 1)) {
                sb.append(", ");
            }
        }
        if (optionalParameters) {
            sb.append("]");
        }
        if (unlimitedParameters) {
            if (parameters.size() > 0) {
                sb.append(", ");
            }
            sb.append("...");
        }
        sb.append(")");
    }

    /* term operations */

    @Override
    public final Value toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER);

        result.addMember(state, new SetlString(getName()));

        return result;
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        object = null;
        if (this == v) {
            return 0;
        } else if (v instanceof PreDefinedProcedure) {
            return getName().compareTo(((PreDefinedProcedure) v).getName());
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    @Override
    public int compareToOrdering() {
        object = null;
        return 1400;
    }

    @Override
    public boolean equalTo(final Object v) {
        if (this == v) {
            return true;
        } else if (v instanceof PreDefinedProcedure) {
            return getName().equals(((PreDefinedProcedure) v).getName());
        }
        return false;
    }

    private final static int initHashCode = PreDefinedProcedure.class.hashCode();

    @Override
    public int hashCode() {
        if (nameHashCode == -1) {
            nameHashCode = getName().hashCode();
        }
        return initHashCode + nameHashCode;
    }
}


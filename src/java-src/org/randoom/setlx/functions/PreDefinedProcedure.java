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
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.WriteBackAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class PreDefinedProcedure extends Procedure {
    // functional characters used in terms
    private final static String  FUNCTIONAL_CHARACTER = generateFunctionalCharacter(PreDefinedProcedure.class);

    private       String  name;
    private       boolean unlimitedParameters;
    private       boolean allowFewerParameters;

    protected PreDefinedProcedure() {
        super(new ArrayList<ParameterDef>(), new Block());
        this.name                 = null;
        this.unlimitedParameters  = false;
        this.allowFewerParameters = false;
    }

    public final String getName() {
        if (name == null) {
            name = this.getClass().getSimpleName().substring(3);
        }
        return name;
    }

    // only to be used by MathFunction.java & MathFunction2.java !
    protected final void setName(final String name) {
        this.name = name;
    }

    // add parameters to own definition
    protected void addParameter(final String param) {
        parameters.add(new ParameterDef(param, ParameterDef.READ_ONLY));
    }
    protected void addParameter(final String param, final int type) {
        parameters.add(new ParameterDef(param, type));
    }

    // allow an unlimited number of parameters
    protected void enableUnlimitedParameters() {
        unlimitedParameters    = true;
    }

    // allow an calling with fewer number of parameters then specified
    protected void allowFewerParameters() {
        allowFewerParameters   = true;
    }

    // this function is to be implemented by all predefined functions
    protected abstract Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException;

    // this function is called from within SetlX
    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        final int paramSize = parameters.size();
        final int argsSize  = args.size();
        if (paramSize < argsSize) {
            if (unlimitedParameters) {
                // unlimited means: at least the number of defined parameters or more
                // no error
            } else {
                String error = "Procedure is defined with fewer parameters ";
                error +=       "(" + paramSize;
                if (allowFewerParameters) {
                    error +=   " or less";
                }
                error +=       ").";
                throw new IncorrectNumberOfParametersException(error);
            }
        } else if (paramSize > argsSize) {
            if (allowFewerParameters) {
                // fewer parameters are allowed
                // no error
            } else {
                String error = "Procedure is defined with more parameters ";
                error +=       "(" + paramSize;
                if (unlimitedParameters) {
                    error +=   " or more";
                }
                error +=       ").";
                throw new IncorrectNumberOfParametersException(error);
            }
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<Value>(argsSize);
        for (final Expr arg : args) {
            values.add(arg.eval(state).clone());
        }

        // List of writeBack-values, which should be stored into the outer scope
        final ArrayList<Value> writeBackVars = new ArrayList<Value>(paramSize);

        if (state.isDebugStepThroughFunction) {
            state.setDebugStepThroughFunction(false);
        }

        // call predefined function (which may add writeBack-values to List)
        final Value result  = this.execute(state, values, writeBackVars);

        // extract 'rw' arguments from writeBackVars list and store them into WriteBackAgent
        if (writeBackVars.size() > 0) {
            final WriteBackAgent wba = new WriteBackAgent(writeBackVars.size());
            for (int i = 0; i < paramSize; ++i) {
                final ParameterDef param = parameters.get(i);
                if (param.getType() == ParameterDef.READ_WRITE && writeBackVars.size() > 0) {
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
            wba.writeBack(state);
        }

        return result;
    }

    /* string and char operations */

    @Override
    public final void appendString(final State state, final StringBuilder sb, final int tabs) {
        final String endl = state.getEndl();
        sb.append("procedure(");
        final Iterator<ParameterDef> iter = parameters.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        if (unlimitedParameters) {
            if (parameters.size() > 0) {
                sb.append(", ");
            }
            sb.append("...");
        }
        sb.append(") {");
        sb.append(endl);
        state.getLineStart(sb, tabs + 1);
        sb.append("/* predefined procedure `");
        sb.append(getName());
        sb.append("' */");
        sb.append(endl);
        state.getLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER);

        result.addMember(state, new SetlString(getName()));

        return result;
    }
}


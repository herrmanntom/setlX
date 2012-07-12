package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.ProcedureDefinition;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.WriteBackAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class PreDefinedFunction extends ProcedureDefinition {
    // functional characters used in terms
    private final static String  FUNCTIONAL_CHARACTER = "^preDefinedProcedure";
    // continue execution of this function in debug mode until it returns. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean sStepThroughFunction = false;

    private final String  mName;
    private       boolean mUnlimitedParameters;
    private       boolean mAllowFewerParameters;

    protected PreDefinedFunction(final String name) {
        super(new ArrayList<ParameterDef>(), new Block());
        mName                   = name;
        mUnlimitedParameters    = false;
        mAllowFewerParameters   = false;
    }

    public final String getName() {
        return mName;
    }

    // add parameters to own definition
    protected void addParameter(final String param) {
        mParameters.add(new ParameterDef(param, ParameterDef.READ_ONLY));
    }
    protected void addParameter(final String param, final int type) {
        mParameters.add(new ParameterDef(param, type));
    }

    // allow an unlimited number of parameters
    protected void enableUnlimitedParameters() {
        mUnlimitedParameters    = true;
    }

    // allow an calling with fewer number of parameters then specified
    protected void allowFewerParameters() {
        mAllowFewerParameters   = true;
    }

    // this function is to be implemented by all predefined functions
    protected abstract Value execute(final List<Value> args, final List<Value> writeBackVars) throws SetlException;

    // this function is called from within SetlX
    public Value call(final List<Expr> args) throws SetlException {
        final int paramSize = mParameters.size();
        final int argsSize  = args.size();
        if (paramSize < argsSize) {
            if (mUnlimitedParameters) {
                // unlimited means: at least the number of defined parameters or more
                // no error
            } else {
                String error = "Procedure is defined with a fewer number of parameters ";
                error +=       "(" + paramSize;
                if (mAllowFewerParameters) {
                    error +=   " or less";
                }
                error +=       ").";
                throw new IncorrectNumberOfParametersException(error);
            }
        } else if (paramSize > argsSize) {
            if (mAllowFewerParameters) {
                // fewer parameters are allowed
                // no error
            } else {
                String error = "Procedure is defined with a larger number of parameters ";
                error +=       "(" + paramSize;
                if (mUnlimitedParameters) {
                    error +=   " or more";
                }
                error +=       ").";
                throw new IncorrectNumberOfParametersException(error);
            }
        }

        // evaluate arguments
        final ArrayList<Value> values = new ArrayList<Value>(argsSize);
        for (final Expr arg : args) {
            values.add(arg.eval().clone());
        }

        // List of writeBack-values, which should be stored into the outer scope
        final ArrayList<Value> writeBackVars = new ArrayList<Value>(paramSize);

        if (sStepThroughFunction) {
            Environment.setDebugStepThroughFunction(false);
        }

        // call predefined function (which may add writeBack-values to List)
        final Value result  = this.execute(values, writeBackVars);

        // extract 'rw' arguments from writeBackVars list and store them into WriteBackAgent
        if (writeBackVars.size() > 0) {
            final WriteBackAgent wba = new WriteBackAgent(writeBackVars.size());
            for (int i = 0; i < paramSize; ++i) {
                final ParameterDef param = mParameters.get(i);
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
            wba.writeBack();
        }

        return result;
    }

    /* string and char operations */

    public final void appendString(final StringBuilder sb, final int tabs) {
        String endl = Environment.getEndl();
        sb.append("procedure (");
        final Iterator<ParameterDef> iter = mParameters.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(sb);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        if (mUnlimitedParameters) {
            if (mParameters.size() > 0) {
                sb.append(", ");
            }
            sb.append("...");
        }
        sb.append(") {");
        sb.append(endl);
        Environment.getLineStart(sb, tabs + 1);
        sb.append("/* predefined procedure `");
        sb.append(mName);
        sb.append("' */");
        sb.append(endl);
        Environment.getLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    public Value toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);

        result.addMember(new SetlString(mName));

        return result;
    }
}


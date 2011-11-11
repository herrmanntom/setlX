package interpreter.functions;

import interpreter.exceptions.IncorrectNumberOfParametersException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.statements.Block;
import interpreter.types.ProcedureDefinition;
import interpreter.types.RangeDummy;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.ParameterDef;
import interpreter.utilities.WriteBackAgent;

import java.util.LinkedList;
import java.util.List;

public abstract class PreDefinedFunction extends ProcedureDefinition {

    private String  mName;
    private boolean mUnlimitedParameters;
    private boolean mAllowFewerParameters;

    protected PreDefinedFunction(String name) {
        super(new LinkedList<ParameterDef>(), new Block());
        mName                 = name;
        mUnlimitedParameters  = false;
        mAllowFewerParameters = false;
    }

    public final String getName() {
        return mName;
    }

    // add parameters to own definition
    protected void addParameter(String param) {
        mParameters.add(new ParameterDef(param, ParameterDef.READ_ONLY));
    }
    protected void addParameter(String param, int type) {
        mParameters.add(new ParameterDef(param, type));
    }

    // allow an unlimited number of parameters
    protected void enableUnlimitedParameters() {
        mUnlimitedParameters = true;
    }

    // allow an calling with fewer number of parameters then specified
    protected void allowFewerParameters() {
        mAllowFewerParameters = true;
    }

    // this call is to be implemented by all predefined functions
    public abstract Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException;

    public Value call(List<Expr> exprs, List<Value> args) throws SetlException {
        if (args.contains(RangeDummy.RD)) {
            throw new IncorrectNumberOfParametersException("Functions can not be called with ranges as parameters.");
        } else if (mParameters.size() < args.size()) {
            if (mUnlimitedParameters) {
                // unlimited means: at least the number of defined parameters or more
                // no error
            } else {
                String error = "Procedure is defined with a fewer number of parameters ";
                error +=       "(" + mParameters.size();
                if (mAllowFewerParameters) {
                    error +=   " or less";
                }
                error +=       ").";
                throw new IncorrectNumberOfParametersException(error);
            }
        } else if (mParameters.size() > args.size()) {
            if (mAllowFewerParameters) {
                // fewer parameters are allowed
                // no error
            } else {
                String error = "Procedure is defined with a larger number of parameters ";
                error +=       "(" + mParameters.size();
                if (mUnlimitedParameters) {
                    error +=   " or more";
                }
                error +=       ").";
                throw new IncorrectNumberOfParametersException(error);
            }
        }

        // save old environment
        Environment oldEnv = Environment.getEnv();
        // create new environment used for the function call (not really used)
        Environment.setEnv(oldEnv.cloneFunctions());

        // List of writeBack-values, which should be stored into the outer environment
        LinkedList<Value> writeBackVars = new LinkedList<Value>();

        // call predefined function (which may add writeBack-values to List)
        Value             result        = this.execute(args, writeBackVars);

        // extract 'rw' arguments from writeBackVars list and store them into WriteBackAgent
        WriteBackAgent    wba           = new WriteBackAgent();
        for (int i = 0; i < mParameters.size(); ++i) {
            ParameterDef param = mParameters.get(i);
            if (param.getType() == ParameterDef.READ_WRITE && writeBackVars.size() > 0) {
                // value of parameter after execution
                Value postValue = writeBackVars.removeFirst();
                // expression used to fill parameter before execution
                Expr  preExpr   = exprs.get(i);
                /* if possible the WriteBackAgent will set the variable used in this
                   expression to its postExecution state in the outer environment    */
                wba.add(preExpr, postValue);
            }
        }

        // restore old environment
        Environment.setEnv(oldEnv);

        // write values in WriteBackAgent into restored environment
        wba.writeBack();

        return result;
    }

    public final String toString(int tabs) {
        String endl = Environment.getEndl();
        String result = "procedure (";
        for (int i = 0; i < mParameters.size(); ++i) {
            if (i > 0) {
                result += ", ";
            }
            result += mParameters.get(i);
        }
        if (mUnlimitedParameters) {
            if (mParameters.size() > 0) {
                result += ", ";
            }
            result += "...";
        }
        result += ") {" + endl;
        result += Environment.getTabs(tabs + 1) + "/* predefined procedure `" + mName + "' */" + endl;
        result += Environment.getTabs(tabs) + "}";
        return result;
    }

}


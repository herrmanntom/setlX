package interpreter.types;

import interpreter.exceptions.IncorrectNumberOfParametersException;
import interpreter.exceptions.ReturnException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.functions.PreDefinedFunction;
import interpreter.statements.Block;
import interpreter.utilities.Environment;
import interpreter.utilities.ParameterDef;
import interpreter.utilities.WriteBackAgent;

import java.util.List;

// This class represents a function definition
public class SetlDefinition extends Value {
    protected List<ParameterDef> mParameters;  // parameter list
    protected Block              mStatements;  // statements in the body of the definition

    public SetlDefinition(List<ParameterDef> parameters, Block statements) {
        mParameters = parameters;
        mStatements = statements;
    }

    public SetlDefinition clone() {
        // this value can not be changed once set => no harm in returning the original
        return this;
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isProcedure() {
        return SetlBoolean.TRUE;
    }

    /* calls (function calls) */

    public Value call(List<Expr> exprs, List<Value> args) throws SetlException {
        if (mParameters.size() != args.size()) {
            throw new IncorrectNumberOfParametersException("'" + this + "' is defined with a different number of parameters "
                                                          +"(" + mParameters.size() + ").");
        }

        // save old environment
        Environment oldEnv = Environment.getEnv();
        // create new environment used for the function call
        Environment.setEnv(oldEnv.cloneFunctions());

        // put arguments into environment
        for (int i = 0; i < mParameters.size(); ++i) {
            mParameters.get(i).assign(args.get(i));
        }

        Value result = SetlOm.OM;
        try {
            mStatements.execute();
        } catch (ReturnException re) {
            result = re.getValue();
        }

        // extract 'rw' arguments from environment and store them into WriteBackAgent
        WriteBackAgent wba = new WriteBackAgent();
        for (int i = 0; i < mParameters.size(); ++i) {
            ParameterDef param = mParameters.get(i);
            if (param.getType() == ParameterDef.READ_WRITE) {
                // value of parameter after execution
                Value postValue = param.getValue();
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

    /* String and Char operations */

    public String toString(int tabs) {
        String result = "procedure (";
        for (int i = 0; i < mParameters.size(); ++i) {
            if (i > 0) {
                result += ", ";
            }
            result += mParameters.get(i);
        }
        result += ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }

    public String toString() {
        return toString(0);
    }

    /* Comparisons */

    // Compare two Values.  Returns -1 if this value is less than the value given
    // as argument, +1 if its greater and 0 if both values contain the same
    // elements.
    // Useful output is only possible if both values are of the same type.
    // "incomparable" values, e.g. of different types are ranked as follows:
    // SetlOm < SetlBoolean < SetlInt & SetlReal < SetlString < SetlSet < SetlList < SetlDefinition
    // This ranking is necessary to allow sets and lists of different types.
    public int compareTo(Value v){
        if (this == v) { // from using clone()
            return 0;
        } else if (v instanceof SetlDefinition) {
            SetlDefinition other = (SetlDefinition) v;
            if (this instanceof PreDefinedFunction && other instanceof PreDefinedFunction) {
                PreDefinedFunction _this  = (PreDefinedFunction) this;
                PreDefinedFunction _other = (PreDefinedFunction) other;
                return _this.getName().compareTo(_other.getName());
            } else {
                int cmp = mParameters.toString().compareTo(other.mParameters.toString());
                if (cmp != 0) {
                    return cmp;
                }
                return cmp = mStatements.toString().compareTo(other.mStatements.toString());
            }
        } else {
            // everything else is smaller
            return 1;
        }
    }
}


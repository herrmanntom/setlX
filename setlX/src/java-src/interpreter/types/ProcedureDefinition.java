package interpreter.types;

import interpreter.exceptions.IncorrectNumberOfParametersException;
import interpreter.exceptions.ReturnException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.functions.PreDefinedFunction;
import interpreter.statements.Block;
import interpreter.utilities.Environment;
import interpreter.utilities.ParameterDef;
import interpreter.utilities.VariableScope;
import interpreter.utilities.WriteBackAgent;

import java.util.List;

// This class represents a function definition
public class ProcedureDefinition extends Value {
    protected List<ParameterDef> mParameters;  // parameter list
    protected Block              mStatements;  // statements in the body of the definition

    public ProcedureDefinition(List<ParameterDef> parameters, Block statements) {
        mParameters = parameters;
        mStatements = statements;
    }

    public ProcedureDefinition clone() {
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
        } else if (args.contains(RangeDummy.RD)) {
            throw new IncorrectNumberOfParametersException("Functions can not be called with ranges as parameters.");
        }

        // save old scope
        VariableScope oldScope = VariableScope.getScope();
        // create new scope used for the function call
        VariableScope.setScope(oldScope.cloneFunctions());

        // put arguments into inner scope
        for (int i = 0; i < mParameters.size(); ++i) {
            mParameters.get(i).assign(args.get(i));
        }

        Value result = Om.OM;
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

        // restore old scope
        VariableScope.setScope(oldScope);

        // write values in WriteBackAgent into restored scope
        wba.writeBack();

        return result;
    }

    /* String and Char operations */

    public String toString(int tabs) {
        boolean interprete  = Environment.isInterpreteStrings();
        Environment.setInterpreteStrings(false);

        String result = "procedure (";
        for (int i = 0; i < mParameters.size(); ++i) {
            if (i > 0) {
                result += ", ";
            }
            result += mParameters.get(i);
        }
        result += ") ";
        result += mStatements.toString(tabs, true);

        Environment.setInterpreteStrings(interprete);
        return result;
    }

    public String toString() {
        return toString(0);
    }

    /* term operations */

    public Value toTerm() {
        Term result = new Term("'procedure");

        SetlList paramList = new SetlList();
        for (ParameterDef param: mParameters) {
            paramList.addMember(param.toTerm());
        }
        result.addMember(paramList);

        result.addMember(mStatements.toTerm());

        return result;
    }

    /* Comparisons */

    /* Compare two Values.  Returns -1 if this value is less than the value given
     * as argument, +1 if its greater and 0 if both values contain the same
     * elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * Om < -Infinity < SetlBoolean < SetlInt & Real < SetlString < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public int compareTo(Value v){
        if (this == v) { // from using clone()
            return 0;
        } else if (v instanceof ProcedureDefinition) {
            ProcedureDefinition other = (ProcedureDefinition) v;
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
        } else if (v == Infinity.POSITIVE) {
            // only +Infinity is bigger
            return -1;
        } else {
            // everything else is smaller
            return 1;
        }
    }
}


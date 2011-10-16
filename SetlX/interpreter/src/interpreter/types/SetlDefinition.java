package interpreter.types;

import interpreter.exceptions.IncorrectNumberOfParametersException;
import interpreter.exceptions.ReturnException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.functions.PreDefinedFunction;
import interpreter.statements.Statement;
import interpreter.utilities.Environment;

import java.util.List;

// This class represents a function definition
public class SetlDefinition extends Value {
    private String               mName;        // initial function name
    private List<String>         mParameters;  // parameter list
    private List<Statement>      mStatements;  // statements in the body of the definition
    private List<SetlDefinition> mDefinitions; // definitions in the body of the definition

    public SetlDefinition(String name, List<String> parameters, List<Statement> statements, List<SetlDefinition> definitions) {
        mName        = name;
        mParameters  = parameters;
        mStatements  = statements;
        mDefinitions = definitions;
    }

    public SetlDefinition clone() {
        // this value can not be changed once set
        return this;
    }

    public void addToEnvironment() {
        Environment.putValue(mName, this);
    }

    /* calls (function calls) */

    public Value call(List<Value> args, boolean returnCollection) throws SetlException {
        if (returnCollection) {
            throw new UndefinedOperationException("Incorrect set of brackets for function call.");
        }
        if (mParameters.size() != args.size()) {
            throw new IncorrectNumberOfParametersException("Function is defined with a different number of parameters.");
        }

        // save old environment
        Environment oldEnv = Environment.getEnv();
        // create new environment used for the function call
        Environment.setEnv(oldEnv.cloneFunctions());

        // put arguments into environment
        for (int i = 0; i < args.size(); ++i) {
            Environment.putValue(mParameters.get(i), args.get(i));
        }
        // execute the definitions, because statments might call them later
        for(SetlDefinition d : mDefinitions){
            d.addToEnvironment();
        }

        Value result = SetlOm.OM;
        try {
            for (Statement stmnt: mStatements) {
                stmnt.execute();
            }
        } catch (ReturnException re) {
            result = re.getValue();
        }
        // restore old environment
        Environment.setEnv(oldEnv);
        return result;
    }

    public String toString(int tabs) {
        String endl = Environment.getEndl();
        String result = endl + Environment.getTabs(tabs) + "procedure " + mName + "(";
        for (int i = 0; i < mParameters.size(); ++i) {
            if (i > 0) {
                result += ", ";
            }
            result += mParameters.get(i);
        }
        result += ");" + endl;
        for (Statement stmnt: mStatements) {
            result += stmnt.toString(tabs + 1) + endl;
        }
        for (SetlDefinition dfntn: mDefinitions) {
            result += dfntn.toString(tabs + 1) + endl;
        }
        result += Environment.getTabs(tabs) + "end " + mName + ";";
        return result;
    }

    public String toString() {
        return toString(0);
    }

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
                return mName.compareTo(other.mName);
            } else {
                int cmp = mParameters.toString().compareTo(other.mParameters.toString());
                if (cmp != 0) {
                    return cmp;
                }
                cmp = mStatements.toString().compareTo(other.mStatements.toString());
                if (cmp != 0) {
                    return cmp;
                }
                return mDefinitions.toString().compareTo(other.mDefinitions.toString());
            }
        } else {
            // everything else is smaller
            return 1;
        }
    }
}

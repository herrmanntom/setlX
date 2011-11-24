package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.types.SetlList;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;

import java.util.List;

public class PD_makeTerm extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_makeTerm();

    private PD_makeTerm() {
        super("makeTerm");
        addParameter("name");
        addParameter("body");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        Value arg0 = args.get(0);
        Value arg1 = args.get(1);
        if ( ! (arg0 instanceof SetlString)) {
            throw new IncompatibleTypeException("Name '" + arg0 + "' is not a string.");
        }
        if ( ! (arg1 instanceof SetlList)) {
            throw new IncompatibleTypeException("Argument '" + arg1 + "' is not a list.");
        }
        String name = arg0.toString();
        // Strip out double quotes from string
        name = name.substring(1, name.length() - 1);
        // make the new Term
        return new Term(name, (SetlList) arg1);
    }
}


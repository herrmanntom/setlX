package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.types.SetlList;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;

import java.util.List;

// makeTerm(fnc, body)  : create a term with the form: fnc(body)

public class PD_makeTerm extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_makeTerm();

    private PD_makeTerm() {
        super("makeTerm");
        addParameter("functionalCharacter");
        addParameter("body");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        Value arg0 = args.get(0);
        Value arg1 = args.get(1);
        if ( ! (arg0 instanceof SetlString)) {
            throw new IncompatibleTypeException("functionalCharacter '" + arg0 + "' is not a string.");
        }
        if ( ! (arg1 instanceof SetlList)) {
            throw new IncompatibleTypeException("Argument '" + arg1 + "' is not a list.");
        }
        String fct = arg0.toString();
        // Strip out double quotes from string
        fct = fct.substring(1, fct.length() - 1);

        // check if name is usable as term (fist char is upper case or single qoute ( ' ))
        if (fct.length() > 0 && (fct.charAt(0) == '\'' || Character.isUpperCase(fct.charAt(0)))) {
            // make the new Term
            return new Term(fct, (SetlList) arg1);
        } else {
            throw new IncompatibleTypeException("functionalCharacter '" + fct + "' must start with an upper case letter or a single qoute.");
        }
    }
}


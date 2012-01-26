package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.expressions.Variable;
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
        String fct = arg0.getUnquotedString();

        // check if name is usable as term (fist char is upper case or single qoute ( ' ))
        if (fct.length() > 0 && (fct.charAt(0) == '\'' || Character.isUpperCase(fct.charAt(0)))) {
            // use correct internal representation when user wants to create a variable
            if (fct.equals(Variable.FUNCTIONAL_CHARACTER_EXTERNAL)) {
                fct = Variable.FUNCTIONAL_CHARACTER;
            }
            // make the new Term
            return new Term(fct, (SetlList) arg1);
        } else {
            throw new IncompatibleTypeException("functionalCharacter '" + fct + "' must start with an upper case letter or a single qoute.");
        }
    }
}


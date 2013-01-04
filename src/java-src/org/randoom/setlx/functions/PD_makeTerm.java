package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// makeTerm(fnc, body)  : create a term with the form: fnc(body)

public class PD_makeTerm extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_makeTerm();

    private PD_makeTerm() {
        super("makeTerm");
        addParameter("functionalCharacter");
        addParameter("body");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value arg0 = args.get(0);
        final Value arg1 = args.get(1);
        if ( ! (arg0 instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "FunctionalCharacter '" + arg0 + "' is not a string."
            );
        }
        if ( ! (arg1 instanceof SetlList)) {
            throw new IncompatibleTypeException(
                "Argument '" + arg1 + "' is not a list."
            );
        }
        String fct = arg0.getUnquotedString();

        // check if name is usable as term (fist char is upper case or single quote ( ' ))
        if (fct.length() > 0 && (fct.charAt(0) == '^' || Character.isUpperCase(fct.charAt(0)))) {
            // use correct internal representation when user wants to create a variable
            if (fct.equals(Variable.FUNCTIONAL_CHARACTER_EXTERNAL)) {
                fct = Variable.FUNCTIONAL_CHARACTER;
            }
            // make the new Term
            return new Term(fct, (SetlList) arg1);
        } else {
            throw new IncompatibleTypeException(
                "FunctionalCharacter '" + fct + "' must start with an upper case letter or a hat ('^')."
            );
        }
    }
}


package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.operators.Variable;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.HashMap;

/**
 * makeTerm(functionalCharacter, body) : Create a term with the form: functionalCharacter(body)
 */
public class PD_makeTerm extends PreDefinedProcedure {

    private final static ParameterDef        FUNCTIONAL_CHARACTER = createParameter("functionalCharacter");
    private final static ParameterDef        BODY                 = createParameter("body");

    /** Definition of the PreDefinedProcedure `makeTerm'. */
    public  final static PreDefinedProcedure DEFINITION           = new PD_makeTerm();

    private PD_makeTerm() {
        super();
        addParameter(FUNCTIONAL_CHARACTER);
        addParameter(BODY);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws IncompatibleTypeException {
        final Value arg0 = args.get(FUNCTIONAL_CHARACTER);
        final Value arg1 = args.get(BODY);
        if ( ! (arg0 instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "FunctionalCharacter '" + arg0.toString(state) + "' is not a string."
            );
        }
        if ( ! (arg1 instanceof SetlList)) {
            throw new IncompatibleTypeException(
                "Argument '" + arg1.toString(state) + "' is not a list."
            );
        }
        String fct = arg0.getUnquotedString(state);

        // check if name is usable as term (fist char is upper case or single quote ( ' ))
        if (TermUtilities.isInternalFunctionalCharacter(fct) || (fct.length() > 0 && Character.isUpperCase(fct.charAt(0)))) {
            // use correct internal representation when user wants to create a variable
            if (fct.equals(Variable.getFunctionalCharacterExternal())) {
                fct = Variable.getFunctionalCharacter();
            }
            // make the new Term
            return new Term(fct, (SetlList) arg1);
        } else {
            throw new IncompatibleTypeException(
                "FunctionalCharacter '" + fct + "' must start with an upper case letter or three hats ('" + TermUtilities.getPrefixOfInternalFunctionalCharacters() + "')."
            );
        }
    }
}


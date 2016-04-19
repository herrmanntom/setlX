package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.HashMap;

/**
 * makeTerm(functionalCharacter, body) : Create a term with the form: functionalCharacter(body)
 */
public class PD_makeTerm extends PreDefinedProcedure {

    private final static ParameterDefinition FUNCTIONAL_CHARACTER = createParameter("functionalCharacter");
    private final static ParameterDefinition BODY                 = createParameter("body");

    /** Definition of the PreDefinedProcedure `makeTerm'. */
    public  final static PreDefinedProcedure DEFINITION           = new PD_makeTerm();

    private PD_makeTerm() {
        super();
        addParameter(FUNCTIONAL_CHARACTER);
        addParameter(BODY);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
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

        if (! fct.startsWith(TermUtilities.getFunctionalCharacterPrefix())) {
            fct = TermUtilities.getFunctionalCharacterPrefix() + fct;
        }

        if (TermUtilities.isInternalFunctionalCharacter(fct) || TermUtilities.isFunctionalCharacter(fct)) {
            return new Term(fct, (SetlList) arg1);
        }

        throw new IncompatibleTypeException(
                "FunctionalCharacter '" + fct + "' must start with one or three hats ('" + TermUtilities.getFunctionalCharacterPrefix() + "' or '" + TermUtilities.getPrefixOfInternalFunctionalCharacters() + "')."
        );
    }
}


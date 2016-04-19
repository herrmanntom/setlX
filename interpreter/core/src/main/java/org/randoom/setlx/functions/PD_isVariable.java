package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.operators.Variable;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isVariable(term) : Returns true if `integer' is a prime, false otherwise.
 *                    Unlike isProbablePrime() this functions actually
 *                    tests all possible factors, therefore has linear
 *                    complexity.
 */
public class PD_isVariable extends PreDefinedProcedure {

    private final static ParameterDefinition TERM = createParameter("term");

    /** Definition of the PreDefinedProcedure `isVariable'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isVariable();

    private PD_isVariable() {
        super();
        addParameter(TERM);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
        final Value parameterValue  = args.get(TERM);
        if (parameterValue.isTerm() == SetlBoolean.TRUE) {
            Term term = (Term) parameterValue;
            if (
                    Variable.getFunctionalCharacter().equals(term.getFunctionalCharacter())
                    && term.firstMember().isString() == SetlBoolean.TRUE
            ) {
                return SetlBoolean.TRUE;
            }
        }

        return SetlBoolean.FALSE;

    }

}


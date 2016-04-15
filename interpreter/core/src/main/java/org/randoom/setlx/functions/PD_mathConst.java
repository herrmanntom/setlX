package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 *  mathConst(name_of_constant) : get the value of a mathematical constant (currently pi, e and infinity)
 */
public class PD_mathConst extends PreDefinedProcedure {

    private final static ParameterDefinition NAME_OF_CONSTANT = createParameter("nameOfConstant");

    /** Definition of the PreDefinedProcedure `mathConst'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_mathConst();

    private PD_mathConst() {
        super();
        addParameter(NAME_OF_CONSTANT);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args)
        throws UndefinedOperationException, IncompatibleTypeException
    {
        final Value name = args.get(NAME_OF_CONSTANT);
        if (       name.getUnquotedString(state).equalsIgnoreCase( "e"        )) {
            return SetlDouble.E;
        } else if (name.getUnquotedString(state).equalsIgnoreCase( "pi"       )) {
            return SetlDouble.PI;
        } else if (name.getUnquotedString(state).equalsIgnoreCase( "infinity" )) {
            return SetlDouble.POSITIVE_INFINITY;
        } else {
            throw new IncompatibleTypeException("Name-argument '" + name.toString(state) + "' is not a known constant or not a string.");
        }
    }
}


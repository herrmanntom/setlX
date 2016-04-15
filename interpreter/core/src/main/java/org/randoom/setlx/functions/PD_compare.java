package org.randoom.setlx.functions;

import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * compare(valueA, valueB) : Compares two arbitrary values, returns an integer.
 *                           Return value <  0 means: valueA "<" valueB
 *                           Return value == 0 means: valueA ==  valueB
 *                           Return value >  0 means: valueA ">" valueB
 *                           Note that order between different types was chosen
 *                           arbitrarily and implies no special meaning.
 */
public class PD_compare extends PreDefinedProcedure {

    private final static ParameterDefinition VALUE_A      = createParameter("valueA");
    private final static ParameterDefinition VALUE_B      = createParameter("valueB");

    /** Definition of the PreDefinedProcedure `compare'. */
    public  final static PreDefinedProcedure DEFINITION   = new PD_compare();

    private PD_compare() {
        super();
        addParameter(VALUE_A);
        addParameter(VALUE_B);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        return Rational.valueOf(args.get(VALUE_A).compareTo(args.get(VALUE_B)));
    }
}


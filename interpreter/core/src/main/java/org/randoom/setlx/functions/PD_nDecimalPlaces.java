package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * nDecimalPlaces(rational, n := 2) : Get string of rational number with max of n digits after decimal point.
 */
public class PD_nDecimalPlaces extends PreDefinedProcedure {

    private final static ParameterDefinition RATIONAL   = createParameter("rational");
    private final static ParameterDefinition N_DIGITS   = createOptionalParameter("nDigits", Rational.TWO);

    /** Definition of the PreDefinedProcedure `nDecimalPlaces'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_nDecimalPlaces();

    private PD_nDecimalPlaces() {
        super();
        addParameter(RATIONAL);
        addParameter(N_DIGITS);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
              Value number  = args.get(RATIONAL);
        final Value nValue  = args.get(N_DIGITS);
        if ( !(number instanceof Rational)) {
            throw new IncompatibleTypeException(
                "Rational-argument '" + number + "' is not a rational number."
            );
        }
        if (nValue.isInteger() == SetlBoolean.FALSE || nValue.compareTo(Rational.ZERO) < 1) {
            throw new IncompatibleTypeException(
                "N-argument '" + nValue + "' is not an integer >= 1."
            );
        }
        final int           n       = nValue.jIntValue();
	
	boolean sign = false;
	if (number.compareTo(Rational.ZERO) < 0) {
	    number = number.minus(state);
	    sign = true;
	}    
              Value         rest    = number.modulo(state, Rational.ONE);
        final Value         intPart = number.difference(state, rest);

        final StringBuilder result  = new StringBuilder();
	if (sign) {
	    result.append("-");
	}
        intPart.appendString(state, result, 0);
        result.append(".");
        for (int i = 1; i <= n; ++i) {
            rest           = rest.product(state, Rational.TEN);
            Value restMod1 = rest.modulo(state, Rational.ONE);
            Value digit    = rest.difference(state, restMod1);
            rest           = restMod1;

            digit.appendString(state, result, 0);
        }
	
        return SetlString.newSetlStringFromSB(result);
    }
}


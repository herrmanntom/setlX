package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// nDecimalPlaces(rational, n) : get string of rational number with max of n digits after decimal point

public class PD_nDecimalPlaces extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_nDecimalPlaces();

    private PD_nDecimalPlaces() {
        super("nDecimalPlaces");
        addParameter("rational");
        addParameter("n");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value number  = args.get(0);
        final Value nValue  = args.get(1);
        if ( ! (number instanceof Rational)) {
            throw new IncompatibleTypeException(
                "Rational-argument '" + number + "' is not a rational number."
            );
        }
        if (nValue.isInteger() == SetlBoolean.FALSE || nValue.compareTo(Rational.ZERO) < 1 ) {
            throw new IncompatibleTypeException(
                "N-argument '" + nValue + "' is not an integer >= 1."
            );
        }
        final int           n       = ((Rational) nValue).intValue();

              Value         rest    = number.modulo(state, Rational.ONE);
        final Value         intPart = number.difference(state, rest);

        final StringBuilder result  = new StringBuilder();
              Value         digit   = null;
              Value         restMod1= null;

        intPart.appendString(state, result, 0);
        result.append(".");
        for (int i = 1; i <= n; ++i) {
            rest    = rest.product(state, Rational.TEN);
            restMod1= rest.modulo(state, Rational.ONE);
            digit   = rest.difference(state, restMod1);
            rest    = restMod1;

            digit.appendString(state, result, 0);
        }

        return SetlString.newSetlStringFromSB(result);
    }
}


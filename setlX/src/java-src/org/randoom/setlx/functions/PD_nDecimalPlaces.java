package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.List;

// nDecimalPlaces(rational, n) : get string of rational number with max of n digits after decimal point

public class PD_nDecimalPlaces extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_nDecimalPlaces();

    private PD_nDecimalPlaces() {
        super("nDecimalPlaces");
        addParameter("rational");
        addParameter("n");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value number  = args.get(0);
        final Value nValue  = args.get(1);
        if ( ! (number instanceof Rational)) {
            throw new IncompatibleTypeException(
                "rational-argument '" + number + "' is not a rational number."
            );
        }
        if (nValue.isInteger() == SetlBoolean.FALSE || nValue.compareTo(new Rational(0)) < 1 ) {
            throw new IncompatibleTypeException(
                "n-argument '" + nValue + "' is not an integer >= 1."
            );
        }
        final int           n       = ((Rational) nValue).intValue();

              Value         rest    = number.modulo(new Rational(1));
        final Value         intPart = number.difference(rest);

        final StringBuilder result  = new StringBuilder();
              Value         digit   = null;
              Value         restMod1= null;

        intPart.appendString(result, 0);
        result.append(".");
        for (int i = 1; i <= n; ++i) {
            rest    = rest.multiply(new Rational(10));
            restMod1= rest.modulo(new Rational(1));
            digit   = rest.difference(restMod1);
            rest    = restMod1;

            digit.appendString(result, 0);
        }

        return new SetlString(result.toString());
    }
}


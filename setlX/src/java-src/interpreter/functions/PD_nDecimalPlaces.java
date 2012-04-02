package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.types.Rational;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.List;

// nDecimalPlaces(rational, n) : get string of rational number with max of n digits after decimal point

public class PD_nDecimalPlaces extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_nDecimalPlaces();

    private PD_nDecimalPlaces() {
        super("nDecimalPlaces");
        addParameter("rational");
        addParameter("n");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value   number  = args.get(0);
        Value   nValue  = args.get(1);
        if ( ! (number instanceof Rational)) {
            throw new IncompatibleTypeException("rational-argument '" + number + "' is not a rational number.");
        }
        if (nValue.isInteger() == SetlBoolean.FALSE || nValue.compareTo(new Rational(0)) < 1 ) {
            throw new IncompatibleTypeException("n-argument '" + nValue + "' is not an integer >= 1.");
        }
        int     n       = ((Rational) nValue).intValue();

        Value   rest    = number.modulo(new Rational(1));
        Value   intPart = number.difference(rest);

        String  result  = intPart.toString() + ".";
        Value   digit   = null;
        Value   restMod1= null;

        for (int i = 1; i <= n; ++i) {
            rest    = rest.multiply(new Rational(10));
            restMod1= rest.modulo(new Rational(1));
            digit   = rest.difference(restMod1);
            rest    = restMod1;

            result += digit.toString();
        }

        return new SetlString(result);
    }
}


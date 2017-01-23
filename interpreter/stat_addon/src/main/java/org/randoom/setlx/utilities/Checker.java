package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;

/**
 * Provides utility functions that assure the correct type of values.
 */
public class Checker {

    /** Checks if given values are numbers and if not, throws an exception */
    public static boolean checkIfNumber(State state, Value... values) throws IncompatibleTypeException {
        for (Value value : values) {
            if (! (value.isRational() == SetlBoolean.TRUE || value.isDouble() == SetlBoolean.TRUE)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not a number."
                );
            }
        }
        return true;
    }

    /** Checks if given values are numbers and different to zero and if not, throws an exception */
    public static boolean checkIfNumberAndNotZero(State state, String parameterName, Value... values) throws SetlException{
        for (Value value: values) {
            if (! (value.isRational() == SetlBoolean.TRUE || value.isDouble() == SetlBoolean.TRUE)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not a number."
                );
            } else if (value.toJDoubleValue(state) == 0) {
                throw new IncompatibleTypeException(
                        "The parameter '" + parameterName + "' cannot be zero."
                );
            }
        }
        return true;
    }

    /** Checks if given values are numbers and greater zero and if not, throws an exception */
    public static boolean checkIfNumberAndGreaterZero(State state, Value... values) throws SetlException {
        for (Value value : values) {
            if (! (value.isRational() == SetlBoolean.TRUE || value.isDouble() == SetlBoolean.TRUE)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not a number."
                );
            }
            if (! (value.toJDoubleValue(state) > 0)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not greater than zero."
                );
            }
        }
        return true;
    }

    /** Checks if given values are numbers and greater or equal zero and if not, throws an exception */
    public static boolean checkIfNumberAndGreaterOrEqualZero(State state, Value... values) throws SetlException {
        for (Value value : values) {
            if (! (value.isRational() == SetlBoolean.TRUE || value.isDouble() == SetlBoolean.TRUE)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not a number."
                );
            }
            if (! (value.toJDoubleValue(state) >= 0)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not greater or equal zero."
                );
            }
        }
        return true;
    }

    /** Checks if given values are natural numbers and if not, throws an exception */
    public static boolean checkIfNaturalNumber(State state, Value... values) throws IncompatibleTypeException {
        for (Value value : values) {
            if (! (value.isInteger() == SetlBoolean.TRUE)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not a natural number."
                );
            }
        }
        return true;
    }

    /** Checks if given values are natural numbers and greater zero and if not, throws an exception */
    public static boolean checkIfNaturalNumberAndGreaterZero(State state, Value... values) throws SetlException {
        for (Value value : values) {
            if (! (value.isInteger() == SetlBoolean.TRUE)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not a natural number."
                );
            }
            if (! (value.toJDoubleValue(state) > 0)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not greater zero."
                );
            }
        }
        return true;
    }

    /** Checks if a Value is a canvas */
    public static boolean checkIfCanvas(State state, Value value) throws IncompatibleTypeException {
        if (! (value instanceof Canvas)) {
            throw new IncompatibleTypeException(
                    "Input-argument '" + value.toString(state) + "' is not of type canvas."
            );
        }
        return true;
    }
}

package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;

/**
 * Provides utility functions that assure the correct type of values.
 */
public class Checker {

    /** Checks if a Value is a Number and if not, throws an exception */
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

    /** Checks if a Value is a Number and greater zero and if not, throws an exception */
    public static boolean checkIfNumberAndGreaterZero(State state, Value... values) throws SetlException {
        for (Value value : values) {
            if (! (value.isRational() == SetlBoolean.TRUE || value.isDouble() == SetlBoolean.TRUE)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not a number."
                );
            }
            if (! (value.toJDoubleValue(state) > 0)) {
                throw new IncompatibleTypeException(
                        "Input-argument '" + value.toString(state) + "' is not greater or equal zero."
                );
            }
        }
        return true;
    }

    /** Checks if a Value is a Number and greater or equal zero and if not, throws an exception */
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

    /** Checks if a Value is a natural Number and if not, throws an exception */
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

    /** Checks if a Value is a natural Number and greater zero and if not, throws an exception */
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
}

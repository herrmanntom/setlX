package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;

public class SetlBoolean extends Value {

    public final static SetlBoolean FALSE = new SetlBoolean();
    public final static SetlBoolean TRUE  = new SetlBoolean();

    private SetlBoolean() {  }

    public SetlBoolean clone() {
        // this value is atomic and can not be changed once set
        return this;
    }

    public static SetlBoolean get(boolean value){
        if (value) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    public SetlBoolean and(Value other) throws IncompatibleTypeException {
        if (other instanceof SetlBoolean) {
            if (this == TRUE && other == TRUE) {
                return TRUE;
            } else {
                return FALSE;
            }
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " and " + other + "´ is not a Boolean value.");
        }
    }

    public SetlBoolean not() {
        if (this == TRUE) {
            return FALSE;
        } else {
            return TRUE;
        }
    }

    public SetlBoolean or(Value other) throws IncompatibleTypeException {
        if (other instanceof SetlBoolean) {
            if (this == TRUE || other == TRUE) {
                return TRUE;
            } else {
                return FALSE;
            }
        } else {
            throw new IncompatibleTypeException("Right-hand-side of `" + this + " or " + other + "´ is not a Boolean value.");
        }
    }

    public String toString() {
        if (this == TRUE) {
            return "true";
        } else {
            return "false";
        }
    }

    // Compare two Values.  Returns -1 if this value is less than the value given
    // as argument, +1 if its greater and 0 if both values contain the same
    // elements.
    // Useful output is only possible if both values are of the same type.
    // "incomparable" values, e.g. of different types are ranked as follows:
    // SetlOm < SetlBoolean < SetlInt & SetlReal < SetlString < SetlSet < SetlTuple < SetlDefinition
    // This ranking is necessary to allow sets and lists of different types.
    public int compareTo(Value v){
        if (v instanceof SetlBoolean) {
            if (this == v) {
                return 0;
            } else if (this == TRUE && v == FALSE) {
                return 1;
            } else {
                return -1;
            }
        } else if (v instanceof SetlOm) {
            // only SetlOm is smaller
            return 1;
        } else {
            return -1;
        }
    }
}

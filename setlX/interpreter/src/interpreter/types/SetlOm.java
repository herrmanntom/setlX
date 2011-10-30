package interpreter.types;

public class SetlOm extends Value {

    public final static SetlOm OM = new SetlOm();

    private SetlOm() {  }

    public SetlOm clone() {
        // this value is atomic and can not be changed
        return this;
    }

    /* String and Char operations */

    public String toString() {
        return "om";
    }

    /* Comparisons */

    // Compare two Values.  Returns -1 if this value is less than the value given
    // as argument, +1 if its greater and 0 if both values contain the same
    // elements.
    // Useful output is only possible if both values are of the same type.
    // "incomparable" values, e.g. of different types are ranked as follows:
    // SetlOm < SetlBoolean < SetlInt & SetlReal < SetlString < SetlSet < SetlList < SetlDefinition
    // This ranking is necessary to allow sets and lists of different types.
    public int compareTo(Value v){
        if (v == OM) {
            return 0;
        } else {
            // everything is bigger than SetlOm
            return -1;
        }
    }
}

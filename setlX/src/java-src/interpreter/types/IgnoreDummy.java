package interpreter.types;

public class IgnoreDummy extends Value {

    public final static IgnoreDummy ID = new IgnoreDummy();

    private IgnoreDummy() {}

    public IgnoreDummy clone() {
        // this value is atomic and can not be changed
        return this;
    }

    public String toString() {
        return "_";
    }

    public int compareTo(Value v) {
        return -1; // dummy is uncomparable, but throwable interface does not handle exceptions...
    }
}

package interpreter.types;

import interpreter.exceptions.UndefinedOperationException;

public class RangeDummy extends Value {

    public final static RangeDummy RD = new RangeDummy();

    private RangeDummy(){}

    public RangeDummy clone() {
        // this value is atomic and can not be changed
        return this;
    }

    public String toString(){
        return "..";
    }

    public int compareTo(Value v) throws UndefinedOperationException {
        throw new UndefinedOperationException("dummy is uncomparable");
    }
}

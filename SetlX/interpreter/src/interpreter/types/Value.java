package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.utilities.Environment;

import java.util.List;

public abstract class Value implements Comparable<Value> {

    public abstract Value   clone();

    /* Comparisons */

    // Compare two Values.  Returns -1 if this value is less than the value given
    // as argument, +1 if its greater and 0 if both values contain the same
    // elements.
    // Useful output is only possible if both values are of the same type.
    // "incomparable" values, e.g. of different types are ranked as follows:
    // SetlOm < SetlBoolean < SetlInt & SetlReal < SetlString < SetlSet < SetlList < SetlDefinition
    // This ranking is necessary to allow sets and lists of different types.
    public abstract int compareTo(Value v);

    public final boolean equals(Value v) {
        return this.compareTo(v) == 0;
    }

    public final SetlBoolean isEqual(Value other) {
        return SetlBoolean.get(this.equals(other));
    }

    /* this comparison is different than `this.compareTo(other) < 0' and should
       throw errors on seemingly incomparable types like `5 < TRUE'            */
    public SetlBoolean isLessThan(Value other) throws SetlException {
        throw new UndefinedOperationException("`" + this + " < " + other + "´ is undefined.");
    }

    /* Boolean operations */

    public SetlBoolean and(Value other) throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Left-hand-side of `" + this + " and " + other + "´ is not a Boolean value.");
    }

    public SetlBoolean not() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand of `not " + this + "´ is not a Boolean value.");
    }

    public SetlBoolean or(Value other) throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Left-hand-side of `" + this + " or " + other + "´ is not a Boolean value.");
    }

    /* arithmetic operations */

    public NumberValue absoluteValue() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand of `abs " + this + "´ is not a number or character.");
    }

    public Value add(Value summand) throws SetlException {
        if (summand instanceof SetlString) {
            return ((SetlString) summand).addFlipped(this);
        }
        throw new UndefinedOperationException("`" + this + " + " + summand + "´ is undefined.");
    }

    public NumberValue divide(Value divisor) throws SetlException {
        throw new UndefinedOperationException("`" + this + " / " + divisor + "´ is undefined.");
    }

    public final Value maximum(Value other) throws SetlException {
        if(other != SetlOm.OM && this.isLessThan(other) == SetlBoolean.TRUE){
            return other.clone();
        } else {
            return this.clone();
        }
    }

    public final Value minimum(Value other) throws SetlException {
        if(other == SetlOm.OM || other.isLessThan(this) == SetlBoolean.TRUE){
            return other.clone();
        } else {
            return this.clone();
        }
    }

    public Value mod(Value modulo) throws SetlException {
        throw new UndefinedOperationException("`" + this + " % " + modulo + "´ is undefined.");
    }

    public Value multiply(Value multiplier) throws SetlException {
        throw new UndefinedOperationException("`" + this + " * " + multiplier + "´ is undefined.");
    }

    public NumberValue negate() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand of `- " + this + "´ is not a number.");
    }

    public NumberValue power(Value exponent) throws SetlException {
        throw new IncompatibleTypeException("Left-hand-side of `" + this + " ** " + exponent + "´ is not a number.");
    }

    public Value subtract(Value subtrahend) throws SetlException {
        throw new UndefinedOperationException("`" + this + " - " + subtrahend + "´ is undefined.");
    }

    /* operations on compound values (Lists/Tuples, Sets [, Strings]) */

    public void addMember(Value element) throws SetlException {
        throw new IncompatibleTypeException("Can not add `" + element + "´ into operand; `" + this + "´ is not a compound value.");
    }

    public Value addMembers() throws SetlException {
        throw new IncompatibleTypeException("Right-hand-side of `+/ " + this + "´ is not a compound value.");
    }

    public Value arbitraryMember() throws SetlException {
        throw new IncompatibleTypeException("Operand of `arb " + this + "´ is not a compound value.");
    }

    public final SetlInt cardinality() throws IncompatibleTypeException {
        return new SetlInt(this.size());
    }

    public SetlSet collectMembers(Value index) throws SetlException {
        throw new IncompatibleTypeException("Can not get member with index `" + index + "´ from operand; `" + this + "´ is not a map.");
    }

    public SetlBoolean containsMember(Value element) throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Right-hand-side of `" + element  + " in " + this + "´ is not a compound value.");
    }

    public SetlSet domain() throws SetlException {
        throw new IncompatibleTypeException("Operand of `domain " + this + "´ is not a set.");
    }

    public Value firstMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Can not get first member from operand; `" + this + "´ is not a compound value.");
    }

    public Value getMember(Value index) throws SetlException {
        throw new IncompatibleTypeException("Can not get member with index `" + index + "´ from operand; `" + this + "´ is not a compound value or direct access is unsupported for this type.");
    }

    public Value getMemberUnCloned(Value index) throws SetlException {
        throw new IncompatibleTypeException("Can not get member with index `" + index + "´ from operand; `" + this + "´ is not a compound value or direct access is unsupported for this type.");
    }

    public Value getMembers(Value low, Value high) throws SetlException {
        throw new IncompatibleTypeException("Can not get member between index `" + low + "´ and `" + high + "´ from operand; `" + this + "´ is not a compound value or ranges are unsupported for this type.");
    }

    public SetlBoolean isMap() {
        return SetlBoolean.FALSE;
    }

    public Value lastMember() throws SetlException {
        throw new IncompatibleTypeException("Can not get last member from operand; `" + this + "´ is not a compound value.");
    }

    public Value maximumMember() throws SetlException {
        throw new IncompatibleTypeException("Right-hand-side of `max/ " + this + "´ is not a compound value.");
    }

    public Value minimumMember() throws SetlException {
        throw new IncompatibleTypeException("Right-hand-side of `min/ " + this + "´ is not a compound value.");
    }

    public Value multiplyMembers() throws SetlException {
        throw new IncompatibleTypeException("Right-hand-side of `*/ " + this + "´ is not a compound value.");
    }

    public SetlSet powerSet() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand of `pow " + this + "´ is not a set.");
    }

    public SetlSet range() throws SetlException {
        throw new IncompatibleTypeException("Operand of `range " + this + "´ is not a set.");
    }

    public void removeMember(Value element) throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Can not remove `" + element + "´ from operand; `" + this + "´ is not a compound value.");
    }

    public void removeFirstMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Can not remove first member from operand; `" + this + "´ is not a compound value.");
    }

    public void removeLastMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Can not remove last member from operand; `" + this + "´ is not a compound value.");
    }

    public void setMember(Value index, Value v) throws SetlException {
        throw new IncompatibleTypeException("Can not set member with index `" + index + "´ from operand; `" + this + "´ is not a compound value or direct access is unsupported for this type.");
    }

    public int size() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand of `# " + this + "´ is not a compound value.");
    }

    /* calls (element access or function call) */

    public Value call(List<Value> args, boolean returnCollection) throws SetlException {
        throw new IncompatibleTypeException("Can not perform call with arguments `" + args + "´ on this operand-type; `" + this + "´ is not a function or compound value.");
    }

    /* String and Char operations */

    public SetlString charConvert() throws SetlException {
        throw new IncompatibleTypeException("Operand of `char " + this + "´ is not a number between 0 and 255.");
    }

    public SetlString str() {
        return new SetlString(this.toString());
    }

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + toString();
    }

    public String toStringForPrint() {
        return toString();
    }
}



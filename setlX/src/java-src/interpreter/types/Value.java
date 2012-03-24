package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;
import interpreter.utilities.Environment;
import interpreter.utilities.MatchResult;

import java.util.List;

public abstract class Value implements Comparable<Value> {

    private int  mLineNr = -1;

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = Environment.sourceLine;
    }

    public abstract Value   clone();

    /* Boolean operations */

    public Value and(Expr other) throws SetlException {
        throw new IncompatibleTypeException("Left-hand-side of '" + this + " && " + other + "' is not a Boolean value.");
    }

    public Value implies(Expr other) throws SetlException {
        throw new IncompatibleTypeException("Left-hand-side of '" + this + " => " + other + "' is not a Boolean value.");
    }

    public Value not() throws SetlException {
        throw new IncompatibleTypeException("Operand of '!" + this + "' is not a Boolean value.");
    }

    public Value or(Expr other) throws SetlException {
        throw new IncompatibleTypeException("Left-hand-side of '" + this + " || " + other + "' is not a Boolean value.");
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isBoolean() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isError() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isInfinity() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isInteger() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isList() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isMap() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isProcedure() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isReal() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isSet() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isString() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isTerm() {
        return SetlBoolean.FALSE;
    }

    /* type conversions */

    public Value toInteger() {
        return Om.OM;
    }

    public Value toReal() {
        return Om.OM;
    }

    /* arithmetic operations */

    public NumberValue absoluteValue() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand '" + this + "' is not a number or character.");
    }

    public Value difference(Value subtrahend) throws SetlException {
        if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(this);
        }
        throw new UndefinedOperationException("'" + this + " - " + subtrahend + "' is undefined.");
    }

    public Value divide(Value divisor) throws SetlException {
        if (divisor instanceof Term) {
            return ((Term) divisor).divideFlipped(this);
        }
        throw new UndefinedOperationException("'" + this + " / " + divisor + "' is undefined.");
    }

    public Value factorial() throws SetlException {
        throw new UndefinedOperationException("'" + this + "!' is undefined.");
    }

    public void fillCollectionWithinRange(Value step, Value stop, CollectionValue collection) throws SetlException {
        throw new IncompatibleTypeException("Start argument '" + this + "' is not an integer.");
    }

    public final Value maximum(Value other) throws SetlException {
        if(other != Om.OM && this.isLessThan(other) == SetlBoolean.TRUE){
            return other.clone();
        } else {
            return this.clone();
        }
    }

    public final Value minimum(Value other) throws SetlException {
        if(other == Om.OM || other.isLessThan(this) == SetlBoolean.TRUE){
            return other.clone();
        } else {
            return this.clone();
        }
    }

    public Value modulo(Value modulo) throws SetlException {
        if (modulo instanceof Term) {
            return ((Term) modulo).moduloFlipped(this);
        }
        throw new UndefinedOperationException("'" + this + " % " + modulo + "' is undefined.");
    }

    public Value multiply(Value multiplier) throws SetlException {
        if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
        }
        throw new UndefinedOperationException("'" + this + " * " + multiplier + "' is undefined.");
    }

    public Value negate() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand '" + this + "' is not a number.");
    }

    public Value power(Value exponent) throws SetlException {
        if (exponent instanceof Term) {
            return ((Term) exponent).powerFlipped(this);
        }
        throw new IncompatibleTypeException("Left-hand-side of '" + this + " ** " + exponent + "' is not a number.");
    }

    public Value sum(Value summand) throws SetlException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand instanceof SetlString) {
            return ((SetlString) summand).sumFlipped(this);
        }
        throw new UndefinedOperationException("'" + this + " + " + summand + "' is undefined.");
    }

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    public void addMember(Value element) throws SetlException {
        throw new IncompatibleTypeException("Can not add '" + element + "' into operand; '" + this + "' is not a collection value.");
    }

    public Value arbitraryMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand '" + this + "' is not a collection value.");
    }

    public SetlList arguments() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand '" + this + "' is not a term.");
    }

    public Value cardinality() throws IncompatibleTypeException {
        return new SetlInt(this.size());
    }

    public Value collectionAccess(List<Value> args) throws SetlException {
        throw new IncompatibleTypeException("Can not access elements using the arguments '" + args + "' on this operand-type; '" + this + "' is not a collection value.");
    }

    public Value collectionAccessUnCloned(List<Value> args) throws SetlException {
        throw new IncompatibleTypeException("Can not access elements using the arguments '" + args + "' on this operand-type; '" + this + "' is not a collection value.");
    }

    // returns a set of all pairs which first element matches arg
    public Value collectMap(Value arg) throws SetlException {
        throw new IncompatibleTypeException("Can not collect values of members matching the key '" + arg + "' on this operand-type; '" + this + "' is not a map.");
    }

    public SetlBoolean containsMember(Value element) throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Right-hand-side of '" + element  + " in " + this + "' is not a collection value.");
    }

    public SetlSet domain() throws SetlException {
        throw new IncompatibleTypeException("Operand '" + this + "' is not a set.");
    }

    public Value firstMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Can not get first member from operand; '" + this + "' is not a collection value.");
    }

    public SetlString functionalCharacter() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand '" + this + "' is not a term.");
    }

    public Value getMember(Value index) throws SetlException {
        throw new IncompatibleTypeException("Can not get member with index '" + index + "' from operand; '" + this + "' is not a collection value or direct access is unsupported for this type.");
    }

    public Value getMemberUnCloned(Value index) throws SetlException {
        throw new IncompatibleTypeException("Can not get member with index '" + index + "' from operand; '" + this + "' is not a collection value or direct access is unsupported for this type.");
    }

    public Value getMembers(Value low, Value high) throws SetlException {
        throw new IncompatibleTypeException("Can not get member between index '" + low + "' and '" + high + "' from operand; '" + this + "' is not a collection value or ranges are unsupported for this type.");
    }

    public Value lastMember() throws SetlException {
        throw new IncompatibleTypeException("Can not get last member from operand; '" + this + "' is not a collection value.");
    }

    public Value maximumMember() throws SetlException {
        throw new IncompatibleTypeException("Right-hand-side of 'max/ " + this + "' is not a collection value.");
    }

    public Value minimumMember() throws SetlException {
        throw new IncompatibleTypeException("Right-hand-side of 'min/ " + this + "' is not a collection value.");
    }

    public Value multiplyMembers() throws SetlException {
        throw new IncompatibleTypeException("Right-hand-side of '*/ " + this + "' is not a collection value.");
    }

    public SetlSet powerSet() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand '" + this + "' is not a set.");
    }

    public Value randomMember() throws SetlException {
        throw new IncompatibleTypeException("Operand '" + this + "' is not a collection value.");
    }

    public SetlSet range() throws SetlException {
        throw new IncompatibleTypeException("Operand '" + this + "' is not a set.");
    }

    public void removeMember(Value element) throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Can not remove '" + element + "' from operand; '" + this + "' is not a collection value.");
    }

    public void removeFirstMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Can not remove first member from operand; '" + this + "' is not a collection value.");
    }

    public void removeLastMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Can not remove last member from operand; '" + this + "' is not a collection value.");
    }

    public void setMember(Value index, Value v) throws SetlException {
        throw new IncompatibleTypeException("Can not set member with index '" + index + "' from operand; '" + this + "' is not a collection value or direct access is unsupported for this type.");
    }

    public int size() throws IncompatibleTypeException {
        throw new IncompatibleTypeException("Operand of '#" + this + "' is not a collection value.");
    }

    public Value sumMembers() throws SetlException {
        throw new IncompatibleTypeException("Right-hand-side of '+/ " + this + "' is not a collection value.");
    }

    /* function call */

    public Value call(List<Expr> exprs, List<Value> args) throws SetlException {
        throw new IncompatibleTypeException("Can not perform call with arguments '" + args + "' on this operand-type; '" + this + "' is not a procedure.");
    }

    /* string and char operations */

    public String canonical() {
        return toString();
    }

    public SetlString charConvert() throws SetlException {
        throw new IncompatibleTypeException("Operand '" + this + "' is not a number between 0 and 255.");
    }

    public String getUnquotedString() {
        return toString();
    }

    public SetlString str() {
        return new SetlString(this.toString());
    }

    public String toString(int tabs) {
        return toString();
    }

    public abstract String toString();

    /* term operations */

    public MatchResult matchesTerm(Value other) {
        if (other == IgnoreDummy.ID || this.equals(other)) {
            return new MatchResult(true);
        } else {
            return new MatchResult(false);
        }
    }

    public Value toTerm() {
        return this.clone();
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * SetlError < Om < -Infinity < SetlBoolean < SetlInt & Real < SetlString < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public abstract int compareTo(Value v);

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Value) {
            return this.compareTo((Value) o) == 0;
        } else {
            return false;
        }
    }

    public final SetlBoolean isEqual(Value other) {
        return SetlBoolean.get(this.equals(other));
    }

    /* this comparison is different than `this.compareTo(other) < 0' and should
       throw errors on seemingly incomparable types like `5 < TRUE'            */
    public SetlBoolean isLessThan(Value other) throws SetlException {
        throw new UndefinedOperationException("'" + this + " < " + other + "' is undefined.");
    }
}



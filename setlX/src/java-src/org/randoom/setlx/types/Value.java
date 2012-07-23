package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.utilities.MatchResult;

import java.util.List;

public abstract class Value implements Comparable<Value> {

    public abstract Value   clone();

    /* Boolean operations */

    public Value and(final Expr other) throws SetlException {
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this + " && " + other + "' is not a Boolean value."
        );
    }

    public Value implies(final Expr other) throws SetlException {
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this + " => " + other + "' is not a Boolean value."
        );
    }

    public Value not() throws SetlException {
        throw new IncompatibleTypeException(
            "Operand of '!" + this + "' is not a Boolean value."
        );
    }

    public Value or(final Expr other) throws SetlException {
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this + " || " + other + "' is not a Boolean value."
        );
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

    public SetlBoolean isRational() {
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

    public Value toRational() {
        return Om.OM;
    }

    public Value toReal() {
        return Om.OM;
    }

    /* arithmetic operations */

    public NumberValue absoluteValue() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a number or character."
        );
    }

    public Value ceil() throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a number."
        );
    }

    public Value difference(final Value subtrahend) throws SetlException {
        if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(this);
        }
        throw new UndefinedOperationException(
            "'" + this + " - " + subtrahend + "' is undefined."
        );
    }

    public Value differenceAssign(final Value subtrahend) throws SetlException {
        return difference(subtrahend);
    }

    public Value divide(final Value divisor) throws SetlException {
        if (divisor instanceof Term) {
            return ((Term) divisor).divideFlipped(this);
        }
        throw new UndefinedOperationException(
            "'" + this + " / " + divisor + "' is undefined."
        );
    }

    public Value divideAssign(final Value divisor) throws SetlException {
        return divide(divisor);
    }

    public Value factorial() throws SetlException {
        throw new UndefinedOperationException(
            "'" + this + "!' is undefined."
        );
    }

    public void fillCollectionWithinRange(final Value step, final Value stop, final CollectionValue collection) throws SetlException {
        throw new IncompatibleTypeException(
            "Start argument '" + this + "' is not a number."
        );
    }

    public Value floor() throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a number."
        );
    }

    public final Value maximum(final Value other) throws SetlException {
        if(other != Om.OM && this.isLessThan(other) == SetlBoolean.TRUE){
            return other.clone();
        } else {
            return this.clone();
        }
    }

    public final Value minimum(final Value other) throws SetlException {
        if(other == Om.OM || other.isLessThan(this) == SetlBoolean.TRUE){
            return other.clone();
        } else {
            return this.clone();
        }
    }

    public Value modulo(final Value modulo) throws SetlException {
        if (modulo instanceof Term) {
            return ((Term) modulo).moduloFlipped(this);
        }
        throw new UndefinedOperationException(
            "'" + this + " % " + modulo + "' is undefined."
        );
    }

    public Value moduloAssign(final Value modulo) throws SetlException {
        return modulo(modulo);
    }

    public Value multiply(final Value multiplier) throws SetlException {
        if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
        }
        throw new UndefinedOperationException(
            "'" + this + " * " + multiplier + "' is undefined."
        );
    }

    public Value multiplyAssign(final Value multiplier) throws SetlException {
        return multiply(multiplier);
    }

    public Value negate() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a number."
        );
    }

    public Value power(final Value exponent) throws SetlException {
        if (exponent instanceof Term) {
            return ((Term) exponent).powerFlipped(this);
        }
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this + " ** " + exponent + "' is not a number."
        );
    }

    public Value round() throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a number."
        );
    }

    public Value sum(final Value summand) throws SetlException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand instanceof SetlString && this != Om.OM) {
            return ((SetlString) summand).sumFlipped(this);
        }
        throw new UndefinedOperationException(
            "'" + this + " + " + summand + "' is undefined."
        );
    }

    public Value sumAssign(final Value summand) throws SetlException {
        return sum(summand);
    }

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    public void addMember(final Value element) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not add '" + element + "' into operand; '" + this + "' is not a collection value."
        );
    }

    public Value arbitraryMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a collection value."
        );
    }

    public SetlList arguments() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a term."
        );
    }

    public Value cardinality() throws IncompatibleTypeException {
        return Rational.valueOf(this.size());
    }

    public Value collectionAccess(final List<Value> args) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not access elements using the arguments '" + args + "' on this operand-type;" +
            " '" + this + "' is not a collection value."
        );
    }

    public Value collectionAccessUnCloned(final List<Value> args) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not access elements using the arguments '" + args + "' on this operand-type;" +
            " '" + this + "' is not a collection value."
        );
    }

    // returns a set of all pairs which first element matches arg
    public Value collectMap(final Value arg) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not collect values of members matching the key '" + arg + "' on this operand-type;" +
            " '" + this + "' is not a map."
        );
    }

    public SetlBoolean containsMember(final Value element) throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Right-hand-side of '" + element  + " in " + this + "' is not a collection value."
        );
    }

    public SetlSet domain() throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a set."
        );
    }

    public Value firstMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Can not get first member from operand; '" + this + "' is not a collection value."
        );
    }

    public SetlString functionalCharacter() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a term."
        );
    }

    public Value getMember(final Value index) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get member with index '" + index + "' from operand;" +
            " '" + this + "' is not a collection value or direct access is unsupported for this type."
        );
    }

    public Value getMemberUnCloned(final Value index) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get member with index '" + index + "' from operand;" +
            " '" + this + "' is not a collection value or direct access is unsupported for this type."
        );
    }

    public Value getMembers(final Value low, final Value high) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get member between index '" + low + "' and '" + high + "' from operand;" +
            " '" + this + "' is not a collection value or ranges are unsupported for this type."
        );
    }

    public Value join(final Value separator) throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' not a collection value."
        );
    }

    public Value lastMember() throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get last member from operand; '" + this + "' is not a collection value."
        );
    }

    public Value maximumMember() throws SetlException {
        throw new IncompatibleTypeException(
            "Argument of 'max(" + this + "') is not a collection value."
        );
    }

    public Value minimumMember() throws SetlException {
        throw new IncompatibleTypeException(
            "Argument of 'min(" + this + "') is not a collection value."
        );
    }

    public Value multiplyMembers(final Value neutral) throws SetlException {
        throw new IncompatibleTypeException(
            "Right-hand-side of '*/ " + this + "' is not a collection value."
        );
    }

    public Value nextPermutation() throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a list or string."
        );
    }

    public SetlSet permutations() throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a collection value."
        );
    }

    public SetlSet powerSet() throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a set."
        );
    }

    public Value randomMember() throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a collection value."
        );
    }

    public SetlSet range() throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a set."
        );
    }

    public void removeMember(final Value element) throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Can not remove '" + element + "' from operand; '" + this + "' is not a collection value."
        );
    }

    public void removeFirstMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Can not remove first member from operand; '" + this + "' is not a collection value."
        );
    }

    public void removeLastMember() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Can not remove last member from operand; '" + this + "' is not a collection value."
        );
    }

    public Value reverse() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a list or string."
        );
    }

    public void setMember(final Value index, final Value v) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not set member with index '" + index + "' from operand;" +
            " '" + this + "' is not a collection value or direct access is unsupported for this type."
        );
    }

    public Value shuffle() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a list or string."
        );
    }

    public int size() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Operand of '#" + this + "' is not a collection value."
        );
    }

    public Value sort() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a list or string."
        );
    }

    public SetlList split(final Value pattern) throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a string."
        );
    }

    public Value sumMembers(final Value neutral) throws SetlException {
        throw new IncompatibleTypeException(
            "Right-hand-side of '+/ " + this + "' is not a collection value."
        );
    }

    /* function call */

    public Value call(final List<Expr> args) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not perform call with arguments '" + args + "' on this operand-type; '" + this + "' is not a procedure."
        );
    }

    /* string and char operations */

    public abstract void appendString(final StringBuilder sb, final int tabs);

    public void appendUnquotedString(final StringBuilder sb, final int tabs) {
        appendString(sb, tabs);
    }

    public void canonical(final StringBuilder sb) {
        appendString(sb, 0);
    }

    public SetlString charConvert() throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a number between 0 and 255."
        );
    }

    public String getUnquotedString() {
        return toString();
    }

    public SetlString str() {
        final StringBuilder sb = new StringBuilder();
        appendString(sb, 0);
        return SetlString.newSetlStringFromSB(sb);
    }

    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        appendString(sb, 0);
        return sb.toString();
    }

    /* term operations */

    public MatchResult matchesTerm(final Value other) throws IncompatibleTypeException {
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
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real < SetlString
     * < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public abstract int compareTo(final Value v);

    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Value) {
            return this.compareTo((Value) o) == 0;
        } else {
            return false;
        }
    }

    public abstract int hashCode();

    public final SetlBoolean isEqual(final Value other) {
        return SetlBoolean.get(this.equals(other));
    }

    /* this comparison is different than `this.compareTo(other) < 0' and should
       throw errors on seemingly incomparable types like `5 < TRUE'            */
    public SetlBoolean isLessThan(final Value other) throws SetlException {
        throw new UndefinedOperationException("'" + this + " < " + other + "' is undefined.");
    }
}


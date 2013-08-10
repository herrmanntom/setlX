package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NotAnIntegerException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Base class for all value-types used in SetlX.
 */
public abstract class Value extends CodeFragment implements Comparable<Value> {

    @Override
    public abstract Value clone();

    @Override
    public void collectVariablesAndOptimize (
      final List<String> boundVariables,
      final List<String> unboundVariables,
      final List<String> usedVariables
    ) {
        /* nothing to collect or optimize (for most, but not all, values) */
    }

    /* Boolean operations */

    public Value conjunction(final State state, final Expr other) throws SetlException {
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this + " && " + other + "' is not a Boolean value."
        );
    }

    public Value disjunction(final State state, final Expr other) throws SetlException {
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this + " || " + other + "' is not a Boolean value."
        );
    }

    public Value implication(final State state, final Expr other) throws SetlException {
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this + " => " + other + "' is not a Boolean value."
        );
    }

    public Value not(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand of '!" + this + "' is not a Boolean value."
        );
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isBoolean() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isConstructor() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isError() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isTop() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isNumber() {
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

    public SetlBoolean isObject() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isProcedure() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isDouble() {
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

    public Value toInteger(final State state) throws SetlException {
        return Om.OM;
    }

    public Value toRational(final State state) throws SetlException {
        return Om.OM;
    }

    public Value toDouble(final State state) throws SetlException {
        return Om.OM;
    }

    /* native type checks */

    /**
     * Check if this value is equivalent to a native Java double AND can be
     * converted without throwing any kind of exception at runtime.
     *
     * @return True if this type is can be converted.
     */
    public boolean jDoubleConvertable() {
        return false;
    }
    /**
     * Check if this value is equivalent to a native Java integer AND can be
     * converted without throwing any kind of exception at runtime.
     *
     * @return True if this type is can be converted.
     */
    public boolean jIntConvertable() {
        return false;
    }

    /* native type conversions */

    /**
     * Get the native Java double equivalent of this value.
     * This method does NOT convert non-double equivalent types into double!
     *
     * If jDoubleConvertable() equals true for this value, then it must be
     * impossible for this method to fail or throw an exception of any kind.
     *
     * @return                           Equivalent double of this value.
     * @throws IncompatibleTypeException if this value cannot be converted into a double.
     * @throws NumberToLargeException    if this value is too large or to small to be converted.
     */
    public double jDoubleValue() throws IncompatibleTypeException, NumberToLargeException {
        throw new IncompatibleTypeException(
            "'" + this + "' is not a double."
        );
    }

    /**
     * Get the native Java integer equivalent of this value.
     * This method does NOT convert non-integer equivalent types into integer!
     *
     * If jIntConvertable() equals true for this value, then it must be
     * impossible for this method to fail or throw an exception of any kind.
     *
     * @return                           Equivalent int of this value.
     * @throws IncompatibleTypeException if this value cannot be converted into an integer.
     * @throws NumberToLargeException    if this value is too large or to small to be converted.
     */
    public int jIntValue() throws IncompatibleTypeException, NotAnIntegerException, NumberToLargeException {
        throw new IncompatibleTypeException(
            "'" + this + "' is not an integer."
        );
    }

    /**
     * Convert this value into a native Java double.
     *
     * @return               Equivalent double of this value.
     * @throws SetlException if this value cannot be converted.
     */
    public double toJDoubleValue(final State state) throws SetlException {
        final Value real = this.toDouble(state);
        if (real != Om.OM) {
            return real.jDoubleValue();
        } else {
            throw new IncompatibleTypeException(
                "'" + this + "' is not convertable to a double."
            );
        }
    }

    /**
     * Convert this value into an native Java integer.
     *
     * @return               Equivalent int of this value.
     * @throws SetlException if this value cannot be converted.
     */
    public final int toJIntValue(final State state) throws SetlException {
        final Value integer = this.toInteger(state);
        if (integer != Om.OM) {
            return integer.jIntValue();
        } else {
            throw new IncompatibleTypeException(
                "'" + this + "' is not convertable to integer."
            );
        }
    }

    /* arithmetic operations */

    public Value absoluteValue(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a number or character."
        );
    }

    public Value ceil(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a number."
        );
    }

    /**
     * Compute the difference between this value and another.
     *
     * @param state          Current state of the running setlX program.
     * @param subtrahend     Value to subtract.
     * @return               Difference of this and subtrahend.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this + " - " + subtrahend + "' is undefined."
        );
    }

    public Value differenceAssign(final State state, final Value subtrahend) throws SetlException {
        return difference(state, subtrahend);
    }

    public Value factorial(final State state) throws SetlException {
        throw new UndefinedOperationException(
            "'" + this + "!' is undefined."
        );
    }

    public void fillCollectionWithinRange(final State state, final Value step, final Value stop, final CollectionValue collection) throws SetlException {
        throw new IncompatibleTypeException(
            "Start argument '" + this + "' is not a number."
        );
    }

    public Value floor(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a number."
        );
    }

    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof Term) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this + " \\ " + divisor + "' is undefined."
        );
    }

    public Value integerDivisionAssign(final State state, final Value divisor) throws SetlException {
        return integerDivision(state, divisor);
    }

    public final Value maximum(final State state, final Value other) throws SetlException {
        if(other != Om.OM && this.isLessThan(state, other) == SetlBoolean.TRUE){
            return other.clone();
        } else {
            return this.clone();
        }
    }

    public final Value minimum(final State state, final Value other) throws SetlException {
        if(other == Om.OM || other.isLessThan(state, this) == SetlBoolean.TRUE){
            return other.clone();
        } else {
            return this.clone();
        }
    }

    public Value minus(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a number."
        );
    }

    public Value modulo(final State state, final Value modulo) throws SetlException {
        if (modulo instanceof Term) {
            return ((Term) modulo).moduloFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this + " % " + modulo + "' is undefined."
        );
    }

    public Value moduloAssign(final State state, final Value modulo) throws SetlException {
        return modulo(state, modulo);
    }

    public Value power(final State state, final Value exponent) throws SetlException {
        if (exponent instanceof Term) {
            return ((Term) exponent).powerFlipped(state, this);
        }
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this + " ** " + exponent + "' is not a number."
        );
    }

    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this + " * " + multiplier + "' is undefined."
        );
    }

    public Value productAssign(final State state, final Value multiplier) throws SetlException {
        return product(state, multiplier);
    }

    /**
     * Divide this value by another.
     *
     * @param state          Current state of the running setlX program.
     * @param divisor        Value to divide by.
     * @return               Division of this and divisor.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value quotient(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof Term) {
            return ((Term) divisor).quotientFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this + " / " + divisor + "' is undefined."
        );
    }

    public Value quotientAssign(final State state, final Value divisor) throws SetlException {
        return quotient(state, divisor);
    }

    public Value rnd(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a rational, integer or collection value."
        );
    }

    public Value rnd(final State state, final Value numberOfChoices) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a rational or integer."
        );
    }

    public Value round(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a number."
        );
    }

    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString && this != Om.OM) {
            return ((SetlString) summand).sumFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this + " + " + summand + "' is undefined."
        );
    }

    public Value sumAssign(final State state, final Value summand) throws SetlException {
        return sum(state, summand);
    }

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    public void addMember(final State state, final Value element) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not add '" + element + "' into operand; '" + this + "' is not a collection value."
        );
    }

    public Value arbitraryMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a collection value."
        );
    }

    public Value arguments(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a term."
        );
    }

    public Value cardinality(final State state) throws SetlException {
        return Rational.valueOf(this.size());
    }

    public Value cartesianProduct(final State state, final Value other) throws SetlException {
        if (other instanceof Term) {
            return ((Term) other).cartesianProductFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this + " >< " + other + "' is undefined."
        );
    }

    public Value collectionAccess(final State state, final List<Value> args) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not access elements using the arguments '" + args + "' on this operand-type;" +
            " '" + this + "' is not a collection value."
        );
    }

    public Value collectionAccessUnCloned(final State state, final List<Value> args) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not access elements using the arguments '" + args + "' on this operand-type;" +
            " '" + this + "' is not a collection value."
        );
    }

    // returns a set of all pairs which first element matches arg
    public Value collectMap(final State state, final Value arg) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not collect values of members matching the key '" + arg + "' on this operand-type;" +
            " '" + this + "' is not a map."
        );
    }

    public SetlBoolean containsMember(final State state, final Value element) throws SetlException {
        throw new IncompatibleTypeException(
            "Right-hand-side of '" + element  + " in " + this + "' is not a collection value."
        );
    }

    public Value domain(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a set."
        );
    }

    public Value firstMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get first member from operand; '" + this + "' is not a collection value."
        );
    }

    public Value functionalCharacter(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a term."
        );
    }

    public Value getMember(final State state, final Value index) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get member with index '" + index + "' from operand;" +
            " '" + this + "' is not a collection value or direct access is unsupported for this type."
        );
    }

    public Value getMemberUnCloned(final State state, final Value index) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get member with index '" + index + "' from operand;" +
            " '" + this + "' is not a collection value or direct access is unsupported for this type."
        );
    }

    public Value getMembers(final State state, final Value low, final Value high) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get member between index '" + low + "' and '" + high + "' from operand;" +
            " '" + this + "' is not a collection value or ranges are unsupported for this type."
        );
    }

    public Value join(final State state, final Value separator) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' not a collection value."
        );
    }

    public Value lastMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get last member from operand; '" + this + "' is not a collection value."
        );
    }

    /**
     * Find largest member value in this (collection-) value.
     *
     * Note: This function must always return a valid value (i.e. not om) and
     *       satisfy the following equation for all possible m and n:
     *
     *       max(m + n) == max([max(m), max(n)])
     *
     *       Especially when m is {} or [] and n is not.
     *
     * @param state          Current state of the running setlX program.
     * @return               Maximum of the members contained in this value.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value maximumMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument of 'max(" + this + "') is not a collection value."
        );
    }

    /**
     * Find smallest member value in this (collection-) value.
     *
     * Note: This function must always return a valid value (i.e. not om) and
     *       satisfy the following equation for all possible m and n:
     *
     *       min(m + n) == min([min(m), min(n)])
     *
     *       Especially when m is {} or [] and n is not.
     *
     * @param state          Current state of the running setlX program.
     * @return               Minimum of the members contained in this value.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value minimumMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument of 'min(" + this + "') is not a collection value."
        );
    }

    public Value productOfMembers(final State state, final Value neutral) throws SetlException {
        throw new IncompatibleTypeException(
            "Right-hand-side of '*/ " + this + "' is not a collection value."
        );
    }

    public Value nextPermutation(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a list or string."
        );
    }

    public Value permutations(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a collection value."
        );
    }

    public Value powerSet(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a set."
        );
    }

    public Value range(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a set."
        );
    }

    public void removeMember(final Value element) throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Can not remove '" + element + "' from operand; '" + this + "' is not a collection value."
        );
    }

    public Value removeFirstMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not remove first member from operand; '" + this + "' is not a collection value."
        );
    }

    public Value removeLastMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not remove last member from operand; '" + this + "' is not a collection value."
        );
    }

    public Value reverse(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a list or string."
        );
    }

    public void setMember(final State state, final Value index, final Value v) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not set member with index '" + index + "' from operand;" +
            " '" + this + "' is not a collection value or direct access is unsupported for this type."
        );
    }

    public Value shuffle(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a list or string."
        );
    }

    public int size() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Operand of '#" + this + "' is not a collection value."
        );
    }

    public Value sort(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a list or string."
        );
    }

    public Value split(final State state, final Value pattern) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this + "' is not a string."
        );
    }

    public Value sumOfMembers(final State state, final Value neutral) throws SetlException {
        throw new IncompatibleTypeException(
            "Right-hand-side of '+/ " + this + "' is not a collection value."
        );
    }

    /* features of objects */

    public Value getObjectMember(final State state, final String variable) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get member '" + variable + "' from operand; '" + this + "' is not an object."
        );
    }

    public Value getObjectMemberUnCloned(final State state, final String variable) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get member '" + variable + "' from operand; '" + this + "' is not an object."
        );
    }

    public void setObjectMember(final State state, final String variable, final Value value, final String context) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not add member '" + variable + "' into operand; '" + this + "' is not an object."
        );
    }

    /* function call */

    public Value call(final State state, final List<Expr> args) throws SetlException {
        String param = args.toString();
        param = param.substring(1, param.length() - 1);
        throw new IncompatibleTypeException(
            "Can not perform call with arguments '" + param + "' on this operand-type; '" + this + "' is not a procedure."
        );
    }

    /* string and char operations */

    @Override
    public abstract void appendString(final State state, final StringBuilder sb, final int tabs);

    public void appendUnquotedString(final State state, final StringBuilder sb, final int tabs) {
        appendString(state, sb, tabs);
    }

    public void canonical(final State state, final StringBuilder sb) {
        appendString(state, sb, 0);
    }

    public final String canonical() {
        final State         bubble = new State();
        final StringBuilder sb     = new StringBuilder();
        canonical(bubble, sb);
        return sb.toString();
    }

    public Value charConvert(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this + "' is not a number between 0 and 255."
        );
    }

    public String getUnquotedString() {
        return toString();
    }

    public SetlString str(final State state) throws SetlException {
        final StringBuilder sb = new StringBuilder();
        appendString(state, sb, 0);
        return SetlString.newSetlStringFromSB(sb);
    }

    /* term operations */

    public MatchResult matchesTerm(final State state, final Value other) throws SetlException {
        if (other == IgnoreDummy.ID || this.equalTo(other)) {
            return new MatchResult(true);
        } else {
            return new MatchResult(false);
        }
    }

    @Override
    public Value toTerm(final State state) {
        return this.clone();
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     */
    @Override
    public abstract int     compareTo(final Value v);

    /**
     * In order to compare "incomparable" values, e.g. of different types, the
     * following (mostly arbitrary) order is established and used in compareTo():
     *
     * SetlError.BOTTOM <= SetlError < Om < SetlBoolean < Rational & SetlDouble
     * < SetlString < SetlSet < SetlList < Term < ProcedureDefinition
     * < SetlObject < ConstructorDefinition < Top
     *
     * This ranking is necessary to allow sets and lists of different types.
     *
     * @return Number representing the order of this type in compareTo().
     */
    protected abstract int  compareToOrdering();

    public abstract boolean equalTo  (final Value v);

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Value) {
            return this.equalTo((Value) o);
        } else {
            return false;
        }
    }

    @Override
    public abstract int hashCode();

    public SetlBoolean isEqualTo(final State state, final Value other) throws SetlException {
        return SetlBoolean.valueOf(this.equalTo(other));
    }

    /* this comparison is different than `this.compareTo(other) < 0' and should
       throw errors on seemingly incomparable types like `5 < TRUE'            */
    public SetlBoolean isLessThan(final State state, final Value other) throws SetlException {
        throw new UndefinedOperationException("'" + this + " < " + other + "' is undefined.");
    }
}


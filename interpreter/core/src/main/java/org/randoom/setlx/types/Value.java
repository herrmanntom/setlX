package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NotAnIntegerException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.List;

/**
 * Base class for all value-types used in SetlX.
 */
public abstract class Value extends CodeFragment {

    @Override
    public abstract Value clone();

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        /* nothing to collect or optimize (for most, but not all, values) */
        return true;
    }

    /* Boolean operations */

    /**
     * Compute the conjunction between this value and another.
     *
     * @param state          Current state of the running setlX program.
     * @param other          Expression to evaluate lazily.
     * @return               Difference of this and subtrahend.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value conjunction(final State state, final OperatorExpression other) throws SetlException {
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this.toString(state) + " && " + other.toString(state) + "' is not a Boolean value."
        );
    }

    /**
     * Compute the disjunction between this value and another.
     *
     * @param state          Current state of the running setlX program.
     * @param other          Expression to evaluate lazily.
     * @return               Difference of this and subtrahend.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value disjunction(final State state, final OperatorExpression other) throws SetlException {
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this.toString(state) + " || " + other.toString(state) + "' is not a Boolean value."
        );
    }

    /**
     * Compute the implication between this value and another.
     *
     * @param state          Current state of the running setlX program.
     * @param other          Expression to evaluate lazily.
     * @return               Difference of this and subtrahend.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value implication(final State state, final OperatorExpression other) throws SetlException {
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this.toString(state) + " => " + other.toString(state) + "' is not a Boolean value."
        );
    }

    /**
     * Compute the negation of this value.
     *
     * @param state          Current state of the running setlX program.
     * @return               Difference of this and subtrahend.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value not(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand of '!" + this.toString(state) + "' is not a Boolean value."
        );
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isBoolean() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isClass() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isError() {
        return SetlBoolean.FALSE;
    }

    public SetlBoolean isNumber() {
        return SetlBoolean.FALSE;
    }

    /**
     * Test if this value represents INFINITY or -INFINITY.
     *
     * @return True if this value represents infinity.
     */
    public SetlBoolean isInfinite() {
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

    public SetlBoolean isMatrix() {
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

    public SetlBoolean isVector() {
        return SetlBoolean.FALSE;
    }

    /* type conversions */

    /**
     * Convert this value into an integer setlX rational.
     *
     * @param state          Current state of the running setlX program.
     * @return               Equivalent rational of this value, or om.
     * @throws SetlException if this value cannot be converted.
     */
    public Value toInteger(final State state) throws SetlException {
        return Om.OM;
    }

    /**
     * Convert this value into a setlX rational.
     *
     * @param state          Current state of the running setlX program.
     * @return               Equivalent rational of this value, or om.
     * @throws SetlException if this value cannot be converted.
     */
    public Value toRational(final State state) throws SetlException {
        return Om.OM;
    }

    /**
     * Convert this value into a setlX double.
     *
     * @param state          Current state of the running setlX program.
     * @return               Equivalent double of this value, or om.
     * @throws SetlException if this value cannot be converted.
     */
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
    public boolean jDoubleConvertible() {
        return false;
    }
    /**
     * Check if this value is equivalent to a native Java integer AND can be
     * converted without throwing any kind of exception at runtime.
     *
     * @return True if this type is can be converted.
     */
    public boolean jIntConvertible() {
        return false;
    }

    /* native type conversions */

    /**
     * Get the native Java double equivalent of this value.
     * This method does NOT convert non-double equivalent types into double!
     *
     * If jDoubleConvertible() equals true for this value, then it must be
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
     * If jIntConvertible() equals true for this value, then it must be
     * impossible for this method to fail or throw an exception of any kind.
     *
     * @return                           Equivalent int of this value.
     * @throws IncompatibleTypeException if this value cannot be converted into an integer.
     * @throws NotAnIntegerException     if this value is not an integer.
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
     * @param state          Current state of the running setlX program.
     * @return               Equivalent double of this value.
     * @throws SetlException if this value cannot be converted.
     */
    public double toJDoubleValue(final State state) throws SetlException {
        final Value real = this.toDouble(state);
        if (real != Om.OM) {
            return real.jDoubleValue();
        } else {
            throw new IncompatibleTypeException(
                "'" + this.toString(state) + "' is not convertable to a double."
            );
        }
    }

    /**
     * Convert this value into an native Java integer.
     *
     * @param state          Current state of the running setlX program.
     * @return               Equivalent int of this value.
     * @throws SetlException if this value cannot be converted.
     */
    public final int toJIntValue(final State state) throws SetlException {
        final Value integer = this.toInteger(state);
        if (integer != Om.OM) {
            return integer.jIntValue();
        } else {
            throw new IncompatibleTypeException(
                "'" + this.toString(state) + "' is not convertable to integer."
            );
        }
    }

    /* arithmetic operations */

    public Value absoluteValue(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a number or character."
        );
    }

    public Value ceil(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this.toString(state) + "' is not a number."
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
        if (subtrahend.getClass() == Term.class) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this.toString(state) + " - " + subtrahend.toString(state) + "' is undefined."
        );
    }

    public Value differenceAssign(final State state, final Value subtrahend) throws SetlException {
        return difference(state, subtrahend);
    }

    public Value factorial(final State state) throws SetlException {
        throw new UndefinedOperationException(
            "'" + this.toString(state) + "!' is undefined."
        );
    }

    public void fillCollectionWithinRange(final State state, final Value step, final Value stop, final CollectionValue collection) throws SetlException {
        throw new IncompatibleTypeException(
            "Start argument '" + this.toString(state) + "' is not a number."
        );
    }

    public Value floor(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this.toString(state) + "' is not a number."
        );
    }

    /**
     * Compute the integer division between this value and another.
     *
     * @param state          Current state of the running setlX program.
     * @param divisor        Value to divide by.
     * @return               Difference of this and subtrahend.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor.getClass() == Term.class) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this.toString(state) + " \\ " + divisor.toString(state) + "' is undefined."
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
            "Operand '" + this.toString(state) + "' is not a number."
        );
    }

    /**
     * Compute the modulo between this value and another.
     *
     * @param state          Current state of the running setlX program.
     * @param modulo         Value to divide by.
     * @return               Modulo of this and modulo.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value modulo(final State state, final Value modulo) throws SetlException {
        if (modulo.getClass() == Term.class) {
            return ((Term) modulo).moduloFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this.toString(state) + " % " + modulo.toString(state) + "' is undefined."
        );
    }

    public Value moduloAssign(final State state, final Value modulo) throws SetlException {
        return modulo(state, modulo);
    }

    /**
     * Raise this value to the power of another.
     *
     * @param state          Current state of the running setlX program.
     * @param exponent       Value to raise by.
     * @return               This raised by the power of exponent.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value power(final State state, final Value exponent) throws SetlException {
        if (exponent.getClass() == Term.class) {
            return ((Term) exponent).powerFlipped(state, this);
        }
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this.toString(state) + " ** " + exponent.toString(state) + "' is not a number."
        );
    }

    /**
     * Product of this value and another.
     *
     * @param state          Current state of the running setlX program.
     * @param multiplier     Value to multiply by.
     * @return               Product of this value and multiplier.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier.getClass() == Term.class) {
            return ((Term) multiplier).productFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this.toString(state) + " * " + multiplier.toString(state) + "' is undefined."
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
        if (divisor.getClass() == Term.class) {
            return ((Term) divisor).quotientFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this.toString(state) + " / " + divisor.toString(state) + "' is undefined."
        );
    }

    public Value quotientAssign(final State state, final Value divisor) throws SetlException {
        return quotient(state, divisor);
    }

    public Value rnd(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this.toString(state) + "' is not a rational, integer or collection value."
        );
    }

    public Value rnd(final State state, final Value numberOfChoices) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this.toString(state) + "' is not a rational or integer."
        );
    }

    public Value round(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this.toString(state) + "' is not a number."
        );
    }

    /**
     * Add this value and another.
     *
     * @param state          Current state of the running setlX program.
     * @param summand        Value to add.
     * @return               Sum of this and summand.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand.getClass() == Term.class) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (this != Om.OM && summand.getClass() == SetlString.class) {
            return ((SetlString) summand).sumFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this.toString(state) + " + " + summand.toString(state) + "' is undefined."
        );
    }

    public Value sumAssign(final State state, final Value summand) throws SetlException {
        return sum(state, summand);
    }

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    public void addMember(final State state, final Value element) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not add '" + element.toString(state) + "' into operand; '" + this.toString(state) + "' is not a collection value."
        );
    }

    public Value arbitraryMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a collection value."
        );
    }

    public Value arguments(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a term."
        );
    }

    public Value cardinality(final State state) throws SetlException {
        return Rational.valueOf(this.size());
    }

    /**
     * Implementation of the >< operator. Computes the cartesianProduct of two sets
     * and combines two lists into one.
     *
     * @param state          Current state of the running setlX program.
     * @param other          Second value.
     * @return               Cartesian product of this and other.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value cartesianProduct(final State state, final Value other) throws SetlException {
        if (other.getClass() == Term.class) {
            return ((Term) other).cartesianProductFlipped(state, this);
        }
        throw new UndefinedOperationException(
            "'" + this.toString(state) + " >< " + other.toString(state) + "' is undefined."
        );
    }

    public Value collectionAccess(final State state, final List<Value> args) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not access elements using the arguments '" + args + "' on this operand-type;" +
            " '" + this.toString(state) + "' is not a collection value."
        );
    }

    public Value collectionAccessUnCloned(final State state, final List<Value> args) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not access elements using the arguments '" + args + "' on this operand-type;" +
            " '" + this.toString(state) + "' is not a collection value."
        );
    }

    // returns a set of all pairs which first element matches arg
    public Value collectMap(final State state, final Value arg) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not collect values of members matching the key '" + arg.toString(state) + "' on this operand-type;" +
            " '" + this.toString(state) + "' is not a map."
        );
    }

    /**
     * Test if this value contains the specified element.
     *
     * @param state          Current state of the running setlX program.
     * @param element        Element to search for.
     * @return               True if the element is contained, false otherwise.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public SetlBoolean containsMember(final State state, final Value element) throws SetlException {
        throw new IncompatibleTypeException(
            "Right-hand-side of '" + element.toString(state)  + " in " + this.toString(state) + "' is not a collection value."
        );
    }

    public Value domain(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a set."
        );
    }

    /**
     * Get the first member of this value.
     *
     * @param state          Current state of the running setlX program.
     * @return               First member of this value.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value firstMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get first member from operand; '" + this.toString(state) + "' is not a collection value."
        );
    }

    /**
     * Get the functional character (of a term).
     *
     * @param state          Current state of the running setlX program.
     * @return               functional character of this value.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value functionalCharacter(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a term."
        );
    }

    /**
     * Get a specified member of this value.
     *
     * @param state          Current state of the running setlX program.
     * @param index          Index of the member to get.
     * @return               Member of this value at the specified index.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value getMember(final State state, final Value index) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get member with index '" + index.toString(state) + "' from operand;" +
            " '" + this.toString(state) + "' is not a collection value or direct access is unsupported for this type."
        );
    }

    public Value join(final State state, final Value separator) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this.toString(state) + "' not a collection value."
        );
    }

    /**
     * Get the last member of this value.
     *
     * @param state          Current state of the running setlX program.
     * @return               last member of this value.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value lastMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get last member from operand; '" + this.toString(state) + "' is not a collection value."
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
            "Argument of 'max(" + this.toString(state) + "') is not a collection value."
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
            "Argument of 'min(" + this.toString(state) + "') is not a collection value."
        );
    }

    public Value productOfMembers(final State state, final Value neutral) throws SetlException {
        throw new IncompatibleTypeException(
            "Right-hand-side of '*/ " + this.toString(state) + "' is not a collection value."
        );
    }

    public Value nextPermutation(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a list or string."
        );
    }

    public Value permutations(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a collection value."
        );
    }

    public Value powerSet(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a set."
        );
    }

    public Value range(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a set."
        );
    }

    /**
     * Remove the first member of this value.
     *
     * @param state          Current state of the running setlX program.
     * @return               First member of this value.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value removeFirstMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not remove first member from operand; '" + this.toString(state) + "' is not a collection value."
        );
    }

    /**
     * Remove the last member of this value.
     *
     * @param state          Current state of the running setlX program.
     * @return               Last member of this value.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value removeLastMember(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not remove last member from operand; '" + this.toString(state) + "' is not a collection value."
        );
    }

    public Value reverse(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a list or string."
        );
    }

    /**
     * Set a specified member of this collection value.
     *
     * @param state          Current state of the running setlX program.
     * @param index          The of the member to set. Note: Index starts with 1, not 0.
     * @param value          The value to set the member to.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public void setMember(final State state, final Value index, final Value value) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not set member with index '" + index.toString(state) + "' from operand;" +
            " '" + this.toString(state) + "' is not a collection value or direct access is unsupported for this type."
        );
    }

    public Value shuffle(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this.toString(state) + "' is not a list or string."
        );
    }

    public int size() throws IncompatibleTypeException {
        throw new IncompatibleTypeException(
            "Operand of '#" + this + "' is not a collection value."
        );
    }

    public Value sort(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this.toString(state) + "' is not a list or string."
        );
    }

    public Value split(final State state, final Value pattern) throws SetlException {
        throw new IncompatibleTypeException(
            "Argument '" + this.toString(state) + "' is not a string."
        );
    }

    public Value sumOfMembers(final State state, final Value neutral) throws SetlException {
        throw new IncompatibleTypeException(
            "Right-hand-side of '+/ " + this.toString(state) + "' is not a collection value."
        );
    }

    /* features of objects */

    public Value getObjectMemberUnCloned(final State state, final String variable) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not get member '" + variable + "' from operand; '" + this.toString(state) + "' is not an object."
        );
    }

    public void setObjectMember(final State state, final String variable, final Value value, final String context) throws SetlException {
        throw new IncompatibleTypeException(
            "Can not add member '" + variable + "' into operand; '" + this.toString(state) + "' is not an object."
        );
    }

    /* function call */

    /**
     * Implementation of the function call.
     *
     * @param state          Current state of the running setlX program.
     * @param argumentValues Values of arguments of the function call.
     * @param arguments      Arguments of the function call.
     * @param listValue      Value of list argument of the function call.
     * @param listArg        List argument of the function call.
     * @return               Return value of the call.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value call(final State state, List<Value> argumentValues, final FragmentList<OperatorExpression> arguments, final Value listValue, final OperatorExpression listArg) throws SetlException {
        final StringBuilder error = new StringBuilder();
        error.append("Can not perform call with arguments '");
        arguments.appendString(state, error);
        if (listValue != null) {
            if (! arguments.isEmpty()) {
                error.append(", ");
            }
            error.append("*");
            listArg.appendString(state, error, 0);
        }
        error.append("' on this operand-type; '");
        this.appendString(state, error, 0);
        error.append("' is not a procedure.");
        throw new IncompatibleTypeException(error.toString());
    }

    /* string and char operations */

    @Override
    public abstract void appendString(final State state, final StringBuilder sb, final int tabs);

    /**
     * Appends a string representation of this value to the given StringBuilder
     * object, but suppresses quotes when appending strings.
     *
     * @param state Current state of the running setlX program.
     * @param sb    StringBuilder to append to.
     * @param tabs  Number of tabs to use as indentation for statements.
     */
    public void appendUnquotedString(final State state, final StringBuilder sb, final int tabs) {
        appendString(state, sb, tabs);
    }

    /**
     * Appends an uninterpreted string representation of this value to the given
     * StringBuilder object.
     *
     * @param state Current state of the running setlX program.
     * @param sb    StringBuilder to append to.
     */
    public void canonical(final State state, final StringBuilder sb) {
        appendString(state, sb, 0);
    }

    /**
     * Convert this value into a single ASCII character.
     *
     * @param state          Current state of the running setlX program.
     * @return               Character representation of this value.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value charConvert(final State state) throws SetlException {
        throw new IncompatibleTypeException(
            "Operand '" + this.toString(state) + "' is not a number between 0 and 255."
        );
    }

    /**
     * Get a string representation of this value, but suppresses quotes when appending strings.
     *
     * @param state Current state of the running setlX program.
     * @return      String representation of this value.
     */
     public String getUnquotedString(final State state) {
         final StringBuilder sb = new StringBuilder();
         appendString(state, sb, 0);
         return sb.toString();
     }

    /**
     * Get an string representation of this value as SetlString.
     *
     * @param state          Current state of the running setlX program.
     * @return               SetlString representation of this value.
     * @throws SetlException Thrown in case some (user-)error occurs.
     */
    public SetlString str(final State state) throws SetlException {
        final StringBuilder sb = new StringBuilder();
        appendString(state, sb, 0);
        return SetlString.newSetlStringFromSB(sb);
    }

    /* term operations */

    /**
     * Match some value against a this value.
     *
     * @param state          Current state of the running setlX program.
     * @param other          Value to match.
     * @return               Result of the match.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public MatchResult matchesTerm(final State state, final Value other) throws SetlException {
        if (other == IgnoreDummy.ID || this.equalTo(other)) {
            return new MatchResult(true);
        } else {
            return new MatchResult(false);
        }
    }

    @Override
    public Value toTerm(final State state) throws SetlException {
        return this.clone();
    }

    /**
     * Create a Value from a (term-) value representing such a value
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    (Term-) value to convert.
     * @return                         New Value.
     * @throws TermConversionException in case the term is malformed.
     */
    public static Value createFromTerm(State state, Value value) throws TermConversionException {
        if (value.getClass() == Term.class) {
            final Term   term                = (Term) value;
            final String functionalCharacter = term.getFunctionalCharacter();
            if (TermUtilities.isInternalFunctionalCharacter(functionalCharacter)) {
                // special cases
                if (functionalCharacter.equals(CachedProcedure.getFunctionalCharacter())) {
                    return CachedProcedure.termToValue(state, term);
                } else if (functionalCharacter.equals(Closure.getFunctionalCharacter())) {
                    return Closure.termToValue(state, term);
                } else if (functionalCharacter.equals(LambdaClosure.getFunctionalCharacter())) {
                    return LambdaClosure.termToValue(state, term);
                } else if (functionalCharacter.equals(LambdaProcedure.getFunctionalCharacter())) {
                    return LambdaProcedure.termToValue(state, term);
                } else if (functionalCharacter.equals(Procedure.getFunctionalCharacter())) {
                    return Procedure.termToValue(state, term);
                } else if (functionalCharacter.equals(SetlClass.getFunctionalCharacter())) {
                    return SetlClass.termToValue(state, term);
                } else if (functionalCharacter.equals(SetlObject.getFunctionalCharacter())) {
                    return SetlObject.termToValue(state, term);
                } else if (functionalCharacter.equals(Om.getFunctionalCharacter())) {
                    return Om.OM;
                }
            }
        }
        // `value' is in fact a (more or less) simple value
        return value;
    }

    /* comparisons */

    /**
     * Test if two Values are equal.
     * This operation is much faster as ( compareTo(other) == 0 ) for certain types.
     *
     * @param other Other value to compare to `this'
     * @return      True if `this' equals `other', false otherwise.
     */
    public abstract boolean equalTo (final Object other);

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public final boolean equals(final Object o) {
        return this.equalTo(o);
    }

    /**
     * Test if two Values are equal.
     * This operation is much faster as ( compareTo(other) == 0 ) for certain types.
     *
     * @param state          Current state of the running setlX program.
     * @param other          Other value to compare to `this'
     * @return               SetlBoolean.TRUE if `this' equals `other', SetlBoolean.FALSE otherwise.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public SetlBoolean isEqualTo(final State state, final Value other) throws SetlException {
        return SetlBoolean.valueOf(this.equalTo(other));
    }

    /**
     * Test if the value of this is lower than the value of `other'.
     * This comparison is different than `this.compareTo(other) < 0' insofar as
     * it throw errors on seemingly incomparable types like `5 < TRUE'.
     *
     * @param state          Current state of the running setlX program.
     * @param other          Other value to compare to `this'
     * @return               SetlBoolean.TRUE if `this' is less than `other', SetlBoolean.FALSE otherwise.
     * @throws SetlException Thrown in case the types are incomparable.
     */
    public SetlBoolean isLessThan(final State state, final Value other) throws SetlException {
        throw new UndefinedOperationException("'" + this + " < " + other + "' is undefined.");
    }
}


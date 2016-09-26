package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operators.*;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.Iterator;
import java.util.List;

/**
 *    This class implements terms in the form of
 *
 *        f(e1, e2, ..., en)
*/
public class Term extends IndexedCollectionValue {
    // functional character of the term;    e.g. f
    private final String   functionalCharacter;
    // arguments in inner body of the term; e.g. e1, e2, ..., en
    private final SetlList body;

    /**
     * Create a new term.
     *
     * @param functionalCharacter Functional character of the new term.
     */
    public Term(final String functionalCharacter) {
        this(functionalCharacter, 4);
    }

    /**
     * Create a new term.
     *
     * @param functionalCharacter Functional character of the new term.
     * @param initialCapacity     Number of elements to store without resizing.
     */
    public Term(final String functionalCharacter, final int initialCapacity) {
        this(functionalCharacter, new SetlList(initialCapacity));
    }

    /**
     * Create a new term.
     *
     * @param functionalCharacter Functional character of the new term.
     * @param body                Body of the new term.
     */
    public Term(final String functionalCharacter, final SetlList body) {
        this.functionalCharacter = functionalCharacter;
        this.body                = body;
    }

    @Override
    public Term clone() {
        return new Term(functionalCharacter, body.clone());
    }

    @Override
    public Iterator<Value> iterator() {
        return body.iterator();
    }

    @Override
    public Iterator<Value> descendingIterator() {
        return body.descendingIterator();
    }

    /* Boolean operations */

    // viral operation
    @Override
    public Value conjunction(final State state, final OperatorExpression other) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                new Conjunction(other)
        ).toTerm(state);
    }

    /**
     * Compute the conjunction between another value and this.
     *
     * @param state Current state of the running setlX program.
     * @param other Expression to evaluate lazily.
     * @return      Conjunction of value and this.
     * @throws SetlException in case of (User-) Error.
     */
    public Value conjunctionFlipped(final State state, final Value other) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, other),
                new Conjunction(OperatorExpression.createFromTerm(state, this))
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value disjunction(final State state, final OperatorExpression other) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                new Disjunction(other)
        ).toTerm(state);
    }

    /**
     * Compute the disjunction between another value and this.
     *
     * @param state Current state of the running setlX program.
     * @param other Expression to evaluate lazily.
     * @return      Disjunction of value and this.
     * @throws SetlException in case of (User-) Error.
     */
    public Value disjunctionFlipped(final State state, final Value other) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, other),
                new Disjunction(OperatorExpression.createFromTerm(state, this))
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value implication(final State state, final OperatorExpression other) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                new Implication(other)
        ).toTerm(state);
    }

    /**
     * Compute the implication between another value and this.
     *
     * @param state Current state of the running setlX program.
     * @param other Expression to evaluate lazily.
     * @return      Implication of value and this.
     * @throws SetlException in case of (User-) Error.
     */
    public Value implicationFlipped(final State state, final Value other) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, other),
                new Implication(OperatorExpression.createFromTerm(state, this))
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value not(final State state) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                Not.N
        ).toTerm(state);
    }

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isTerm() {
        return SetlBoolean.TRUE;
    }

    /* arithmetic operations */

    // viral operation
    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                OperatorExpression.createFromTerm(state, subtrahend),
                Difference.D
        ).toTerm(state);
    }

    /**
     * Compute the difference between another value and this.
     *
     * @param state      Current state of the running setlX program.
     * @param subtrahend Value to subtract.
     * @return           Difference subtrahend and this.
     * @throws SetlException in case of (User-) Error.
     */
    public Value differenceFlipped(final State state, final Value subtrahend) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, subtrahend),
                OperatorExpression.createFromTerm(state, this),
                Difference.D
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value factorial(final State state) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                Factorial.F
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                OperatorExpression.createFromTerm(state, divisor),
                IntegerDivision.ID
        ).toTerm(state);
    }

    /**
     * Compute the integer division between another value and this.
     *
     * @param state   Current state of the running setlX program.
     * @param divisor Value to divide by.
     * @return        Integer division of divisor and this.
     * @throws SetlException in case of (User-) Error.
     */
    public Value integerDivisionFlipped(final State state, final Value divisor) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, divisor),
                OperatorExpression.createFromTerm(state, this),
                IntegerDivision.ID
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value minus(final State state) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                Minus.M
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value modulo(final State state, final Value modulo) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                OperatorExpression.createFromTerm(state, modulo),
                Modulo.M
        ).toTerm(state);
    }

    /**
     * Compute the modulo between another value and this.
     *
     * @param state   Current state of the running setlX program.
     * @param modulo  Value to divide by.
     * @return        Modulo of modulo and this.
     * @throws SetlException in case of (User-) Error.
     */
    public Value moduloFlipped(final State state, final Value modulo) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, modulo),
                OperatorExpression.createFromTerm(state, this),
                Modulo.M
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value power(final State state, final Value exponent) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                OperatorExpression.createFromTerm(state, exponent),
                Power.P
        ).toTerm(state);
    }

    /**
     * Compute the power between another value and this.
     *
     * @param state    Current state of the running setlX program.
     * @param exponent Value to raise by.
     * @return         Exponent raised by the power of this.
     * @throws SetlException in case of (User-) Error.
     */
    public Value powerFlipped(final State state, final Value exponent) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, exponent),
                OperatorExpression.createFromTerm(state, this),
                Power.P
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                OperatorExpression.createFromTerm(state, multiplier),
                Product.P
        ).toTerm(state);
    }

    /**
     * Compute the product between another value and this.
     *
     * @param state      Current state of the running setlX program.
     * @param multiplier Value to multiply by.
     * @return           Product of multiplier and this.
     * @throws SetlException in case of (User-) Error.
     */
    public Value productFlipped(final State state, final Value multiplier) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, multiplier),
                OperatorExpression.createFromTerm(state, this),
                Product.P
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value quotient(final State state, final Value divisor) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                OperatorExpression.createFromTerm(state, divisor),
                Quotient.Q
        ).toTerm(state);
    }

    /**
     * Divide another value by this.
     *
     * @param state      Current state of the running setlX program.
     * @param divisor    Value to divide by.
     * @return           Division of divisor and this.
     * @throws SetlException in case of (User-) Error.
     */
    public Value quotientFlipped(final State state, final Value divisor) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, divisor),
                OperatorExpression.createFromTerm(state, this),
                Quotient.Q
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                OperatorExpression.createFromTerm(state, summand),
                Sum.S
        ).toTerm(state);
    }

    /**
     * Add this value and another.
     *
     * @param state      Current state of the running setlX program.
     * @param summand    Value to add.
     * @return           Sum of this and summand.
     * @throws SetlException in case of (User-) Error.
     */
    public Value sumFlipped(final State state, final Value summand) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, summand),
                OperatorExpression.createFromTerm(state, this),
                Sum.S
        ).toTerm(state);
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    @Override
    public void addMember(final State state, final Value element) {
        body.addMember(state, element);
    }

    @Override
    public SetlList arguments(final State state) {
        return body.clone();
    }

    // viral operation
    @Override
    public Value cardinality(final State state) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                Cardinality.C
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value cartesianProduct(final State state, final Value other) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                OperatorExpression.createFromTerm(state, other),
                CartesianProduct.CP
        ).toTerm(state);
    }

    /**
     * Implementation of the >< operator. Computes the cartasion product of two sets
     * and combines two lists into one.
     *
     * @param state          Current state of the running setlX program.
     * @param other          Second value.
     * @return               Cartesian product of this and other.
     * @throws SetlException in case of (User-) Error.
     */
    public Value cartesianProductFlipped(final State state, final Value other) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, other),
                OperatorExpression.createFromTerm(state, this),
                CartesianProduct.CP
        ).toTerm(state);
    }

    // viral operation
    @Override
    public Value collectionAccess(final State state, final List<Value> args) throws SetlException {
        final FragmentList<OperatorExpression> arguments = new FragmentList<>(args.size());
        for (final Value v : args) {
            arguments.add(OperatorExpression.createFromTerm(state, v));
        }
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                new CollectionAccess(arguments)
        ).toTerm(state);
    }

    @Override
    public Value collectionAccessUnCloned(final State state, final List<Value> args) throws SetlException {
        return body.collectionAccessUnCloned(state, args);
    }

    // viral operation
    @Override
    public Value collectMap(final State state, final Value arg) throws SetlException {
        return new OperatorExpression(
                OperatorExpression.createFromTerm(state, this),
                new CollectMap(OperatorExpression.createFromTerm(state, arg))
        ).toTerm(state);
    }

    @Override
    public SetlBoolean containsMember(final State state, final Value element) {
        // Terms are inherently recursive, so search recursively
        return containsMemberRecursive(element);
    }

    @Override
    public Value firstMember() {
        return body.firstMember();
    }

    @Override
    public SetlString functionalCharacter(final State state) {
        String functionalCharacter = getFunctionalCharacter();
        if ( ! TermUtilities.isInternalFunctionalCharacter(functionalCharacter)) {
            // remove prefix for non-internal terms
            functionalCharacter = functionalCharacter.substring(TermUtilities.getLengthOfFunctionalCharacterPrefix());
        }
        return new SetlString(functionalCharacter);
    }

    /**
     * @return functional character of this Term.
     */
    public String getFunctionalCharacter() {
        return functionalCharacter;
    }

    @Override
    public Value getMember(final int index) throws SetlException {
        return body.getMember(index);
    }

    @Override
    public Value getMember(final State state, final Value index) throws SetlException {
        return body.getMember(state, index);
    }

    @Override
    protected Value getMembers(final State state, final int expectedNumberOfMembers, final int lowFromStart, final int highFromStart) throws SetlException {
        return body.getMembers(state, expectedNumberOfMembers, lowFromStart, highFromStart);
    }

    @Override
    public Value lastMember() {
        return body.lastMember();
    }

    @Override
    public Value maximumMember(final State state) throws SetlException {
        return body.maximumMember(state);
    }

    @Override
    public Value minimumMember(final State state) throws SetlException {
        return body.minimumMember(state);
    }

    // viral operation
    @Override
    public Value productOfMembers(final State state, final Value neutral) throws SetlException {
        if (neutral == Om.OM) {
            return new OperatorExpression(
                    OperatorExpression.createFromTerm(state, this),
                    ProductOfMembers.POM
            ).toTerm(state);
        } else {
            return new OperatorExpression(
                    OperatorExpression.createFromTerm(state, neutral),
                    OperatorExpression.createFromTerm(state, this),
                    ProductOfMembersBinary.POMB
            ).toTerm(state);
        }
    }

    @Override
    public void removeMember(final State state, final Value element) {
        body.removeMember(state, element);
    }

    @Override
    public Value removeFirstMember() {
        return body.removeFirstMember();
    }

    @Override
    public Value removeLastMember() {
        return body.removeLastMember();
    }

    @Override
    public void setMember(final State state, final Value index, final Value value) throws SetlException {
        body.setMember(state, index, value);
    }

    @Override
    public void setMember(final State state, int index, final Value value) throws SetlException {
        body.setMember(state, index, value);
    }

    @Override
    public int size() {
        return body.size();
    }

    // viral operation
    @Override
    public Value sumOfMembers(final State state, final Value neutral) throws SetlException {
        if (neutral == Om.OM) {
            return new OperatorExpression(
                    OperatorExpression.createFromTerm(state, this),
                    SumOfMembers.SOM
            ).toTerm(state);
        } else {
            return new OperatorExpression(
                    OperatorExpression.createFromTerm(state, neutral),
                    OperatorExpression.createFromTerm(state, this),
                    SumOfMembersBinary.SOMB
            ).toTerm(state);
        }
    }

    /* function call */

    // viral operation
    @Override
    public Value call(final State state, List<Value> argumentValues, final FragmentList<OperatorExpression> arguments, final Value listValue, final OperatorExpression listArg) throws SetlException {
        if (functionalCharacter.equalsIgnoreCase(VariableIgnore.getFunctionalCharacter())) {
            OperatorExpression operatorExpression = new OperatorExpression(
                    new Variable(OperatorExpression.createFromTerm(state, this).toString(state))
            );
            operatorExpression = new OperatorExpression(
                    operatorExpression,
                    listArg,
                    new Call(arguments, listArg)
            );
            return operatorExpression.toTerm(state);
        } else {
            throw new IncompatibleTypeException(
                "Viral term expansion is only supported when performing a call on a term representing a variable."
            );
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        TermUtilities.appendCodeFragmentString(state, this, sb);
    }

    @Override
    public void canonical(final State state, final StringBuilder sb) {
        sb.append(functionalCharacter);
        sb.append("(");
        body.canonical(state, sb, /* brackets = */ false);
        sb.append(")");
    }

    /* term operations */

    @Override
    public MatchResult matchesTerm(final State state, final Value other) throws SetlException {
        if ( functionalCharacter.equals(VariableIgnore.getFunctionalCharacter()) ||
                ( other.getClass() == Term.class &&
                  ((Term) other).functionalCharacter.equals(VariableIgnore.getFunctionalCharacter())
                )
           ) {
            return new MatchResult(true); // one of the terms is `ignore'
        } else if (functionalCharacter.equals(Variable.getFunctionalCharacter()) && body.size() == 1) {
            // 'this' is a variable, which match anything (except ignore of course)
            final MatchResult result  = new MatchResult(true);
            // get name of variable
            final Value       idStr   = body.firstMember(state);
            if (idStr.isString() == SetlBoolean.FALSE) { // this is a wrong variable term
                return new MatchResult(false);
            }
            final String id = idStr.getUnquotedString(state);
            result.addBinding(id, other);
            return result;
        } else if (functionalCharacter.equals(StringConstructor.getFunctionalCharacter()) && body.size() == 3 &&
                   other.isString() == SetlBoolean.TRUE) {
            // 'this' is a StringConstructor, which may match a simple string
            return other.matchesTerm(state, this);

        } else if (other.getClass() != Term.class) {
            return new MatchResult(false);
        }
        // 'other' is a term
        final Term otherTerm = (Term) other;

        if ( ! functionalCharacter.equals(otherTerm.functionalCharacter)) {
            // functional characters do not match
            return new MatchResult(false);
        } else if (body.size() != otherTerm.body.size()) {
            return new MatchResult(false);
        }

        // same functional character & same number of arguments
        final MatchResult     result        = new MatchResult(true);
        final Iterator<Value> thisIterator  = iterator();
        final Iterator<Value> otherIterator = otherTerm.iterator();
        while (thisIterator.hasNext() && otherIterator.hasNext() && result.isMatch()) {
            final MatchResult subResult = thisIterator.next().matchesTerm(state, otherIterator.next());
            if (subResult.isMatch()) {
                result.addBindings(subResult);
            } else {
                return new MatchResult(false);
            }
        }

        return result;
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Term.class) {
            final Term otherTerm = (Term) other;
            int cmp = functionalCharacter.compareTo(otherTerm.functionalCharacter);
            if (cmp != 0) {
                return cmp;
            }
            return body.compareTo(otherTerm.body);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Term.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equalTo(final Object other) {
        if (this == other) {
            return true;
        } else if (other.getClass() == Term.class) {
            final Term otherTerm = (Term) other;
            if (functionalCharacter.equals(otherTerm.functionalCharacter)) {
                return body.equalTo(otherTerm.body);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + functionalCharacter.hashCode() * 31 + body.hashCode();
    }
}


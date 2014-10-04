package org.randoom.setlx.types;

import org.randoom.setlx.boolExpressions.*;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.*;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
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
    public Term conjunction(final State state, final Expr other) throws SetlException {
        return (    new Conjunction(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, other.eval(state))
                    )
               ).toTerm(state);
    }

    /**
     * Compute the conjunction between another value and this.
     *
     * @param state Current state of the running setlX program.
     * @param other Expression to evaluate lazily.
     * @return      Conjunction of value and this.
     */
    public Term conjunctionFlipped(final State state, final Value other) throws SetlException {
        return (    new Conjunction(
                        TermConverter.valueToExpr(state, other),
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term disjunction(final State state, final Expr other) throws SetlException {
        return (    new Disjunction(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, other.eval(state))
                    )
               ).toTerm(state);
    }

    /**
     * Compute the disjunction between another value and this.
     *
     * @param state Current state of the running setlX program.
     * @param other Expression to evaluate lazily.
     * @return      Disjunction of value and this.
     */
    public Term disjunctionFlipped(final State state, final Value other) throws SetlException {
        return (    new Disjunction(
                        TermConverter.valueToExpr(state, other),
                        TermConverter.valueToExpr(state, this))
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term implication(final State state, final Expr other) throws SetlException {
        return (    new Implication(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, other.eval(state))
                    )
               ).toTerm(state);
    }

    /**
     * Compute the implication between another value and this.
     *
     * @param state Current state of the running setlX program.
     * @param other Expression to evaluate lazily.
     * @return      Implication of value and this.
     */
    public Term implicationFlipped(final State state, final Value other) throws SetlException {
        return (    new Implication(
                        TermConverter.valueToExpr(state, other),
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term not(final State state) throws SetlException {
        return (    new Not(
                        TermConverter.valueToExpr(state, this)
                    )
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
    public Term difference(final State state, final Value subtrahend) throws SetlException {
        return (    new Difference(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, subtrahend)
                    )
               ).toTerm(state);
    }

    /**
     * Compute the difference between another value and this.
     *
     * @param state      Current state of the running setlX program.
     * @param subtrahend Value to subtract.
     * @return           Difference subtrahend and this.
     */
    public Term differenceFlipped(final State state, final Value subtrahend) throws SetlException {
        return (    new Difference(
                        TermConverter.valueToExpr(state, subtrahend),
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Value factorial(final State state) throws SetlException {
        return (    new Factorial(
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term integerDivision(final State state, final Value divisor) throws SetlException {
        return (    new IntegerDivision(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, divisor)
                    )
               ).toTerm(state);
    }

    /**
     * Compute the integer division between another value and this.
     *
     * @param state   Current state of the running setlX program.
     * @param divisor Value to divide by.
     * @return        Integer division of divisor and this.
     */
    public Term integerDivisionFlipped(final State state, final Value divisor) throws SetlException {
        return (    new IntegerDivision(
                        TermConverter.valueToExpr(state, divisor),
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term minus(final State state) throws SetlException {
        return (    new Minus(
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term modulo(final State state, final Value modulo) throws SetlException {
        return (    new Modulo(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, modulo)
                    )
               ).toTerm(state);
    }

    /**
     * Compute the modulo between another value and this.
     *
     * @param state   Current state of the running setlX program.
     * @param modulo  Value to divide by.
     * @return        Modulo of modulo and this.
     */
    public Term moduloFlipped(final State state, final Value modulo) throws SetlException {
        return (    new Modulo(
                        TermConverter.valueToExpr(state, modulo),
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term power(final State state, final Value exponent) throws SetlException {
        return (    new Power(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, exponent)
                    )
               ).toTerm(state);
    }

    /**
     * Compute the power between another value and this.
     *
     * @param state    Current state of the running setlX program.
     * @param exponent Value to raise by.
     * @return         Exponent raised by the power of this.
     */
    public Term powerFlipped(final State state, final Value exponent) throws SetlException {
        return (    new Power(
                        TermConverter.valueToExpr(state, exponent),
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term product(final State state, final Value multiplier) throws SetlException {
        return (    new Product(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, multiplier)
                    )
               ).toTerm(state);
    }

    /**
     * Compute the product between another value and this.
     *
     * @param state      Current state of the running setlX program.
     * @param multiplier Value to multiply by.
     * @return           Product of multiplier and this.
     */
    public Term productFlipped(final State state, final Value multiplier) throws SetlException {
        return (    new Product(
                        TermConverter.valueToExpr(state, multiplier),
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term quotient(final State state, final Value divisor) throws SetlException {
        return (    new Quotient(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, divisor)
                    )
               ).toTerm(state);
    }

    /**
     * Divide another value by this.
     *
     * @param state      Current state of the running setlX program.
     * @param divisor    Value to divide by.
     * @return           Division of divisor and this.
     */
    public Term quotientFlipped(final State state, final Value divisor) throws SetlException {
        return (    new Quotient(
                        TermConverter.valueToExpr(state, divisor),
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term sum(final State state, final Value summand) throws SetlException {
        return (    new Sum(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, summand)
                    )
               ).toTerm(state);
    }

    /**
     * Add this value and another.
     *
     * @param state      Current state of the running setlX program.
     * @param summand    Value to add.
     * @return           Sum of this and summand.
     */
    public Term sumFlipped(final State state, final Value summand) throws SetlException {
        return (    new Sum(
                        TermConverter.valueToExpr(state, summand),
                        TermConverter.valueToExpr(state, this)
                    )
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
    public Term cardinality(final State state) throws SetlException {
        return (    new Cardinality(
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term cartesianProduct(final State state, final Value other) throws SetlException {
        return (    new CartesianProduct(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, other)
                    )
               ).toTerm(state);
    }

    /**
     * Implementation of the >< operator. Computes the cartasionProduct of two sets
     * and combines two lists into one.
     *
     * @param state          Current state of the running setlX program.
     * @param other          Second value.
     * @return               Cartesian product of this and other.
     */
    public Term cartesianProductFlipped(final State state, final Value other) throws SetlException {
        return (    new CartesianProduct(
                        TermConverter.valueToExpr(state, other),
                        TermConverter.valueToExpr(state, this)
                    )
               ).toTerm(state);
    }

    // viral operation
    @Override
    public Term collectionAccess(final State state, final List<Value> args) throws SetlException {
        final List<Expr> argExprs = new ArrayList<Expr>(args.size());
        for (final Value v : args) {
            argExprs.add(TermConverter.valueToExpr(state, v));
        }
        return (    new CollectionAccess(
                        TermConverter.valueToExpr(state, this),
                        argExprs)
               ).toTerm(state);
    }

    @Override
    public Value collectionAccessUnCloned(final State state, final List<Value> args) throws SetlException {
        return body.collectionAccessUnCloned(state, args);
    }

    // viral operation
    @Override
    public Term collectMap(final State state, final Value arg) throws SetlException {
        return (    new CollectMap(
                        TermConverter.valueToExpr(state, this),
                        TermConverter.valueToExpr(state, arg))
               ).toTerm(state);
    }

    @Override
    public SetlBoolean containsMember(final State state, final Value element) {
        // Terms are inherently recursive, so search recursively
        return containsMemberRecursive(element); // this is implemented in CollectionValue.java
    }

    @Override
    public Value firstMember() {
        return body.firstMember();
    }

    @Override
    public SetlString functionalCharacter(final State state) {
        return functionalCharacter();
    }

    /**
     * Get the functional character of this term.
     *
     * @return functional character of this value.
     */
    public SetlString functionalCharacter() {
        return new SetlString(getFunctionalCharacter());
    }

    /**
     * Get the functional character of this term.
     *
     * @return functional character of this value.
     */
    public String getFunctionalCharacter() {
        if (functionalCharacter.equals(Variable.getFunctionalCharacter())) {
            return Variable.getFunctionalCharacterExternal();
        } else {
            return functionalCharacter;
        }
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
    public Term productOfMembers(final State state, final Value neutral) throws SetlException {
        if (neutral == Om.OM) {
            return (    new ProductOfMembers(
                            TermConverter.valueToExpr(state, this)
                        )
                   ).toTerm(state);
        } else {
            return (    new ProductOfMembersBinary(
                            TermConverter.valueToExpr(state, neutral),
                            TermConverter.valueToExpr(state, this)
                        )
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
    public void setMember(final State state, final Value index, final Value v) throws SetlException {
        body.setMember(state, index, v);
    }

    @Override
    public int size() {
        return body.size();
    }

    // viral operation
    @Override
    public Term sumOfMembers(final State state, final Value neutral) throws SetlException {
        if (neutral == Om.OM) {
            return (    new SumOfMembers(
                    TermConverter.valueToExpr(state, this)
                )
           ).toTerm(state);
        } else {
            return (    new SumOfMembersBinary(
                    TermConverter.valueToExpr(state, neutral),
                    TermConverter.valueToExpr(state, this)
                )
           ).toTerm(state);
        }
    }

    /* function call */

    // viral operation
    @Override
    public Term call(final State state, final List<Expr> args, final Expr listArg) throws SetlException {
        if (functionalCharacter.equalsIgnoreCase(VariableIgnore.getFunctionalCharacter())) {
            return (    new Call(
                            new Variable(
                                TermConverter.valueToExpr(state, this).toString(state)
                            ),
                            args,
                            listArg
                        )
                   ).toTerm(state);
        } else {
            throw new IncompatibleTypeException(
                "Viral term expansion is only supported when performing a call on a term representing a variable."
            );
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        TermConverter.valueToCodeFragment(state, this, false).appendString(state, sb, 0);
    }

    @Override
    public void canonical(final State state, final StringBuilder sb) {
        if (functionalCharacter.equals(Variable.getFunctionalCharacter())) {
            sb.append(Variable.getFunctionalCharacterExternal());
        } else {
            sb.append(functionalCharacter);
        }

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
            if (idStr.isString() == SetlBoolean.FALSE) { // this is a wrong ^variable term
                return new MatchResult(false);
            }
            final String id = idStr.getUnquotedString(state);

            // look up if this variable is currently defined
            final Value       thisVal = state.findValue(id);
            if (thisVal != Om.OM) {
                return thisVal.matchesTerm(state, other);
            } else {
                // this undefined variable will be set to the value of `other' upon complete match
                result.addBinding(id, other);
                return result;
            }
        } else if (functionalCharacter.equals(StringConstructor.getFunctionalCharacter()) && body.size() == 2 &&
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
            if ( ! (functionalCharacter.equals(Variable.getFunctionalCharacterExternal()) &&
                    otherTerm.functionalCharacter.equals(Variable.getFunctionalCharacter())   )
               ) {
                // however this only unacceptable when ! (this == 'Variable AND other == 'variable)
                // e.g 'Variable must match 'variable
                return new MatchResult(false);
            }
        } else if (body.size() != otherTerm.body.size()) {
            return new MatchResult(false);
        }

        // same functional character & same number of arguments
        final MatchResult     result      = new MatchResult(true);
        final Iterator<Value> thisIter    = iterator();
        final Iterator<Value> otherIter   = otherTerm.iterator();
        while (thisIter.hasNext() && otherIter.hasNext() && result.isMatch()) {
            final MatchResult subResult   = thisIter.next().matchesTerm(state, otherIter.next());
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
    public int compareTo(final Value other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Term.class) {
            final Term otherTerm = (Term) other;
                  int  cmp   = functionalCharacter.compareTo(otherTerm.functionalCharacter);
            if (cmp != 0 && (
                    (
                        functionalCharacter.equals(Variable.getFunctionalCharacterExternal()) &&
                        otherTerm.functionalCharacter.equals(Variable.getFunctionalCharacter())
                    ) || (
                        functionalCharacter.equals(Variable.getFunctionalCharacter()) &&
                        otherTerm.functionalCharacter.equals(Variable.getFunctionalCharacterExternal())
                    )
                )
            ) {
                // these are regarded as one and the same
                cmp = 0;
            }
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

    @Override
    public boolean equalTo(final Object other) {
        if (this == other) {
            return true;
        } else if (other.getClass() == Term.class) {
            final Term otherTerm = (Term) other;
            if (functionalCharacter.equals(otherTerm.functionalCharacter)
                  || (
                    functionalCharacter.equals(Variable.getFunctionalCharacterExternal()) &&
                    otherTerm.functionalCharacter.equals(Variable.getFunctionalCharacter())
                ) || (
                    functionalCharacter.equals(Variable.getFunctionalCharacter()) &&
                    otherTerm.functionalCharacter.equals(Variable.getFunctionalCharacterExternal())
                )
            ) {
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


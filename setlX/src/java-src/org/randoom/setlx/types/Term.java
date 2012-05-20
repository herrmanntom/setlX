package org.randoom.setlx.types;

import org.randoom.setlx.boolExpressions.*;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.expressions.*;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
    This class implements terms in the form of

        f(e1, e2, ..., en)

*/

public class Term extends CollectionValue {
    // functional character of the term;    e.g. f
    private String      mFunctionalCharacter;
    // arguments in inner body of the term; e.g. e1, e2, ..., en
    private SetlList    mBody;

    public Term(String functionalCharacter) {
        this(functionalCharacter, new SetlList());
    }

    public Term(String functionalCharacter, SetlList body) {
        mFunctionalCharacter = functionalCharacter;
        mBody                = body;
    }

    public Term clone() {
        return new Term(mFunctionalCharacter, mBody.clone());
    }

    public Iterator<Value> iterator() {
        return mBody.iterator();
    }

    /* Boolean operations */

    // viral operation
    public Term and(Expr other) throws SetlException {
        return (    new Conjunction(
                        TermConverter.valueToExpr(this),
                        TermConverter.valueToExpr(other.eval())
                    )
               ).toTerm();
    }
    public Term andFlipped(Value other) {
        return (    new Conjunction(
                        TermConverter.valueToExpr(other),
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Term implies(Expr other) throws SetlException {
        return (    new Implication(
                        TermConverter.valueToExpr(this),
                        TermConverter.valueToExpr(other.eval())
                    )
               ).toTerm();
    }
    public Term impliesFlipped(Value other) {
        return (    new Implication(
                        TermConverter.valueToExpr(other),
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Term not() {
        return (    new Negation(
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Term or(Expr other) throws SetlException {
        return (    new Disjunction(
                        TermConverter.valueToExpr(this),
                        TermConverter.valueToExpr(other.eval())
                    )
               ).toTerm();
    }
    public Term orFlipped(Value other) {
        return (    new Disjunction(
                        TermConverter.valueToExpr(other),
                        TermConverter.valueToExpr(this))
               ).toTerm();
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isTerm() {
        return SetlBoolean.TRUE;
    }

    /* arithmetic operations */

    // viral operation
    public Term difference(Value subtrahend) {
        return (    new Difference(
                        TermConverter.valueToExpr(this),
                        TermConverter.valueToExpr(subtrahend)
                    )
               ).toTerm();
    }
    public Term differenceFlipped(Value subtrahend) {
        return (    new Difference(
                        TermConverter.valueToExpr(subtrahend),
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Term divide(Value divisor) {
        return (    new Divide(
                        TermConverter.valueToExpr(this),
                        TermConverter.valueToExpr(divisor)
                    )
               ).toTerm();
    }
    public Term divideFlipped(Value divisor) {
        return (    new Divide(
                        TermConverter.valueToExpr(divisor),
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Value factorial() {
        return (    new Factorial(
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Term modulo(Value modulo) {
        return (    new Modulo(
                        TermConverter.valueToExpr(this),
                        TermConverter.valueToExpr(modulo)
                    )
               ).toTerm();
    }
    public Term moduloFlipped(Value modulo) {
        return (    new Modulo(
                        TermConverter.valueToExpr(modulo),
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Term multiply(Value multiplier) {
        return (    new Multiply(
                        TermConverter.valueToExpr(this),
                        TermConverter.valueToExpr(multiplier)
                    )
               ).toTerm();
    }
    public Term multiplyFlipped(Value multiplier) {
        return (    new Multiply(
                        TermConverter.valueToExpr(multiplier),
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Term negate() {
        return (    new Negate(
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Term power(Value exponent) {
        return (    new Power(
                        TermConverter.valueToExpr(this),
                        TermConverter.valueToExpr(exponent)
                    )
               ).toTerm();
    }
    public Term powerFlipped(Value exponent) {
        return (    new Power(
                        TermConverter.valueToExpr(exponent),
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Term sum(Value summand) {
        return (    new Sum(
                        TermConverter.valueToExpr(this),
                        TermConverter.valueToExpr(summand)
                    )
               ).toTerm();
    }
    public Term sumFlipped(Value summand) {
        return (    new Sum(
                        TermConverter.valueToExpr(summand),
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    public void addMember(Value element) {
        mBody.addMember(element);
    }

    public SetlList arguments() {
        return mBody.clone();
    }

    // viral operation
    public Term cardinality() {
        return (    new Cardinality(
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    // viral operation
    public Term collectionAccess(List<Value> args) {
        List<Expr> argExprs = new ArrayList<Expr>(args.size());
        for (Value v : args) {
            argExprs.add(TermConverter.valueToExpr(v));
        }
        return (    new CollectionAccess(
                        TermConverter.valueToExpr(this),
                        argExprs)
               ).toTerm();
    }

    public Value collectionAccessUnCloned(List<Value> args) throws SetlException {
        return mBody.collectionAccessUnCloned(args);
    }

    // viral operation
    public Term collectMap(Value arg) {
        return (    new CollectMap(
                        TermConverter.valueToExpr(this),
                        TermConverter.valueToExpr(arg))
               ).toTerm();
    }

    public SetlBoolean containsMember(Value element) {
        // Terms are inherently recursive, so search recursively
        return containsMemberRecursive(element); // this is implemented in CollectionValue.java
    }

    public Value firstMember() {
        return mBody.firstMember();
    }

    public SetlString functionalCharacter() {
        if (mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER)) {
            return new SetlString(Variable.FUNCTIONAL_CHARACTER_EXTERNAL);
        } else {
            return new SetlString(mFunctionalCharacter);
        }
    }

    public Value getMember(Value index) throws SetlException {
        return mBody.getMember(index);
    }

    public Value getMemberUnCloned(Value index) throws SetlException {
        return mBody.getMemberUnCloned(index);
    }

    public Value getMembers(Value low, Value high) throws SetlException {
        return mBody.getMembers(low, high);
    }

    public Value lastMember() {
        return mBody.lastMember();
    }

    // viral operation
    public Term multiplyMembers() throws SetlException {
        return (    new MultiplyMembers(
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    public Value maximumMember() throws SetlException {
        return mBody.maximumMember();
    }

    public Value minimumMember() throws SetlException {
        return mBody.minimumMember();
    }

    public void removeMember(Value element) {
        mBody.removeMember(element);
    }

    public void removeFirstMember() {
        mBody.removeFirstMember();
    }

    public void removeLastMember() {
        mBody.removeLastMember();
    }

    public void setMember(Value index, Value v) throws SetlException {
        mBody.setMember(index, v);
    }

    public int size() {
        return mBody.size();
    }

    // viral operation
    public Term sumMembers() {
        return (    new SumMembers(
                        TermConverter.valueToExpr(this)
                    )
               ).toTerm();
    }

    /* function call */

    // viral operation
    public Term call(List<Expr> exprs, List<Value> args) throws IncompatibleTypeException {
        if (mFunctionalCharacter.equalsIgnoreCase(VariableIgnore.FUNCTIONAL_CHARACTER)) {
            List<Expr> argExprs = new ArrayList<Expr>(args.size());
            for (Value v : args) {
                argExprs.add(TermConverter.valueToExpr(v));
            }
            return (    new Call(
                            new Variable(
                                TermConverter.valueToExpr(this).toString()
                            ),
                            argExprs
                        )
                   ).toTerm();
        } else {
            throw new IncompatibleTypeException(
                "Viral term expansion is only supported when performing a call on a term representing a variable."
            );
        }
    }

    /* string and char operations */

    public String canonical() {
        String result = mBody.canonical();
        result = "(" + result.substring(1, result.length() - 1) + ")";

        if (mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER)) {
            result = Variable.FUNCTIONAL_CHARACTER_EXTERNAL + result;
        } else {
            result = mFunctionalCharacter + result;
        }

        return result;
    }

    public String toString() {
        return TermConverter.valueToCodeFragment(this, false).toString();
    }

    /* term operations */

    public MatchResult matchesTerm(Value other) throws IncompatibleTypeException {
        if ( mFunctionalCharacter.equals(VariableIgnore.FUNCTIONAL_CHARACTER) ||
                ( other instanceof Term &&
                  ((Term) other).mFunctionalCharacter.equals(VariableIgnore.FUNCTIONAL_CHARACTER)
                )
           ) {
            return new MatchResult(true); // one of the terms is `ignore'
        } else if (mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER) && mBody.size() == 1) {
            // 'this' is a variable, which match anything (except ignore of course)
            MatchResult result  = new MatchResult(true);
            // get name of variable
            Value       idStr   = mBody.iterator().next();
            if ( ! (idStr instanceof SetlString)) { // wrong 'variable term
                return new MatchResult(false);
            }
            String      id      = idStr.toString();
            if (id.length() <= 2) { // wrong 'variable term (name is to short)
                return new MatchResult(false);
            }
            // remove quotes
            id = id.substring(1, id.length() - 1);
            //store other to be stored into this variable upon complete match
            result.addBinding(id, other);
            return result;
        } else if ( ! (other instanceof Term)) {
            return new MatchResult(false);
        }
        // 'other' is a term
        Term otherTerm = (Term) other;

        if ( ! mFunctionalCharacter.equals(otherTerm.mFunctionalCharacter)) {
            // functional characters do not match
            if ( ! (mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER_EXTERNAL) &&
                    otherTerm.mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER)   )
               ) {
                // however this only unacceptable when ! (this == 'Variable AND other == 'variable)
                // e.g 'Variable must match 'variable
                return new MatchResult(false);
            }
        } else if (mBody.size() != otherTerm.mBody.size()) {
            return new MatchResult(false);
        }

        // same functional character & same number of arguments
        MatchResult     result      = new MatchResult(true);
        Iterator<Value> thisIter    = iterator();
        Iterator<Value> otherIter   = otherTerm.iterator();
        while (thisIter.hasNext() && otherIter.hasNext()) {
            Value       thisMember  = thisIter .next();
            Value       otherMember = otherIter.next();
            MatchResult subResult   = thisMember.matchesTerm(otherMember);
            if (subResult.isMatch()) {
                result.addBindings(subResult);
            } else {
                return new MatchResult(false);
            }
        }

        // all members match
        return result;
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
    public int compareTo(Value v){
        if (v instanceof Term) {
            Term other = (Term) v;
            int cmp = mFunctionalCharacter.compareTo(other.mFunctionalCharacter);
            if (cmp != 0 && (
                    (
                        mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER_EXTERNAL) &&
                        other.mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER)
                    ) || (
                        mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER) &&
                        other.mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER_EXTERNAL)
                    )
                )
            ) {
                // these are regarded as one and the same
                cmp = 0;
            }
            if (cmp != 0) {
                return cmp;
            }
            return mBody.compareTo(other.mBody);
        } else if (v instanceof ProcedureDefinition || v == Infinity.POSITIVE) {
            // only ProcedureDefinition and +Infinity are bigger
            return -1;
        } else {
            // everything else is smaller
            return 1;
        }
    }
}


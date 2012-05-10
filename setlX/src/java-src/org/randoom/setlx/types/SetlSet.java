package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.TreeSet;

/* This class implements an set of arbitrary SetlX values.
 * Note that sets in SetlX are in lexicographical order.
 *
 * It will most likely be created and filled by an SetListConstructor
 * (or is result of an operation).
 *
 * Also see:
 *   interpreter.utilities.Constructor
 *   interpreter.utilities.ExplicitList
 *   interpreter.utilities.Iteration
 *   interpreter.utilities.Range
 */

public class SetlSet extends CollectionValue {
    /* To allow initially `free' cloning, by only marking a clone without
     * actually doing any cloning, this set carries a isClone flag.
     *
     * If the contents of this SetlSet is modified `separateFromOriginal()'
     * MUST be called before the modification, which then performs the real cloning,
     * if required.
     *
     * Main benefit of this technique is to perform the real cloning only
     * when a clone is actually modified, thus not performing a time consuming
     * cloning, when the clone is only used read-only, which it is in most cases.
     */

    private TreeSet<Value> mSet;
    private boolean        isCloned; // is this set a clone?

    public SetlSet(){
        mSet        = new TreeSet<Value>();
        isCloned    = false; // new sets are not a clone
    }

    private SetlSet(TreeSet<Value> set){
        mSet        = set;
        isCloned    = true;  // sets created from another set ARE a clone
    }

    public SetlSet clone() {
        /* When cloning, THIS set is marked to be a clone as well.
         *
         * This is done, because even though THIS is the original, it must also be
         * cloned upon modification, otherwise clones which carry the same
         * member set of THIS set would not notice, e.g.
         * modifications of THIS original would bleed through to the clones.
         */
        isCloned = true;
        return new SetlSet(mSet);
    }

    /* If the contents of THIS SetlSet is modified, the following function MUST
     * be called before the modification. It performs the real cloning,
     * if THIS is actually marked as a clone.
     *
     * While clone() is called upon all members of this set, this does not perform
     * a `deep' cloning, as the members themselves are only marked for cloning.
     */
    private void separateFromOriginal() {
        if (isCloned) {
            TreeSet<Value> original = mSet;
            mSet = new TreeSet<Value>();
            for (Value v: original) {
                mSet.add(v.clone());
            }
            isCloned = false;
        }
    }

    public Iterator<Value> iterator() {
        return mSet.iterator();
    }

    public SetlBoolean isLessThan(Value other) throws IncompatibleTypeException {
        if (other instanceof SetlSet) {
            SetlSet otr = (SetlSet) other;
            return SetlBoolean.get((otr.mSet.containsAll(mSet)) && this.compareTo(otr) != 0);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " < " + other + "' is not a set."
            );
        }
    }

    /* type checks (sort of boolean operation) */

    public SetlBoolean isMap() {
        for (Value v: mSet) {
            if (v instanceof SetlList) {
                if (((SetlList) v).size() != 2) {
                    return SetlBoolean.FALSE;
                }
            } else {
                return SetlBoolean.FALSE;
            }
        }
        return SetlBoolean.TRUE;
    }

    public SetlBoolean isSet() {
        return SetlBoolean.TRUE;
    }

    /* type conversion */

    /*package*/ SetlList toList() {
        return new SetlList(new LinkedList<Value>(mSet));
    }

    /* arithmetic operations */

    public Value difference(Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof SetlSet) {
            SetlSet s       = (SetlSet) subtrahend;
            SetlSet result  = clone();
            result.separateFromOriginal();
            result.mSet.removeAll(s.mSet);
            return result;
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " - " + subtrahend + "' is not a set."
            );
        }
    }

    public Value multiply(Value multiplier) throws IncompatibleTypeException {
        if (multiplier instanceof SetlSet) {
            SetlSet m       = (SetlSet) multiplier;
            SetlSet result  = clone();
            result.separateFromOriginal();
            result.mSet.retainAll(m.mSet);
            return result;
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a set."
            );
        }
    }

    public Value sum(Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if(summand instanceof CollectionValue) {
            SetlSet result = this.clone();
            for (Value v: (CollectionValue) summand) {
                result.addMember(v.clone());
            }
            return result;
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand + "' is not a set or string."
            );
        }
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    public void addMember(Value element) {
        if (element == Om.OM) {
            return;
        } else {
            separateFromOriginal();
            mSet.add(element.clone());
        }
    }

    public Value collectionAccess(List<Value> args) throws SetlException {
        if (args.contains(RangeDummy.RD)) {
            throw new UndefinedOperationException(
                "Range operations are unsupported on '" + this + "'."
            );
        } else if (args.size() != 1) {
            throw new UndefinedOperationException(
                "Can not access elements using arguments '" + args + "' on '" + this + "';" +
                " Exactly one argument is required."
            );
        }
        return getMember(args.get(0));
    }

    public Value collectionAccessUnCloned(List<Value> args) throws SetlException {
        if (args.contains(RangeDummy.RD)) {
            throw new UndefinedOperationException(
                "Range operations are unsupported on '" + this + "'."
            );
        } else if (args.size() != 1) {
            throw new UndefinedOperationException(
                "Can not access elements using arguments '" + args + "' on '" + this + "';" +
                " Exactly one argument is required."
            );
        }
        return getMemberUnCloned(args.get(0));
    }

    // returns a set of all pairs which first element matches arg
    public Value collectMap(Value arg) throws SetlException {
        /* Extract the subset of all members, for which this is true
         * [arg] < subset <= [arg, +infinity]
         *
         * As this set is in lexicographical order, all pairs which first element
         * matches arg must be in this subset.
         */
        Value   lowerBound  = new SetlList();
        lowerBound.addMember(arg);
        Value   upperBound  = new SetlList();
        upperBound.addMember(arg);
        upperBound.addMember(Infinity.POSITIVE);

        NavigableSet<Value> subSet  = mSet.subSet(lowerBound, false, upperBound, true);
        SetlSet             result  = new SetlSet();

        for (Value v: subSet) {
            if (v instanceof SetlList) {
                if (v.size() == 2) {
                    result.addMember(v.lastMember());
                } else {
                    throw new IncompatibleTypeException(
                        "'" + this + "' is not a map."
                    );
                }
            } else {
                throw new IncompatibleTypeException(
                    "'" + this + "' is not a map."
                );
            }
        }
        return result;
    }

    public SetlBoolean containsMember(Value element) {
        return SetlBoolean.get(mSet.contains(element));
    }

    public SetlSet domain() throws SetlException {
        SetlSet result = new SetlSet();
        for (Value v: mSet) {
            if (v instanceof SetlList) {
                if (v.size() == 2) {
                    result.addMember(v.firstMember());
                } else {
                    throw new IncompatibleTypeException(
                        "'" + this + "' is not a map."
                    );
                }
            } else {
                throw new IncompatibleTypeException(
                    "'" + this + "' is not a map."
                );
            }
        }
        return result;
    }

    public Value firstMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return mSet.first().clone();
    }

    public Value getMember(Value element) throws SetlException {
        return getMemberZZZInternal(element).clone();
    }

    public Value getMemberUnCloned(Value element) throws SetlException {
        separateFromOriginal();
        return getMemberZZZInternal(element);
    }

    private Value getMemberZZZInternal(Value element) throws SetlException {
        /* Extract the subset of all members, for which this is true
         * [element] < subset <= [element, +infinity]
         *
         * As this set is in lexicographical order, all pairs which first element
         * matches element must be in this subset.
         */
        Value   lowerBound  = new SetlList();
        lowerBound.addMember(element);
        Value   upperBound  = new SetlList();
        upperBound.addMember(element);
        upperBound.addMember(Infinity.POSITIVE);

        NavigableSet<Value> subSet  = mSet.subSet(lowerBound, false, upperBound, true);
        Value               result  = Om.OM;

        for (Value v: subSet) {
            if (v instanceof SetlList) {
                if (v.size() == 2) {
                    if (result instanceof Om) {
                        result = v.getMemberUnCloned(new Rational(2));
                    } else {
                        // double match!
                        result = Om.OM;
                        break;
                    }
                } else {
                    throw new IncompatibleTypeException(
                        "'" + this + "' is not a map."
                    );
                }
            } else {
                throw new IncompatibleTypeException(
                    "'" + this + "' is not a map."
                );
            }
        }
        return result;
    }

    public Value lastMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return mSet.last().clone();
    }

    public Value maximumMember() {
        if (size() < 1) {
            return Infinity.NEGATIVE;
        }
        return lastMember();
    }

    public Value minimumMember() {
        if (size() < 1) {
            return Infinity.POSITIVE;
        }
        return firstMember();
    }

    public SetlSet permutations() throws SetlException {
        // add all members to a new list
        SetlList    result  = (SetlList) (new SetlList()).sum(this);
        // permutate list and return result
        return result.permutations();
    }

    // Compute the power set of this set according to the
    // following recursive equations:
    //     power({})      = { {} }
    //     power(A + {x}) = power(A) + { {x} + s : s in power(A) }
    public SetlSet powerSet() throws SetlException {
        if (size() == 0) {
            SetlSet power = new SetlSet();
            power.addMember(clone());
            return power;
        }
        Value          last      = lastMember();
        SetlSet        rest      = clone();
        rest.removeLastMember();
        SetlSet        powerRest = rest.powerSet();
        SetlSet        powerSet  = powerRest.clone();
        powerRest.separateFromOriginal();
        for (Value subSet : powerRest) {
            subSet.addMember(last);
        }
        return (SetlSet) powerSet.sum(powerRest);
    }

    public SetlSet range() throws SetlException {
        SetlSet result = new SetlSet();
        for (Value v: mSet) {
            if (v instanceof SetlList) {
                SetlList list  = (SetlList) v;
                if (list.size() == 2) {
                    result.addMember(list.lastMember());
                } else {
                    throw new IncompatibleTypeException(
                        "'" + this + "' is not a map."
                    );
                }
            } else {
                throw new IncompatibleTypeException(
                    "'" + this + "' is not a map."
                );
            }
        }
        return result;
    }

    public void setMember(Value index, Value v) throws SetlException {
        separateFromOriginal();
        /* Extract the subset of all members, for which this is true
         * [index] <= subset <= [index, +infinity]
         *
         * As this set is in lexicographical order, all pairs which first element
         * matches index must be in this subset.
         */
        Value   lowerBound  = new SetlList();
        lowerBound.addMember(index);
        Value   upperBound  = new SetlList();
        upperBound.addMember(index);
        upperBound.addMember(Infinity.POSITIVE);

        // remove all previously set pairs which first member matches index
        mSet.removeAll(new TreeSet<Value>(mSet.subSet(lowerBound, true, upperBound, true)));

        /* to get here this set must be empty or a map without a pair matching the index */
        if (v != Om.OM) {
            // add new pair [index, value] to this set
            SetlList pair = new SetlList();
            pair.addMember(index);
            pair.addMember(v);
            mSet.add(pair);
        }
    }

    public int size() {
        return mSet.size();
    }

    public void removeMember(Value element) {
        separateFromOriginal();
        mSet.remove(element);
    }

    public void removeFirstMember() {
        removeMember(firstMember());
    }

    public void removeLastMember() {
        removeMember(lastMember());
    }

    /* string and char operations */

    public String canonical() {
        String result = "{";

        Iterator<Value> iter    = iterator();
        while (iter.hasNext()) {
            Value   member  = iter.next();
            result += member.canonical();
            if (iter.hasNext()) {
                result += ", ";
            }
        }

        return result + "}";
    }

    public String toString() {
        return TermConverter.valueToCodeFragment(this, false).toString();
    }

    /* term operations */

    public MatchResult matchesTerm(Value other) throws IncompatibleTypeException {
        if (other == IgnoreDummy.ID) {
            return new MatchResult(true);
        } else if ( ! (other instanceof SetlSet)) {
            return new MatchResult(false);
        } else if ( this.size() != other.size()) {
            return new MatchResult(false);
        }

        // first match all atomic values
        TreeSet<Value> thisCopy     = new TreeSet<Value>(mSet);
        TreeSet<Value> otherCopy    = new TreeSet<Value>(((SetlSet)other).mSet);

        for (Value v : mSet) {
            // remove value from both sets, if
            // a) it is contained in both sets
            if (otherCopy.contains(v)) {
                // b) it realy matches itself
                // c) AND is a 'simple' match, i.e. does not include any variables
                //        which must be set after the match
                MatchResult mr = v.matchesTerm(v);
                if (mr.isMatch() && ( ! mr.hasBindings())) {
                    thisCopy .remove(v);
                    otherCopy.remove(v);
                }
            } else {
                if ( ! (v instanceof CollectionValue)) {
                    // this atomic value must be present to match both sets, but
                    // it is not
                    return new MatchResult(false);
                }
                // non atomic values are allowed to be left over, as they might
                // contain variables to be matched, which will match a permutation
            }
        }

        // add remaining members from `this' to a new list
        SetlList    thisList            = (new SetlSet(thisCopy)).toList();
        // permutate remaining members from `other'
        SetlSet     otherPermutations   = null;
        try {
            otherPermutations   = (new SetlSet(otherCopy)).permutations();
        } catch (SetlException se) {
            // will not happen
        }

        // both set match, when (at least) one permutation matches
        for (Value permutation : otherPermutations) {
            MatchResult match   = thisList.matchesTerm(permutation);
            if (match.isMatch()) {
                return match;
            }
        }

        // and does not match, when no permutation matches
        return new MatchResult(false);
    }

    public Value toTerm() {
        SetlSet termSet = new SetlSet();
        for (Value v: mSet) {
            termSet.addMember(v.toTerm());
        }
        return termSet;
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
        if (v instanceof SetlSet) {
            Iterator<Value> iterFirst  = mSet.iterator();
            Iterator<Value> iterSecond = ((SetlSet) v).mSet.iterator();
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                Value   first  = iterFirst .next();
                Value   second = iterSecond.next();
                int     cmp    = first.compareTo(second);
                if (cmp == 0) {
                    continue;
                }
                return cmp;
            }
            if (iterFirst.hasNext()) {
                return 1;
            }
            if (iterSecond.hasNext()) {
                return -1;
            }
            return 0;
        } else if (v instanceof SetlList || v instanceof Term ||
                   v instanceof ProcedureDefinition || v == Infinity.POSITIVE) {
            // only SetlList, Term, ProcedureDefinition and +Infinity are bigger
            return -1;
        } else {
            return 1;
        }
    }
}


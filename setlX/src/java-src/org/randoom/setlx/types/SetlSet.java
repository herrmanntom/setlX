package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

/* This class implements an set of arbitrary SetlX values.
 * Note that sets in SetlX are in lexicographical order.
 *
 * It will most likely be created and filled by an SetListConstructor
 * (or is result of an operation).
 *
 * Also see:
 *   interpreter.expressions.SetListConstructor
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

    private TreeSet<Value> mSortedSet;
    private boolean        isCloned; // is this set a clone?

    public SetlSet() {
        mSortedSet      = new TreeSet<Value>();
        isCloned        = false; // new sets are not a clone
    }

    private SetlSet(final TreeSet<Value> sortedSet){
        mSortedSet      = sortedSet;
        isCloned        = true;  // sets created from another set ARE a clone
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
        return new SetlSet(mSortedSet);
    }

    /* If the contents of THIS SetlSet is modified, the following function MUST
     * be called before the modification. It performs the real cloning,
     * if THIS is actually marked as a clone.
     *
     * While clone() is called upon all members of this set, this does not perform
     * a `deep' cloning, as the members themselves are only marked for cloning.
     */
    private final void separateFromOriginal() {
        if (isCloned) {
            final TreeSet<Value> original = mSortedSet;
            mSortedSet = new TreeSet<Value>();
            for (final Value v: original) {
                mSortedSet.add(v.clone());
            }
            isCloned   = false;
        }
    }

    public Iterator<Value> iterator() {
        return mSortedSet.iterator();
    }

    public SetlBoolean isLessThan(final Value other) throws IncompatibleTypeException {
        if (other instanceof SetlSet) {
            final SetlSet otr = (SetlSet) other;
            return SetlBoolean.get((otr.mSortedSet.containsAll(mSortedSet)) && this.compareTo(otr) != 0);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " < " + other + "' is not a set."
            );
        }
    }

    /* type checks (sort of boolean operation) */

    public SetlBoolean isMap() {
        for (final Value v: mSortedSet) {
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
        return new SetlList(new ArrayList<Value>(mSortedSet));
    }

    /* arithmetic operations */

    public Value difference(final Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof SetlSet) {
            final SetlSet result = clone();
            result.separateFromOriginal();
            result.mSortedSet.removeAll(((SetlSet) subtrahend).mSortedSet);
            return result;
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " - " + subtrahend + "' is not a set."
            );
        }
    }

    public Value differenceAssign(final Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof SetlSet) {
            separateFromOriginal();
            mSortedSet.removeAll(((SetlSet) subtrahend).mSortedSet);
            return this;
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " -= " + subtrahend + "' is not a set."
            );
        }
    }

    public Value multiply(final Value multiplier) throws IncompatibleTypeException {
        if (multiplier instanceof SetlSet) {
            final SetlSet result = clone();
            result.separateFromOriginal();
            result.mSortedSet.retainAll(((SetlSet) multiplier).mSortedSet);
            return result;
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a set."
            );
        }
    }

    public Value multiplyAssign(final Value multiplier) throws IncompatibleTypeException {
        if (multiplier instanceof SetlSet) {
            separateFromOriginal();
            mSortedSet.retainAll(((SetlSet) multiplier).mSortedSet);
            return this;
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a set."
            );
        }
    }

    public Value sum(final Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(this);
        } else if(summand instanceof CollectionValue) {
            final SetlSet result = this.clone();
            for (final Value v: (CollectionValue) summand) {
                result.addMember(v.clone());
            }
            return result;
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand + "' is not a set or string."
            );
        }
    }

    public Value sumAssign(final Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(this);
        } else if(summand instanceof CollectionValue) {
            separateFromOriginal();
            for (final Value v: (CollectionValue) summand) {
                addMember(v.clone());
            }
            return this;
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " += " + summand + "' is not a set or string."
            );
        }
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    public void addMember(final Value element) {
        if (element != Om.OM) {
            separateFromOriginal();
            mSortedSet.add(element.clone());
        }
    }

    public Value collectionAccess(final List<Value> args) throws SetlException {
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

    public Value collectionAccessUnCloned(final List<Value> args) throws SetlException {
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
    public Value collectMap(final Value arg) throws SetlException {
        /* Extract the subset of all members, for which this is true
         * [arg] < subset <= [arg, +infinity]
         *
         * As this set is in lexicographical order, all pairs which first element
         * matches arg must be in this subset.
         */
        final Value lowerBound  = new SetlList(1);
        lowerBound.addMember(arg);
        final Value upperBound  = new SetlList(2);
        upperBound.addMember(arg);
        upperBound.addMember(Infinity.POSITIVE);

        final NavigableSet<Value> navSubSet = mSortedSet.subSet(lowerBound, false, upperBound, true);

        // make sure the subSet is a TreeSet, as Android API <= 10 can't iterate a navigableSet...
        TreeSet<Value> subSet = null;
        if (navSubSet instanceof TreeSet) { // on all tested runtimes this is a TreeSet, be one can't be sure
            subSet  = (TreeSet<Value>) navSubSet;
        } else { // slow, but secure shallow copy is better than ClassCastException
            subSet  = new TreeSet<Value>(navSubSet);
        }

        final SetlSet result = new SetlSet();
        for (final Value v: subSet) {
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
        return SetlBoolean.get(mSortedSet.contains(element));
    }

    public SetlSet domain() throws SetlException {
        final SetlSet result = new SetlSet();
        for (final Value v: mSortedSet) {
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
        return mSortedSet.first().clone();
    }

    public Value getMember(final Value element) throws SetlException {
        return getMemberZZZInternal(element).clone();
    }

    public Value getMemberUnCloned(final Value element) throws SetlException {
        separateFromOriginal();
        return getMemberZZZInternal(element);
    }

    private Value getMemberZZZInternal(final Value element) throws SetlException {
        /* Extract the subset of all members, for which this is true
         * [element] < subset <= [element, +infinity]
         *
         * As this set is in lexicographical order, all pairs which first element
         * matches element must be in this subset.
         */
        final Value lowerBound  = new SetlList(1);
        lowerBound.addMember(element);
        final Value upperBound  = new SetlList(2);
        upperBound.addMember(element);
        upperBound.addMember(Infinity.POSITIVE);

        final NavigableSet<Value> navSubSet = mSortedSet.subSet(lowerBound, false, upperBound, true);

        // make sure the subSet is a TreeSet, as Android API <= 10 can't iterate a navigableSet...
        TreeSet<Value>            subSet    = null;
        if (navSubSet instanceof TreeSet) { // on all tested runtimes this is a TreeSet, be one can't be sure
            subSet  = (TreeSet<Value>) navSubSet;
        } else { // slow, but secure shallow copy is better than ClassCastException
            subSet  = new TreeSet<Value>(navSubSet);
        }

        Value                     result    = Om.OM;
        for (final Value v: subSet) {
            if (v instanceof SetlList) {
                if (v.size() == 2) {
                    if (result == Om.OM) {
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
        return mSortedSet.last().clone();
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
        // add all members to a new list, then permutate list and return result
        return toList().permutations();
    }

    // Compute the power set of this set according to the
    // following recursive equations:
    //     power({})      = { {} }
    //     power(A + {x}) = power(A) + { {x} + s : s in power(A) }
    public SetlSet powerSet() throws SetlException {
        if (size() == 0) {
            final SetlSet power = new SetlSet();
            power.addMember(clone());
            return power;
        }
        // get some arbitrary member
        final Value      arb       = mSortedSet.iterator().next();
        final SetlSet    rest      = clone();
        rest.removeMember(arb);
        // create powerset of the rest
        final SetlSet    powerRest = rest.powerSet();
        final SetlSet    powerSet  = powerRest.clone();
        // add arbitrary element to every result
        powerRest.separateFromOriginal();
        for (final Value subSet : powerRest) {
            subSet.addMember(arb);
        }
        return (SetlSet) powerSet.sum(powerRest);
    }

    public SetlSet range() throws SetlException {
        final SetlSet result = new SetlSet();
        for (final Value v: mSortedSet) {
            if (v instanceof SetlList) {
                final SetlList list  = (SetlList) v;
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

    public void setMember(final Value index, final Value v) throws SetlException {

        separateFromOriginal();

        /* Extract the subset of all members, for which this is true
         * [index] <= subset <= [index, +infinity]
         *
         * As this set is in lexicographical order, all pairs which first element
         * matches index must be in this subset.
         */
        final Value lowerBound  = new SetlList(1);
        lowerBound.addMember(index);
        final Value upperBound  = new SetlList(2);
        upperBound.addMember(index);
        upperBound.addMember(Infinity.POSITIVE);

        // remove all previously set pairs which first member matches index
        mSortedSet.removeAll(new TreeSet<Value>(mSortedSet.subSet(lowerBound, true, upperBound, true)));

        /* now this set must either be empty or a map without a pair matching the index */
        if (v != Om.OM) {
            // add new pair [index, value] to this set
            final SetlList pair = new SetlList(2);
            pair.addMember(index);
            pair.addMember(v);
            mSortedSet.add(pair);
        }
    }

    public int size() {
        return mSortedSet.size();
    }

    public void removeMember(Value element) {
        separateFromOriginal();
        mSortedSet.remove(element);
    }

    public void removeFirstMember() {
        separateFromOriginal();
        mSortedSet.pollFirst();
    }

    public void removeLastMember() {
        separateFromOriginal();
        mSortedSet.pollLast();
    }

    /* string and char operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        TermConverter.valueToCodeFragment(this, false).appendString(sb, 0);
    }

    public void canonical(final StringBuilder sb) {
        sb.append("{");
        final Iterator<Value> iter  = iterator(); // also calls sort()
        while (iter.hasNext()) {
            iter.next().canonical(sb);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("}");
    }

    /* term operations */

    public MatchResult matchesTerm(final Value otr) throws IncompatibleTypeException {
        if (otr == IgnoreDummy.ID) {
            return new MatchResult(true);
        } else if ( ! (otr instanceof SetlSet)) {
            return new MatchResult(false);
        } else if ( this.size() != otr.size()) {
            return new MatchResult(false);
        }

        // first match all atomic values
        final TreeSet<Value> thisCopy   = new TreeSet<Value>(mSortedSet);
        final TreeSet<Value> otherCopy  = new TreeSet<Value>(((SetlSet) otr).mSortedSet);

        for (final Value v : mSortedSet) {
            // remove value from both sets, if
            // a) it is contained in both sets
            if (otherCopy.contains(v)) {
                // b) it realy matches itself
                // c) AND is a 'simple' match, i.e. does not include any variables
                //        which must be set after the match
                final MatchResult mr = v.matchesTerm(v);
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

        // copy remaining members into a new list
        final SetlList  thisList            = new SetlList(new ArrayList<Value>(thisCopy));
        // permutate remaining members from `other'
              SetlSet   otherPermutations   = null;
        try {
            otherPermutations   = (new SetlList(new ArrayList<Value>(otherCopy))).permutations();
        } catch (SetlException se) {
            // will not happen
        }

        // both set match, when (at least) one permutation matches
        MatchResult match = null;
        for (final Value permutation : otherPermutations) {
            match = thisList.matchesTerm(permutation);
            if (match.isMatch()) {
                return match;
            }
        }

        // and does not match, when no permutation matches
        return new MatchResult(false);
    }

    public Value toTerm() {
        final SetlSet termSet = new SetlSet();
        for (final Value v: mSortedSet) {
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
    public int compareTo(final Value v){
        if (v instanceof SetlSet) {
            final Iterator<Value> iterFirst  = iterator();               // also calls sort()
            final Iterator<Value> iterSecond = ((SetlSet) v).iterator(); // also calls sort()
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                int     cmp    = iterFirst.next().compareTo(iterSecond.next());
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

    private final static int initHashCode = SetlSet.class.hashCode();

    public int hashCode() {
        int hash = initHashCode;
        for (final Value v : mSortedSet) {
            hash = hash * 31 + v.hashCode();
        }
        return hash;
    }
}


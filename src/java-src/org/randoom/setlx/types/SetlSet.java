package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressionUtilities.ExplicitListWithRest;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * This class implements a set of arbitrary SetlX values.
 * Note that sets in SetlX are in lexicographical order.
 *
 * It will most likely be created and filled by an SetListConstructor
 * (or is result of an operation).
 *
 * Also see:
 *   interpreter.expressions.SetListConstructor
 *   interpreter.expressionUtilities.CollectionBuilder
 *   interpreter.expressionUtilities.ExplicitList
 *   interpreter.expressionUtilities.Iteration
 *   interpreter.expressionUtilities.Range
 */
public class SetlSet extends CollectionValue {
    /* To allow initially `free' cloning, by only marking a clone without
     * actually doing any cloning, this set carries an isClone flag.
     *
     * If the contents of this SetlSet is modified, the method `separateFromOriginal()'
     * MUST be called before the modification.  This method then performs the real cloning,
     * if required.
     *
     * Main benefit of this technique is to perform the real cloning only
     * when a clone is actually modified, thus not performing a time consuming
     * cloning, when the clone is only used read-only, which it is in most cases.
     */

    private TreeSet<Value> set;
    private boolean        isCloned; // is this set a clone?

    public SetlSet() {
        this.set      = new TreeSet<Value>();
        this.isCloned = false; // new sets are not a clone
    }

    private SetlSet(final TreeSet<Value> sortedSet){
        this.set      = sortedSet;
        this.isCloned = true;  // sets created from another set ARE a clone
    }

    @Override
    public SetlSet clone() {
        /* When cloning, THIS set is marked to be a clone as well.
         *
         * This is done, because even though THIS is the original, it must also be
         * cloned upon modification, otherwise clones which carry the same
         * member set of THIS set would not notice, e.g.
         * modifications of THIS original would bleed through to the clones.
         */
        isCloned = true;
        return new SetlSet(set);
    }

    /**
     * If the contents of THIS SetlSet is modified, the following function MUST
     * be called before the modification. It performs the real cloning,
     * if THIS is actually marked as a clone.
     *
     * While clone() is called upon all members of this set, this does not perform
     * a `deep' cloning, as the members themselves are only marked for cloning.
     */
    private final void separateFromOriginal() {
        if (isCloned) {
            final TreeSet<Value> original = set;
            set = new TreeSet<Value>();
            for (final Value v: original) {
                set.add(v.clone());
            }
            isCloned   = false;
        }
    }

    @Override
    public Iterator<Value> iterator() {
        return set.iterator();
    }

    @Override
    public Iterator<Value> descendingIterator() {
        return set.descendingIterator();
    }

    @Override
    public SetlBoolean isLessThan(final State state, final Value other) throws SetlException {
        if (other instanceof SetlSet) {
            final SetlSet otr = (SetlSet) other;
            return SetlBoolean.valueOf((otr.set.containsAll(set)) && this.isEqualTo(state, otr) == SetlBoolean.FALSE);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " < " + other + "' is not a set."
            );
        }
    }

    /* type checks (sort of boolean operation) */

    @Override
    public SetlBoolean isMap() {
        final TreeSet<Value> temp = new TreeSet<Value>();
        for (final Value v: set) {
            if (v instanceof SetlList && ((SetlList) v).size() == 2) {
                if ( ! temp.add(((SetlList) v).firstMember())) {
                    return SetlBoolean.FALSE;
                }
            } else {
                return SetlBoolean.FALSE;
            }
        }
        return SetlBoolean.TRUE;
    }

    @Override
    public SetlBoolean isSet() {
        return SetlBoolean.TRUE;
    }

    /* type conversion */

    /*package*/ SetlList toList() {
        return new SetlList(new ArrayList<Value>(set));
    }

    /* arithmetic operations */

    @Override
    public Value difference(final State state, final Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof SetlSet) {
            final SetlSet result = clone();
            result.separateFromOriginal();
            result.set.removeAll(((SetlSet) subtrahend).set);
            return result;
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " - " + subtrahend + "' is not a set."
            );
        }
    }

    @Override
    public Value differenceAssign(final State state, final Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof SetlSet) {
            separateFromOriginal();
            set.removeAll(((SetlSet) subtrahend).set);
            return this;
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " -= " + subtrahend + "' is not a set."
            );
        }
    }

    // modulo => symmetric difference, e.g. {1,2} % {1,3} = {2,3}
    //                                        a       b       c
    // compute as follows:
    //  tmp = b - a
    //  c   = tmp + (a - b)
    @Override
    public Value modulo(final State state, final Value modulo) throws SetlException {
        if (modulo instanceof SetlSet) {

            final SetlSet mClone = (SetlSet) modulo.clone();
            mClone.separateFromOriginal();
            mClone.set.removeAll(this.set);

            final SetlSet result = clone();
            result.separateFromOriginal();
            result.set.removeAll(((SetlSet) modulo).set);

            result.set.addAll(mClone.set);
            return result;
        } else if (modulo instanceof Term) {
            return ((Term) modulo).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " % " + modulo + "' is not a set."
            );
        }
    }

    // modulo => symmetric difference, e.g. {1,2} % {1,3} = {2,3}
    //                                        a       b       a*
    // compute as follows:
    //  tmp = b - a
    //  a  -= b
    //  a  += tmp
    @Override
    public Value moduloAssign(final State state, final Value modulo) throws SetlException {
        if (modulo instanceof SetlSet) {
            separateFromOriginal();

            final SetlSet mClone = (SetlSet) modulo.clone();
            mClone.separateFromOriginal();
            mClone.set.removeAll(this.set);

            this.set.removeAll(((SetlSet) modulo).set);

            this.set.addAll(mClone.set);

            return this;
        } else if (modulo instanceof Term) {
            return ((Term) modulo).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " % " + modulo + "' is not a set."
            );
        }
    }

    @Override
    public Value power(final State state, final Value exponent) throws SetlException {
        if (exponent instanceof NumberValue && exponent.equalTo(Rational.TWO)) {
            try {
                return this.cartesianProduct(state, this);
            } catch (final SetlException se) {
                se.addToTrace("Error in substitute operation \"" + this + " >< " + this +  "\":");
                throw se;
            }
        } else if (exponent instanceof Term) {
            return ((Term) exponent).powerFlipped(state, this);
        }
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this + " ** " + exponent + "' is not a number."
        );
    }

    @Override
    public Value product(final State state, final Value multiplier) throws IncompatibleTypeException {
        if (multiplier instanceof SetlSet) {
            final SetlSet result = clone();
            result.separateFromOriginal();
            result.set.retainAll(((SetlSet) multiplier).set);
            return result;
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a set."
            );
        }
    }

    @Override
    public Value productAssign(final State state, final Value multiplier) throws IncompatibleTypeException {
        if (multiplier instanceof SetlSet) {
            separateFromOriginal();
            set.retainAll(((SetlSet) multiplier).set);
            return this;
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " * " + multiplier + "' is not a set."
            );
        }
    }

    @Override
    public Value sum(final State state, final Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else if(summand instanceof CollectionValue) {
            final SetlSet result = this.clone();
            for (final Value v: (CollectionValue) summand) {
                result.addMember(state, v.clone());
            }
            return result;
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand + "' is not a set or string."
            );
        }
    }

    @Override
    public Value sumAssign(final State state, final Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else if(summand instanceof CollectionValue) {
            separateFromOriginal();
            for (final Value v: (CollectionValue) summand) {
                addMember(state, v.clone());
            }
            return this;
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " += " + summand + "' is not a set or string."
            );
        }
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    @Override
    public void addMember(final State state, final Value element) {
        if (element != Om.OM) {
            separateFromOriginal();
            set.add(element.clone());
        }
    }

    @Override
    public Value cartesianProduct(final State state, final Value other) throws SetlException {
        if (other instanceof SetlSet) {

            final SetlSet result = new SetlSet();

            for (final Value first : set) {
                for (final Value second : (SetlSet) other) {
                    final SetlList tuple = new SetlList(2);
                    tuple.addMember(state, first);
                    tuple.addMember(state, second);
                    result.addMember(state, tuple);
                }
            }

            return result;
        } else if (other instanceof Term) {
            return ((Term) other).cartesianProductFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " >< " + other + "' is not a set."
            );
        }
    }

    @Override
    public Value collectionAccess(final State state, final List<Value> args) throws SetlException {
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
        return getMember(state, args.get(0));
    }

    @Override
    public Value collectionAccessUnCloned(final State state, final List<Value> args) throws SetlException {
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
        return getMemberUnCloned(state, args.get(0));
    }

    // returns a set of all pairs which first element matches arg
    @Override
    public Value collectMap(final State state, final Value arg) throws SetlException {
        /* Extract the subset of all members, for which this is true
         * [arg] < subset <= [arg, +infinity]
         *
         * As this set is in lexicographical order, all pairs which first element
         * matches arg must be in this subset.
         */
        final Value lowerBound  = new SetlList(1);
        lowerBound.addMember(state, arg);
        final Value upperBound  = new SetlList(2);
        upperBound.addMember(state, arg);
        upperBound.addMember(state, Top.TOP);

        final NavigableSet<Value> navSubSet = set.subSet(lowerBound, false, upperBound, true);

        // make sure the subSet is a TreeSet, as Android API <= 10 can't iterate a navigableSet...
        TreeSet<Value> subSet = null;
        if (navSubSet instanceof TreeSet) { // on all tested runtimes this is a TreeSet, be one can't be sure
            subSet  = (TreeSet<Value>) navSubSet;
        } else { // slow, but secure shallow copy is better than ClassCastException
            subSet  = new TreeSet<Value>(navSubSet);
        }

        final SetlSet result = new SetlSet();
        for (final Value v: subSet) {
            if (v instanceof SetlList && v.size() == 2) {
                result.addMember(state, v.lastMember(state));
            } else {
                throw new IncompatibleTypeException(
                    "'" + this + "' is not a map."
                );
            }
        }
        return result;
    }

    @Override
    public SetlBoolean containsMember(final State state, final Value element) {
        return SetlBoolean.valueOf(set.contains(element));
    }

    @Override
    public SetlSet domain(final State state) throws SetlException {
        final TreeSet<Value> result = new TreeSet<Value>();
        for (final Value v: set) {
            if (v instanceof SetlList && v.size() == 2) {
                result.add(v.firstMember(state));
            } else {
                throw new IncompatibleTypeException(
                    "'" + this + "' is not a map."
                );
            }
        }
        return new SetlSet(result);
    }

    @Override
    public Value firstMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return set.first().clone();
    }

    @Override
    public Value getMember(final State state, final Value element) throws SetlException {
        return getMemberZZZInternal(state, element).clone();
    }

    @Override
    public Value getMemberUnCloned(final State state, final Value element) throws SetlException {
        separateFromOriginal();
        return getMemberZZZInternal(state, element);
    }

    private Value getMemberZZZInternal(final State state, final Value element) throws SetlException {
        /* Extract the subset of all members, for which this is true
         * [element] < subset <= [element, +infinity]
         *
         * As this set is in lexicographical order, all pairs which first element
         * matches element must be in this subset.
         */
        final Value lowerBound  = new SetlList(1);
        lowerBound.addMember(state, element);
        final Value upperBound  = new SetlList(2);
        upperBound.addMember(state, element);
        upperBound.addMember(state, Top.TOP);

        final NavigableSet<Value> navSubSet = set.subSet(lowerBound, false, upperBound, true);

        // make sure the subSet is a TreeSet, as Android API <= 10 can't iterate a navigableSet...
        TreeSet<Value>            subSet    = null;
        if (navSubSet instanceof TreeSet) { // on all tested runtimes this is a TreeSet, be one can't be sure
            subSet  = (TreeSet<Value>) navSubSet;
        } else { // slow, but secure shallow copy is better than ClassCastException
            subSet  = new TreeSet<Value>(navSubSet);
        }

        Value                     result    = Om.OM;
        for (final Value v: subSet) {
            if (v instanceof SetlList && v.size() == 2) {
                if (result == Om.OM) {
                    result = ((SetlList) v).getMemberUnCloned(2);
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
        }
        return result;
    }

    @Override
    public Value lastMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return set.last().clone();
    }

    @Override
    public Value maximumMember(final State state) {
        if (size() < 1) {
	    return SetlDouble.NEGATIVE_INFINITY;
        }
        return lastMember(state);
    }

    @Override
    public Value minimumMember(final State state) {
        if (size() < 1) {
	    return SetlDouble.POSITIVE_INFINITY;
        }
        return firstMember(state);
    }

    @Override
    public SetlSet permutations(final State state) throws SetlException {
        // add all members to a new list, then permutate list and return result
        return toList().permutations(state);
    }

    // Compute the power set of this set according to the
    // following recursive equations:
    //     power({})      = { {} }
    //     power(A + {x}) = power(A) + { {x} + s : s in power(A) }
    @Override
    public SetlSet powerSet(final State state) throws SetlException {
        if (size() == 0) {
            final SetlSet power = new SetlSet();
            power.addMember(state, clone());
            return power;
        }
        // get some arbitrary member
        final Value      arb       = set.iterator().next();
        final SetlSet    rest      = clone();
        rest.removeMember(arb);
        // create power set of the rest
        final SetlSet    powerRest = rest.powerSet(state);
        final SetlSet    powerSet  = powerRest.clone();
        // add arbitrary element to every result
        powerRest.separateFromOriginal();
        for (final Value subSet : powerRest) {
            subSet.addMember(state, arb);
        }
        return (SetlSet) powerSet.sum(state, powerRest);
    }

    @Override
    public SetlSet range(final State state) throws SetlException {
        final SetlSet result = new SetlSet();
        for (final Value v: set) {
            if (v instanceof SetlList && v.size() == 2) {
                result.addMember(state, v.lastMember(state));
            } else {
                throw new IncompatibleTypeException(
                    "'" + this + "' is not a map."
                );
            }
        }
        return result;
    }

    @Override
    public void setMember(final State state, final Value index, final Value v) throws SetlException {

        separateFromOriginal();

        /* Extract the subset of all members, for which this is true
         * [index] <= subset <= [index, +infinity]
         *
         * As this set is in lexicographical order, all pairs which first element
         * matches index must be in this subset.
         */
        final Value lowerBound  = new SetlList(1);
        lowerBound.addMember(state, index);
        final Value upperBound  = new SetlList(2);
        upperBound.addMember(state, index);
        upperBound.addMember(state, Top.TOP);

        // remove all previously set pairs which first member matches index
        set.removeAll(new TreeSet<Value>(set.subSet(lowerBound, true, upperBound, true)));

        /* now this set must either be empty or a map without a pair matching the index */
        if (v != Om.OM) {
            // add new pair [index, value] to this set
            final SetlList pair = new SetlList(2);
            pair.addMember(state, index);
            pair.addMember(state, v);
            set.add(pair);
        }
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public void removeMember(final Value element) {
        separateFromOriginal();
        set.remove(element);
    }

    @Override
    public Value removeFirstMember() {
        separateFromOriginal();
        final Value result = set.pollFirst();
        if (result != null) {
            return result;
        } else {
            return Om.OM;
        }
    }

    @Override
    public Value removeLastMember() {
        separateFromOriginal();
        final Value result = set.pollLast();
        if (result != null) {
            return result;
        } else {
            return Om.OM;
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        TermConverter.valueToCodeFragment(this, false).appendString(state, sb, 0);
    }

    @Override
    public void canonical(final State state, final StringBuilder sb) {
        sb.append("{");
        final Iterator<Value> iter  = iterator(); // also calls sort()
        while (iter.hasNext()) {
            iter.next().canonical(state, sb);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("}");
    }

    /* term operations */

    @Override
    public MatchResult matchesTerm(final State state, final Value otr) throws SetlException {
        if (otr == IgnoreDummy.ID) {
            return new MatchResult(true);
        } else if ( ! (otr instanceof SetlSet)) {
            return new MatchResult(false);
        } else if (set.size() == 1 && set.first() instanceof Term) {
            final MatchResult result = ExplicitListWithRest.matchTerm(state, (Term) set.first(), (SetlSet) otr);
            if (result.isMatch()) {
                return result;
            }
        }

        if ( this.size() != otr.size()) {
            return new MatchResult(false);
        }

        // first match all atomic values
        final TreeSet<Value> thisCopy   = new TreeSet<Value>(set);
        final TreeSet<Value> otherCopy  = new TreeSet<Value>(((SetlSet) otr).set);

        for (final Value v : set) {
            // remove value from both sets, if
            // a) it is contained in both sets
            if (otherCopy.contains(v)) {
                // b) it really matches itself
                // c) AND is a 'simple' match, i.e. does not include any variables
                //        which must be set after the match
                final MatchResult mr = v.matchesTerm(state, v);
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
        // permute remaining members from `other'
              Value     otherPermutation    = new SetlList(new ArrayList<Value>(otherCopy));

        // both set match, when (at least) one permutation matches
        MatchResult match = null;
        while (otherPermutation != Om.OM) {
            match = thisList.matchesTerm(state, otherPermutation);
            if (match.isMatch()) {
                return match;
            }
            try {
                otherPermutation = otherPermutation.nextPermutation(state);
            } catch (final SetlException se) {
                // will never happen
                otherPermutation = Om.OM;
            }
        }

        // and does not match, when no permutation matches
        return new MatchResult(false);
    }

    @Override
    public Value toTerm(final State state) {
        final SetlSet termSet = new SetlSet();
        for (final Value v: set) {
            termSet.addMember(state, v.toTerm(state));
        }
        return termSet;
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof SetlSet) {
            final Iterator<Value> iterFirst  = iterator();
            final Iterator<Value> iterSecond = ((SetlSet) v).iterator();
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                final int     cmp    = iterFirst.next().compareTo(iterSecond.next());
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
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    @Override
    protected int compareToOrdering() {
        return 700;
    }

    @Override
    public boolean equalTo(final Value v) {
        if (this == v) {
            return true;
        } else if (v instanceof SetlSet) {
            final TreeSet<Value> other = ((SetlSet) v).set;
            if (set.size() == other.size()) {
                final Iterator<Value> iterFirst  = set.iterator();
                final Iterator<Value> iterSecond = other.iterator();
                while (iterFirst.hasNext() && iterSecond.hasNext()) {
                    if ( ! iterFirst.next().equalTo(iterSecond.next())) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private final static int initHashCode = SetlSet.class.hashCode();

    @Override
    public int hashCode() {
        final int size = set.size();
        int hash = initHashCode + size;
        if (size >= 1) {
            hash = hash * 31 + set.first().hashCode();
            if (size >= 2) {
                hash = hash * 31 + set.last().hashCode();
            }
        }
        return hash;
    }
}


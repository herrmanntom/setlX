package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressionUtilities.ExplicitListWithRest;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Collection;
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
     * MUST be called before the modification.  This method then performs the actual
     * cloning, if required.
     *
     * Main benefit of this technique is to perform the actual cloning only
     * when a clone is actually modified, thus not performing a time consuming
     * cloning, when the clone is only used read-only, which it is in most cases.
     */

    private TreeSet<Value> set;
    private boolean        isCloned; // is this set a clone?

    /**
     * Create a new empty set.
     */
    public SetlSet() {
        this.set      = new TreeSet<Value>();
        this.isCloned = false; // new sets are not a clone
    }

    /**
     * Create a new set of permutations generated by SetlLst.permutations().
     *
     * @param permutations List of permutations
     */
    /*package*/ SetlSet(final Collection<SetlList> permutations) {
        this.set      = new TreeSet<Value>(permutations);
        this.isCloned = false; // this set is not a clone
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
     * be called before the modification. It performs the actual cloning,
     * if THIS is actually marked as a clone.
     *
     * While clone() is called upon all members of this set, this does not perform
     * a `deep' cloning, as the members themselves are only marked for cloning.
     */
    private void separateFromOriginal() {
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
        if (other.getClass()== SetlSet.class) {
            final SetlSet otr = (SetlSet) other;
            return SetlBoolean.valueOf((otr.set.containsAll(set)) && this.isEqualTo(state, otr) == SetlBoolean.FALSE);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " < " + other.toString(state) + "' is not a set."
            );
        }
    }

    /* type checks (sort of boolean operation) */

    @Override
    public SetlBoolean isMap() {
        final TreeSet<Value> temp = new TreeSet<Value>();
        for (final Value v: set) {
            if (v.getClass() == SetlList.class) {
                final SetlList list = (SetlList) v;
                if (list.size() == 2 && temp.add(list.firstMember())) {
                    continue;
                }
            }
            return SetlBoolean.FALSE;
        }
        return SetlBoolean.TRUE;
    }

    @Override
    public SetlBoolean isSet() {
        return SetlBoolean.TRUE;
    }

    /* type conversion */

    /**
     * Convert this set into a list.
     *
     * @return SetlList of the members in this set.
     */
    /*package*/ SetlList toList() {
        return new SetlList(new ArrayList<Value>(set));
    }

    /* arithmetic operations */

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend.getClass() == SetlSet.class) {
            final SetlSet result = clone();
            result.separateFromOriginal();
            result.set.removeAll(((SetlSet) subtrahend).set);
            return result;
        } else if (subtrahend.getClass() == Term.class) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " - " + subtrahend.toString(state) + "' is not a set."
            );
        }
    }

    @Override
    public Value differenceAssign(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend.getClass() == SetlSet.class) {
            separateFromOriginal();
            set.removeAll(((SetlSet) subtrahend).set);
            return this;
        } else if (subtrahend.getClass() == Term.class) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " -= " + subtrahend.toString(state) + "' is not a set."
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
        if (modulo.getClass() == SetlSet.class) {
            final SetlSet mSet = (SetlSet) modulo;

            final SetlSet mClone = mSet.clone();
            mClone.separateFromOriginal();
            mClone.set.removeAll(this.set);

            final SetlSet result = clone();
            result.separateFromOriginal();
            result.set.removeAll(mSet.set);

            result.set.addAll(mClone.set);
            return result;
        } else if (modulo.getClass() == Term.class) {
            return ((Term) modulo).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " % " + modulo.toString(state) + "' is not a set."
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
        if (modulo.getClass() == SetlSet.class) {
            final SetlSet mSet = (SetlSet) modulo;
            separateFromOriginal();

            final SetlSet mClone = mSet.clone();
            mClone.separateFromOriginal();
            mClone.set.removeAll(this.set);

            this.set.removeAll(mSet.set);

            this.set.addAll(mClone.set);

            return this;
        } else if (modulo.getClass() == Term.class) {
            return ((Term) modulo).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " % " + modulo.toString(state) + "' is not a set."
            );
        }
    }

    @Override
    public Value power(final State state, final Value exponent) throws SetlException {
        if (exponent.isNumber() == SetlBoolean.TRUE && exponent.equalTo(Rational.TWO)) {
            try {
                return this.cartesianProduct(state, this);
            } catch (final SetlException se) {
                se.addToTrace("Error in substitute operation \"" + this.toString(state) + " >< " + this.toString(state) +  "\":");
                throw se;
            }
        } else if (exponent.getClass() == Term.class) {
            return ((Term) exponent).powerFlipped(state, this);
        }
        throw new IncompatibleTypeException(
            "Left-hand-side of '" + this.toString(state) + " ** " + exponent.toString(state) + "' is not a number."
        );
    }

    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier.getClass() == SetlSet.class) {
            final SetlSet result = clone();
            result.separateFromOriginal();
            result.set.retainAll(((SetlSet) multiplier).set);
            return result;
        } else if (multiplier.getClass() == Term.class) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " * " + multiplier.toString(state) + "' is not a set."
            );
        }
    }

    @Override
    public Value productAssign(final State state, final Value multiplier) throws SetlException {
        if (multiplier.getClass() == SetlSet.class) {
            separateFromOriginal();
            set.retainAll(((SetlSet) multiplier).set);
            return this;
        } else if (multiplier.getClass() == Term.class) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " * " + multiplier.toString(state) + "' is not a set."
            );
        }
    }

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand.getClass() == Term.class) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand.getClass() == SetlString.class) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else if(summand instanceof CollectionValue) {
            final SetlSet result = this.clone();
            for (final Value v: (CollectionValue) summand) {
                result.addMember(state, v.clone());
            }
            return result;
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " + " + summand.toString(state) + "' is not a set or string."
            );
        }
    }

    @Override
    public Value sumAssign(final State state, final Value summand) throws SetlException {
        if (summand.getClass() == Term.class) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand.getClass() == SetlString.class) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else if(summand instanceof CollectionValue) {
            separateFromOriginal();
            for (final Value v: (CollectionValue) summand) {
                addMember(state, v.clone());
            }
            return this;
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " += " + summand.toString(state) + "' is not a set or string."
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
        if (other.getClass() == SetlSet.class) {

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
        } else if (other.getClass() == Term.class) {
            return ((Term) other).cartesianProductFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " >< " + other.toString(state) + "' is not a set."
            );
        }
    }

    @Override
    public Value collectionAccess(final State state, final List<Value> args) throws SetlException {
        if (args.contains(RangeDummy.RD)) {
            throw new UndefinedOperationException(
                "Range operations are unsupported on '" + this.toString(state) + "'."
            );
        } else if (args.size() != 1) {
            throw new UndefinedOperationException(
                "Can not access elements using arguments '" + args + "' on '" + this.toString(state) + "';" +
                " Exactly one argument is required."
            );
        }
        return getMember(state, args.get(0));
    }

    @Override
    public Value collectionAccessUnCloned(final State state, final List<Value> args) throws SetlException {
        if (args.contains(RangeDummy.RD)) {
            throw new UndefinedOperationException(
                "Range operations are unsupported on '" + this.toString(state) + "'."
            );
        } else if (args.size() != 1) {
            throw new UndefinedOperationException(
                "Can not access elements using arguments '" + args + "' on '" + this.toString(state) + "';" +
                " Exactly one argument is required."
            );
        }
        return getMemberUnCloned(state, args.get(0));
    }

    @Override
    public Value collectMap(final State state, final Value arg) throws SetlException {
        /*
         * When this function is invoked, this set is expected to be a map.
         *
         * This map is represented with SetlList members with exactly two members,
         * the first one being the key and the second one being the value,
         * i.e. {[key1, value1], [key2, value2], ... , [key3, value3]}
         *
         * Thus this function get all map entries
         *                      [`arg', x]
         * in this set and returns a set of all values for x.
         *
         * Thus we extract a subset that satisfies the following equation:
         *
         * [`arg'] <= subset <= [`arg', Top.TOP]
         *
         * which includes all map entries where key == `arg', as this set is
         * in lexicographical order and
         *
         * [`arg'] <= [`arg', x] <= [`arg', Top.TOP]
         *
         * for all x, because all lists with two members are larger as ones with
         * one, if the first member is equal AND all x are smaller as Top.TOP,
         * as that is the largest value possible.
         */
        final Value lowerBound  = new SetlList(1);
        lowerBound.addMember(state, arg);
        final Value upperBound  = new SetlList(2);
        upperBound.addMember(state, arg);
        upperBound.addMember(state, Top.TOP);

        final NavigableSet<Value> subSet = set.subSet(lowerBound, false, upperBound, true);

        /*
         * The extracted subset is now checked for consistency, i.e. if all members
         * in the subset are SetlList with exactly two members.
         *
         * If it does, all values from the map entries found will be added to
         * the resulting set.
         */

        final SetlSet result = new SetlSet();
        for (final Value v: subSet) {
            if (v.getClass() == SetlList.class && v.size() == 2) {
                result.addMember(state, v.lastMember(state));
            } else {
                throw new IncompatibleTypeException(
                    "'" + this.toString(state) + "' is not a map."
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
            if (v.getClass() == SetlList.class && v.size() == 2) {
                result.add(v.firstMember(state));
            } else {
                throw new IncompatibleTypeException(
                    "'" + this.toString(state) + "' is not a map."
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
    public Value getMember(final State state, final Value index) throws SetlException {
        return getMemberZZZInternal(state, index).clone();
    }

    /**
     * Get a specified member of this list, but return it without cloning.
     *
     * @param state          Current state of the running setlX program.
     * @param index          Index of the member to get. Note: Index starts with 1, not 0.
     * @return               Member of this list at the specified index.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value getMemberUnCloned(final State state, final Value index) throws SetlException {
        separateFromOriginal();
        return getMemberZZZInternal(state, index);
    }

    private Value getMemberZZZInternal(final State state, final Value element) throws SetlException {
        /*
         * When this function is invoked, this set is expected to be a functional relation,
         * i.e. a map where each "key" only occurs once.
         *
         * This map is represented with SetlList members with exactly two members,
         * the first one being the key and the second one being the value,
         * i.e. {[key1, value1], [key2, value2], ... , [key3, value3]}
         *
         * Thus this function gets the specific map entry
         *                      [`element', x]
         * in this set and returns the value of this entry (x).
         *
         * Thus we extract a subset that satisfies the following equation:
         *
         * [`element'] <= subset <= [`element', Top.TOP]
         *
         * which includes all map entries where key == `element', as this set is
         * in lexicographical order and
         *
         * [`element'] <= [`element', x] <= [`element', Top.TOP]
         *
         * for all x, because all lists with two members are larger as ones with
         * one, if the first member is equal AND all x are smaller as Top.TOP,
         * as that is the largest value possible.
         */
        final Value lowerBound  = new SetlList(1);
        lowerBound.addMember(state, element);
        final Value upperBound  = new SetlList(2);
        upperBound.addMember(state, element);
        upperBound.addMember(state, Top.TOP);

        final NavigableSet<Value> subSet = set.subSet(lowerBound, false, upperBound, true);

        /*
         * The extracted subset is now checked for consistency with, i.e. if it
         * satisfies the functional relation.
         *
         * If it does, the iteration will only be over one map entry, which value
         * is returned. If there either none or two+ entries om is returned.
         */
        Value result = Om.OM;
        for (final Value v: subSet) {
            if (v.getClass() == SetlList.class && v.size() == 2) {
                if (result == Om.OM) {
                    result = ((SetlList) v).getMemberUnCloned(2);
                } else {
                    // double match!
                    result = Om.OM;
                    break;
                }
            } else {
                throw new IncompatibleTypeException(
                    "'" + this.toString(state) + "' is not a map."
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
    public Value maximumMember(final State state) throws UndefinedOperationException {
        if (size() < 1) {
            // Neutral element of max() is smallest number available
            return SetlDouble.NEGATIVE_INFINITY;
        }
        final Value a = firstMember(state);
        final Value b = lastMember(state);
        if (a.isNumber().equalTo(SetlBoolean.FALSE) || b.isNumber().equalTo(SetlBoolean.FALSE)) {
            throw new UndefinedOperationException(
                    "The set " + this.toString(state) + " is not a set of numbers."
            );
        }
        return b;
    }

    @Override
    public Value minimumMember(final State state) throws UndefinedOperationException {
        // The minimum is only defined for sets of numbers.
        if (size() < 1) {
            // Neutral element of min() is the largest number available.
            return SetlDouble.POSITIVE_INFINITY;
        }
        final Value a = firstMember(state);
        final Value b = lastMember(state);
        if (a.isNumber().equalTo(SetlBoolean.FALSE) || b.isNumber().equalTo(SetlBoolean.FALSE)) {
            throw new UndefinedOperationException(
                    "The set " + this.toString(state) + " is not a set of numbers."
            );
        }
        return a;
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
        if (state.executionStopped) {
            throw new StopExecutionException();
        }
        if (size() == 0) {
            final SetlSet power = new SetlSet();
            power.addMember(state, clone());
            return power;
        }
        // get some arbitrary member
        final Value      arb       = set.iterator().next();
        final SetlSet    rest      = clone();
        rest.removeMember(state, arb);
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
            if (v.getClass() == SetlList.class && v.size() == 2) {
                result.addMember(state, v.lastMember(state));
            } else {
                throw new IncompatibleTypeException(
                    "'" + this.toString(state) + "' is not a map."
                );
            }
        }
        return result;
    }

    @Override
    public void setMember(final State state, final Value index, final Value v) throws SetlException {
        /*
         * When this function is invoked, this set is expected to be a functional relation,
         * i.e. a map where each "key" only occurs once.
         *
         * This map is represented with SetlList members with exactly two members,
         * the first one being the key and the second one being the value,
         * i.e. {[key1, value1], [key2, value2], ... , [key3, value3]}
         *
         * Thus this functions adds/sets the specific map entry
         *                      [`index', `v']
         * to this set.
         */

        separateFromOriginal();

        /*
         * To ensure and preserve the functional relation, any previous mappings
         * for the with the key == `index' are removed.
         *
         * Thus we extract a subset that satisfies the following equation:
         *
         * [`index'] <= subset <= [`index', Top.TOP]
         *
         * which includes all map entries where key == `index', as this set is
         * in lexicographical order and
         *
         * [`index'] <= [`index', x] <= [`index', Top.TOP]
         *
         * for all x, because all lists with two members are larger as ones with
         * one, if the first member is equal AND all x are smaller as Top.TOP,
         * as that is the largest value possible.
         */
        final Value lowerBound  = new SetlList(1);
        lowerBound.addMember(state, index);
        final Value upperBound  = new SetlList(2);
        upperBound.addMember(state, index);
        upperBound.addMember(state, Top.TOP);

        // remove all previously map entries which key == `index'
        set.removeAll(new TreeSet<Value>(set.subSet(lowerBound, true, upperBound, true)));

        // now this set must either be empty or a map without an entry with `index' as key
        if (v != Om.OM) {
            // add new map entry [`index', `v'] to this set
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
    public void removeMember(final State state, final Value element) {
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
        TermConverter.valueToCodeFragment(state, this, false).appendString(state, sb, 0);
    }

    @Override
    public void canonical(final State state, final StringBuilder sb) {
        sb.append("{");
        final Iterator<Value> iter = iterator();
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
    public MatchResult matchesTerm(final State state, final Value other) throws SetlException {
        if (other == IgnoreDummy.ID) {
            return new MatchResult(true);
        } else if (other.getClass() != SetlSet.class) {
            return new MatchResult(false);
        } else if (set.size() == 1 && set.first().getClass() == Term.class) {
            final MatchResult result = ExplicitListWithRest.matchTerm(state, (Term) set.first(), (SetlSet) other);
            if (result.isMatch()) {
                return result;
            }
        }

        if (this.size() != other.size()) {
            return new MatchResult(false);
        }

        // first match all atomic values
        final TreeSet<Value> thisCopy  = new TreeSet<Value>(set);
        final TreeSet<Value> otherCopy = new TreeSet<Value>(((SetlSet) other).set);

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
        final SetlList  thisList         = new SetlList(new ArrayList<Value>(thisCopy));
        // permute remaining members from `other'
              Value     otherPermutation = new SetlList(new ArrayList<Value>(otherCopy));

        // both set match, when (at least) one permutation matches
        while (otherPermutation != Om.OM) {
            MatchResult match = thisList.matchesTerm(state, otherPermutation);
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
    public Value toTerm(final State state) throws SetlException {
        final SetlSet termSet = new SetlSet();
        for (final Value v: set) {
            termSet.addMember(state, v.toTerm(state));
        }
        return termSet;
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == SetlSet.class) {
            final TreeSet<Value> otherSet = ((SetlSet) other).set;
            if (set == otherSet) {
                return 0; // clone
            }
            final Iterator<Value> iterFirst  = set.iterator();
            final Iterator<Value> iterSecond = otherSet.iterator();
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                final int cmp = iterFirst.next().compareTo(iterSecond.next());
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
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetlSet.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        if (this == other) {
            return true;
        } else if (other.getClass() == SetlSet.class) {
            final TreeSet<Value> otherSet = ((SetlSet) other).set;
            if (set == otherSet) {
                return true; // clone
            } else if (set.size() == otherSet.size()) {
                final Iterator<Value> iterFirst  = set.iterator();
                final Iterator<Value> iterSecond = otherSet.iterator();
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

    @Override
    public int hashCode() {
        final int size = set.size();
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + size;
        if (size >= 1) {
            hash = hash * 31 + set.first().hashCode();
            if (size >= 2) {
                hash = hash * 31 + set.last().hashCode();
            }
        }
        return hash;
    }
}

package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressionUtilities.ExplicitListWithRest;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class implements a list of arbitrary SetlX values.
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
public class SetlList extends IndexedCollectionValue {
    /* To allow initially `free' cloning, by only marking a clone without
     * actually doing any cloning, this list carries a isClone flag.
     *
     * If the contents of this SetlList is modified `separateFromOriginal()'
     * MUST be called before the modification, which then performs the actual
     * cloning, if required.
     *
     * Main benefit of this technique is to perform the actual cloning only
     * when a clone is actually modified, thus not performing a time consuming
     * cloning, when the clone is only used read-only, which it is in most cases.
     */

    private ArrayList<Value>    list;
    // is this list a clone
    private boolean             isCloned;

    /**
     * Create a new empty list.
     */
    public SetlList() {
        this(10);
    }

    /**
     * Create a new empty list, allocating space for the given number of elements.
     *
     * @param initialCapacity Number of elements to store without resizing.
     */
    public SetlList(final int initialCapacity) {
        this.list     = new ArrayList<Value>(initialCapacity);
        this.isCloned = false; // new lists are not a clone
    }

    /**
     * Create a new list, cloning from another one.
     *
     * @param list List to clone from.
     */
    /*package*/ SetlList(final ArrayList<Value> list) {
        this.list     = list;
        this.isCloned = true;  // lists created from another list ARE a clone
    }

    @Override
    public SetlList clone() {
        /* When cloning, THIS list is marked to be a clone as well.
         *
         * This is done, because even though THIS is the original, it must also be
         * cloned upon modification, otherwise clones which carry the same
         * member list of THIS list would not notice, e.g.
         * modifications of THIS original would bleed through to the clones.
         */
        isCloned = true;
        return new SetlList(this.list);
    }

    /**
     * If the contents of THIS SetlList is modified, the following function MUST
     * be called before the modification. It performs the actual cloning,
     * if THIS is actually marked as a clone.
     *
     * While clone() is called upon all members of this list, this does not perform
     * a `deep' cloning, as the members themselves are only marked for cloning.
     */
    private void separateFromOriginal() {
        if (isCloned) {
            final ArrayList<Value> original = this.list;
            this.list = new ArrayList<Value>(original.size());
            for (final Value v: original) {
                this.list.add(v.clone());
            }
            isCloned = false;
        }
    }

    @Override
    public Iterator<Value> iterator() {
        return this.list.iterator();
    }

    private class SetlListDecendingIterator implements Iterator<Value> {
        private final SetlList         listShell;
        private final ArrayList<Value> content;
        private       int              size;
        private       int              position;

        private SetlListDecendingIterator(final SetlList listShell) {
            this.listShell = listShell;
            this.content   = listShell.list;
            this.size      = this.content.size();
            this.position  = this.size - 1;
        }

        @Override
        public boolean hasNext() {
            return 0 < position;
        }

        @Override
        public Value next() {
            return content.get(position--);
        }

        @Override
        public void remove() {
            listShell.separateFromOriginal();
            content.remove(position--);
            size = content.size();
        }
    }

    @Override
    public Iterator<Value> descendingIterator() {
        return new SetlListDecendingIterator(this);
    }

    /**
     * Trims the capacity of this list to be its current size.
     * Trailing members with a value of OM are removed.
     */
    public void compress() {
        int size    = list.size();
        int removed = 0;
        while (size > 0 && list.get(size - 1) == Om.OM) {
            list.remove(size - 1);
            ++removed;
            size = list.size();
        }
        if (size < removed) {
            list.trimToSize();
        }
    }

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isList() {
        return SetlBoolean.TRUE;
    }

    /* arithmetic operations */

    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier instanceof Rational) {
            final int m = multiplier.jIntValue();
            if (m < 0) {
                throw new IncompatibleTypeException(
                    "List multiplier '" + multiplier + "' is negative."
                );
            }
            final SetlList result = new SetlList(size() * m);
            for (int i = 0; i < m; ++i) {
                for(final Value v : this) {
                    result.addMember(state, v.clone());
                }
            }
            return result;
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "List multiplier '" + multiplier + "' is not an integer."
            );
        }
    }

    @Override
    public Value productAssign(final State state, final Value multiplier) throws SetlException {
        if (multiplier instanceof Rational) {
            final int m = multiplier.jIntValue();
            if (m < 0) {
                throw new IncompatibleTypeException(
                    "List multiplier '" + multiplier + "' is negative."
                );
            }
            separateFromOriginal();

            final ArrayList<Value> content = this.list;
            this.list = new ArrayList<Value>(content.size() * m);
            for (int i = 0; i < m; ++i) {
                for(final Value v : content) {
                    this.list.add(v.clone());
                }
            }
            return this;
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "List multiplier '" + multiplier + "' is not an integer."
            );
        }
    }

    @Override
    public Value sum(final State state, final Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else if (summand instanceof CollectionValue) {
            final ArrayList<Value> list = new ArrayList<Value>(this.list.size() + summand.size());
            for (final Value v: this.list) {
                list.add(v.clone());
            }
            for (final Value v: (CollectionValue) summand) {
                list.add(v.clone());
            }
            final SetlList result = new SetlList(list);
            // we already cloned all values...
            result.isCloned = false;
            return result;
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand + "' is not a list or string."
            );
        }
    }

    @Override
    public Value sumAssign(final State state, final Value summand) throws SetlException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else if (summand instanceof CollectionValue) {
            separateFromOriginal();
            for (final Value v: (CollectionValue) summand) {
                list.add(v.clone());
            }
            return this;
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " += " + summand + "' is not a list or string."
            );
        }
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    @Override
    public void addMember(final State state, final Value element) {
        separateFromOriginal();
        list.add(element.clone());
    }

    @Override
    public Value cartesianProduct(final State state, final Value otr) throws SetlException {
        // zip := procedure(xs, ys) {
        //      assert(#xs == #ys, "list of different size");
        //      return [ [xs[i], ys[i]] : i in [1..#xs] ];
        // };
        if (otr instanceof SetlList) {
            final SetlList other = (SetlList) otr;
            final int      size  = this.size();
            if (size != other.size()) {
                throw new UndefinedOperationException(
                    "Both sides of '" + this + " >< " + other + "' are not of equal length."
                );
            }

            final SetlList result = new SetlList(size);

            for (int i = 1; i <= size; ++i) {
                final SetlList tuple = new SetlList(2);
                tuple.addMember(state, getMember(i));
                tuple.addMember(state, other.getMember(i));
                result.addMember(state, tuple);
            }

            return result;
        } else if (otr instanceof Term) {
            return ((Term) otr).cartesianProductFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " >< " + otr + "' is not a list."
            );
        }
    }

    /**
     * Create a setlX map of all contained members, with unique valued used
     * as key and the number of their occurrences as values.
     *
     * @param state Current state of the running setlX program.
     * @return      Collection of all contained members.
     */
    public SetlSet collect(final State state) {
        final HashMap<Value, Integer> map        = new HashMap<Value, Integer>();
              Integer                 occurences = null;
        for (final Value v : list) {
            occurences = map.get(v);
            if (occurences == null) {
                map.put(v, 1);
            } else {
                map.put(v, ++occurences);
            }
        }
        final SetlSet result = new SetlSet();
        for (final Map.Entry<Value, Integer> entry : map.entrySet()) {
            final SetlList member = new SetlList(2);
            member.addMember(state, entry.getKey());
            member.addMember(state, Rational.valueOf(entry.getValue()));
            result.addMember(state, member);
        }
        return result;
    }

    @Override
    public Value collectionAccess(final State state, final List<Value> args) throws SetlException {
        final int   aSize  = args.size();
        final Value vFirst = (aSize >= 1)? args.get(0) : null;
        if (args.contains(RangeDummy.RD)) {
            if (aSize == 2 && vFirst == RangeDummy.RD) {
                // everything up to high boundary: this(  .. y);
                return getMembers(state, Rational.ONE, args.get(1));

            } else if (aSize == 2 && args.get(1) == RangeDummy.RD) {
                // everything from low boundary:   this(x ..  );
                return getMembers(state, vFirst, Rational.valueOf(size()));

            } else if (aSize == 3 && args.get(1) == RangeDummy.RD) {
                // full range specification:                this(x .. y);
                return getMembers(state, vFirst, args.get(2));
            }
            throw new UndefinedOperationException(
                "Can not access elements using the arguments '" + args + "' on '" + this + "';" +
                " arguments are malformed."
            );
        } else if (aSize == 1) {
            return getMember(state, vFirst);
        } else {
            throw new UndefinedOperationException(
                "Can not access elements using the arguments '" + args + "' on '" + this + "';" +
                " arguments are malformed."
            );
        }
    }

    @Override
    public Value collectionAccessUnCloned(final State state, final List<Value> args) throws SetlException {
        if (args.contains(RangeDummy.RD)) {
            // uncloned access is only used in assignments, so we should never get here
            throw new UndefinedOperationException(
                "Can not access elements using the arguments '" + args + "' on '" + this + "';" +
                " range is not allowed in assignments."
            );
        } else if (args.size() == 1) {
            return getMemberUnCloned(state, args.get(0));
        } else {
            // uncloned access is only used in assignments, so we should never get here
            throw new UndefinedOperationException(
                "Can not access elements using the arguments '" + args + "' on '" + this + "';" +
                " arguments are malformed."
            );
        }
    }

    @Override
    public SetlBoolean containsMember(final State state, final Value element) {
        return SetlBoolean.valueOf(list.contains(element));
    }

    @Override
    public Value firstMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return list.get(0).clone();
    }

    @Override
    public Value getMember(final int index) throws SetlException {
        return getMemberZZZInternal(index).clone();
    }

    @Override
    public Value getMember(final State state, final Value index) throws SetlException {
        return getMemberZZZInternal(index).clone();
    }

    /**
     * Get a specified member of this list, but return it without cloning.
     *
     * @param index          Index of the member to get. Note: Index starts with 1, not 0.
     * @return               Member of this list at the specified index.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    /*package*/ Value getMemberUnCloned(final int index) throws SetlException {
        separateFromOriginal();
        return getMemberZZZInternal(index);
    }

    @Override
    public Value getMemberUnCloned(final State state, final Value index) throws SetlException {
        separateFromOriginal();
        return getMemberZZZInternal(index);
    }

    private Value getMemberZZZInternal(final Value vIndex) throws SetlException {
        int index = 0;
        if (vIndex.isInteger() == SetlBoolean.TRUE) {
            index = vIndex.jIntValue();
        } else {
            throw new IncompatibleTypeException(
                "Index '" + vIndex + "' is not a integer."
            );
        }
        return getMemberZZZInternal(index);
    }

    private Value getMemberZZZInternal(final int index) throws NumberToLargeException {
        final int indexFromStart;
        if (index == 0) {
            throw new NumberToLargeException(
                    "Index '" + index + "' is invalid."
                );
        } else if (index > 0) {
            indexFromStart = index;
        } else /* if (index < 0) */ {
            // negative index counts from end of the list - convert it to actual index
            indexFromStart = list.size() + index + 1;
        }

        if (indexFromStart < 1 || indexFromStart > list.size()) {
            return Om.OM;
        } else {
            return list.get(indexFromStart - 1);
        }
    }

    @Override
    public Value getMembers(final State state, final Value vLow, final Value vHigh) throws SetlException {
        final int listSize = list.size();
        final int lowFromStart;
        final int highFromStart;
        if (vLow.isInteger() == SetlBoolean.TRUE) {
            final int low = vLow.jIntValue();
            if (low == 0) {
                throw new NumberToLargeException(
                    "Lower bound '" + vLow + "' is invalid."
                );
            } else if (low > 0) {
                lowFromStart = low;
            } else /* if (low < 0) */ {
                // negative index counts from end of the string - convert it to actual index
                lowFromStart = listSize + low + 1;
            }
        } else {
            throw new IncompatibleTypeException(
                "Lower bound '" + vLow + "' is not a integer."
            );
        }
        if (vHigh.isInteger() == SetlBoolean.TRUE) {
            final int high = vHigh.jIntValue();
            if (high >= 0) {
                highFromStart = high;
            } else /* if (high < 0) */ {
                // negative index counts from end of the string - convert it to actual index
                highFromStart = listSize + high + 1;
            }
        } else {
            throw new IncompatibleTypeException(
                "Upper bound '" + vHigh + "' is not a integer."
            );
        }

        int size = highFromStart - (lowFromStart - 1);
        if (size < 0 || lowFromStart < 1 || highFromStart < 1 || lowFromStart > listSize) {
            size = 0;
        }
        final SetlList result = new SetlList(size);
                     // in java the index is one lower
        for (int i = lowFromStart - 1; size > 0 && i < highFromStart && i < listSize; ++i) {
            result.addMember(state, list.get(i).clone());
        }
        return result;
    }

    @Override
    public Value lastMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return list.get(list.size() - 1).clone();
    }

    @Override
    public Value maximumMember(final State state) throws SetlException {
        // Neutral element of max() is smallest number available
        Value max = SetlDouble.NEGATIVE_INFINITY;
        for (final Value v: list) {
            // we assume that all elements are numbers
            if (v.isNumber().equalTo(SetlBoolean.FALSE)) {
                final String errMsg = "The list " + this + " is not a list of numbers.";
                throw new IncompatibleTypeException(errMsg);
            }
            if (v.maximum(state, max).equals(v)) {
                max = v;
            }
        }
        return max;
    }

    @Override
    public Value minimumMember(final State state) throws SetlException {
        // Neutral element of min() is largest number available
        Value min = SetlDouble.POSITIVE_INFINITY;
        for (final Value v: list) {
            // we assume that all elements are numbers
            if (v.isNumber().equalTo(SetlBoolean.FALSE)) {
                final String errMsg = "The list " + this + " is not a list of numbers.";
                throw new IncompatibleTypeException(errMsg);
            }
            if (v.minimum(state, min).equals(v)) {
                min = v;
            }
        }
        return min;
    }

    @Override
    public Value nextPermutation(final State state) throws SetlException {
        if (size() < 2) {
            return Om.OM;
        }

        final ArrayList<Value> p = new ArrayList<Value>(list);

        // Inspired by permutation from
        // http://code.google.com/p/algorithms-java/source/browse/trunk/src/main/java/com/google/code/Permutations.java?r=3
        int a = p.size() - 2;
        while (a >= 0 && p.get(a).compareTo(p.get(a + 1)) >= 0) {
            a--;
        }

        if (a == -1) {
            // this is already the last permutation
            return Om.OM;
        }

        int b = p.size() - 1;
        while (p.get(b).compareTo(p.get(a)) <= 0) {
            b--;
        }

        Value tmp = p.get(a);
        p.set(a, p.get(b));
        p.set(b, tmp);
        for (int i = a + 1, j = p.size() - 1; i < j; ++i, --j) {
            tmp = p.get(i);
            p.set(i,p.get(j));
            p.set(j,tmp);
        }

        return new SetlList(p);
    }

    @Override
    public SetlSet permutations(final State state) throws SetlException {
        if (state.executionStopped) {
            throw new StopExecutionException();
        }
        if (size() == 0) {
            final SetlSet permutations = new SetlSet();
            permutations.addMember(state, clone());
            return permutations;
        }
        final SetlList  rest            = clone();
        final Value     last            = rest.removeLastMember();
        final SetlSet   permutatateRest = rest.permutations(state);
        final SetlSet   permutations    = new SetlSet();
        for (final Value permutation : permutatateRest) {
            final int size = permutation.size();
            for (int i = 0; i <= size; i++) {
                final SetlList  perm    = (SetlList) permutation.clone();
                perm.separateFromOriginal();
                perm.list.add(i, last.clone());
                permutations.addMember(state, perm);
            }
        }
        return permutations;
    }

    @Override
    public void removeMember(final State state, final Value element) {
        separateFromOriginal();
        list.remove(element);
        compress();
    }

    @Override
    public Value removeFirstMember() {
        if (size() < 1) {
            return Om.OM;
        }
        separateFromOriginal();
        final Value result = list.remove(0);
        compress();
        return result;
    }

    @Override
    public Value removeLastMember() {
        final int index = list.size() - 1;
        if (index < 0) {
            return Om.OM;
        }
        separateFromOriginal();
        final Value result = list.remove(index);
        compress();
        return result;
    }

    @Override
    public SetlList reverse(final State state) {
        final int               size    = list.size();
        // mark this list to be clone
        isCloned = true;
        // create reversed clone of this list
        final ArrayList<Value>  reverse = new ArrayList<Value>(size);
        for (int i = size - 1; i >= 0; --i) {
            reverse.add(list.get(i));
        }
        return new SetlList(reverse);
    }

    @Override
    public void setMember(final State state, final Value vIndex, final Value v) throws SetlException {
        separateFromOriginal();
        int index = 0;
        if (vIndex.isInteger() == SetlBoolean.TRUE) {
            index = vIndex.jIntValue();
        } else {
            throw new IncompatibleTypeException(
                "Index '" + vIndex + "' is not a integer."
            );
        }
        if (index < 1) {
            throw new NumberToLargeException(
                "Index '" + index + "' is lower as '1'."
            );
        }

        // in java the index is one lower
        --index;

        if (index >= list.size()) {
            if (v == Om.OM) {
                return; // nothing to do
            } else {
                list.ensureCapacity(index + 1);
                // fill gap from size to index with OM, if necessary
                while (index >= list.size()) {
                    list.add(Om.OM);
                }
            }
        }

        // set index to value
        list.set(index, v);

        if (v == Om.OM) {
            compress();
        }
    }

    @Override
    public SetlList shuffle(final State state) {
        final ArrayList<Value> list = new ArrayList<Value>(this.list);
        Collections.shuffle(list, state.getRandom());
        return new SetlList(list);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public SetlList sort(final State state) {
        final ArrayList<Value> list = new ArrayList<Value>(this.list);
        Collections.sort(list);
        return new SetlList(list);
    }

    @Override
    public SetlList split(final State state, final Value pattern) throws IncompatibleTypeException {
        final SetlList  result    = new SetlList();
              SetlList  subResult = new SetlList();
        for (final Value v : list) {
            if (v.equals(pattern)) {
                result.addMember(state, subResult);
                subResult = new SetlList();
            } else {
                subResult.addMember(state, v.clone());
            }
        }

        result.addMember(state, subResult);

        return result;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        TermConverter.valueToCodeFragment(state, this, false).appendString(state, sb, 0);
    }

    @Override
    public void canonical(final State state, final StringBuilder sb) {
        canonical(state, sb, true);
    }

    /**
     * Appends an uninterpreted string representation of this list to the given
     * StringBuilder object.
     *
     * @param state       Current state of the running setlX program.
     * @param sb          StringBuilder to append to.
     * @param addBracktes Append enclosing brackes of the list.
     */
    public void canonical(final State state, final StringBuilder sb, final boolean addBracktes) {
        if (addBracktes) {
            sb.append("[");
        }

        final Iterator<Value> iter = iterator();
        while (iter.hasNext()) {
            iter.next().canonical(state, sb);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        if (addBracktes) {
            sb.append("]");
        }
    }

    /* term operations */

    @Override
    public MatchResult matchesTerm(final State state, final Value other) throws SetlException {
        if (other == IgnoreDummy.ID) {
            return new MatchResult(true);
        } else if ( ! (other instanceof SetlList || other instanceof SetlString)) {
            return new MatchResult(false);
        }
        final IndexedCollectionValue otherCollection = (IndexedCollectionValue) other;

        if (list.size() == 1 && list.get(0) instanceof Term) {
            final MatchResult result = ExplicitListWithRest.matchTerm(state, (Term) list.get(0), otherCollection);
            if (result.isMatch()) {
                return result;
            }
        }

        if (list.size() != otherCollection.size()) {
            return new MatchResult(false);
        }

        // match all members
        final MatchResult       result      = new MatchResult(true);
        final Iterator<Value>   thisIter    = iterator();
        final Iterator<Value>   otherIter   = otherCollection.iterator();
        while (thisIter.hasNext() && otherIter.hasNext() && result.isMatch()) {
            final MatchResult   subResult   = thisIter.next().matchesTerm(state, otherIter.next());
            if (subResult.isMatch()) {
                result.addBindings(subResult);
            } else {
                return new MatchResult(false);
            }
        }

        return result;
    }

    @Override
    public Value toTerm(final State state) {
        final SetlList termList = new SetlList(list.size());
        for (final Value v: list) {
            termList.addMember(state, v.toTerm(state));
        }
        return termList;
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof SetlList) {
            final Iterator<Value> iterFirst  = list.iterator();
            final Iterator<Value> iterSecond = ((SetlList) v).list.iterator();
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                final int cmp = iterFirst.next().compareTo(iterSecond.next());
                if (cmp != 0) {
                    return cmp;
                }
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
    public int compareToOrdering() {
        return 800;
    }

    @Override
    public boolean equalTo(final Object v) {
        if (this == v) {
            return true;
        } else if (v instanceof SetlList) {
            final ArrayList<Value> other = ((SetlList) v).list;
            if (list.size() == other.size()) {
                final Iterator<Value> iterFirst  = list.iterator();
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

    private final static int initHashCode = SetlList.class.hashCode();

    @Override
    public int hashCode() {
        final int size = list.size();
        int hash = initHashCode + size;
        if (size >= 1) {
            hash = hash * 31 + list.get(0).hashCode();
            if (size >= 2) {
                hash = hash * 31 + list.get(size-1).hashCode();
            }
        }
        return hash;
    }
}


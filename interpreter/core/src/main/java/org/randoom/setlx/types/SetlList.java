package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.ExplicitListWithRest;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.randoom.setlx.types.NumberValue.NUMERICAL_COMPARATOR;

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
        this.list     = new ArrayList<>(initialCapacity);
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
            this.list = new ArrayList<>(original.size());
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

    private static class SetlListDescendingIterator implements Iterator<Value> {
        private final SetlList         listShell;
        private final ArrayList<Value> content;
        private       int              position;

        private SetlListDescendingIterator(final SetlList listShell) {
            this.listShell = listShell;
            this.content   = listShell.list;
            this.position  = content.size() - 1;
        }

        @Override
        public boolean hasNext() {
            return 0 <= position;
        }

        @Override
        public Value next() {
            return content.get(position--);
        }

        @Override
        public void remove() {
            listShell.separateFromOriginal();
            content.remove(position + 1);
        }
    }

    @Override
    public Iterator<Value> descendingIterator() {
        return new SetlListDescendingIterator(this);
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
        if (multiplier.isRational() == SetlBoolean.TRUE) {
            final int m = multiplier.jIntValue();
            if (m < 0) {
                throw new IncompatibleTypeException(
                    "List multiplier '" + multiplier.toString(state) + "' is negative."
                );
            }
            final SetlList result = new SetlList(size() * m);
            for (int i = 0; i < m; ++i) {
                for(final Value v : this) {
                    result.addMember(state, v.clone());
                }
            }
            return result;
        } else if (multiplier.getClass() == Term.class) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "List multiplier '" + multiplier.toString(state) + "' is not an integer."
            );
        }
    }

    @Override
    public Value productAssign(final State state, final Value multiplier) throws SetlException {
        if (multiplier.isRational() == SetlBoolean.TRUE) {
            final int m = multiplier.jIntValue();
            if (m < 0) {
                throw new IncompatibleTypeException(
                    "List multiplier '" + multiplier.toString(state) + "' is negative."
                );
            }
            separateFromOriginal();

            final ArrayList<Value> content = this.list;
            this.list = new ArrayList<>(content.size() * m);
            for (int i = 0; i < m; ++i) {
                for(final Value v : content) {
                    this.list.add(v.clone());
                }
            }
            return this;
        } else if (multiplier.getClass() == Term.class) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "List multiplier '" + multiplier.toString(state) + "' is not an integer."
            );
        }
    }

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand.getClass() == Term.class) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand.getClass() == SetlString.class) {
            return ((SetlString) summand).sumFlipped(state, this);
        } else if (summand instanceof CollectionValue) {
            final ArrayList<Value> list = new ArrayList<>(this.list.size() + summand.size());
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
                "Right-hand-side of '" + this.toString(state) + " + " + summand.toString(state) + "' is not a list or string."
            );
        }
    }

    @Override
    public Value sumAssign(final State state, final Value summand) throws SetlException {
        if (summand.getClass() == Term.class) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand.getClass() == SetlString.class) {
            return ((SetlString)summand).sumFlipped(state, this);
        } else if (summand instanceof CollectionValue) {
            separateFromOriginal();
            for (final Value v: (CollectionValue) summand) {
                list.add(v.clone());
            }
            return this;
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " += " + summand.toString(state) + "' is not a list or string."
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
    public Value cartesianProduct(final State state, final Value other) throws SetlException {
        // zip := procedure(xs, ys) {
        //      assert(#xs == #ys, "list of different size");
        //      return [ [xs[i], ys[i]] : i in [1..#xs] ];
        // };
        if (other.getClass() == SetlList.class) {
            final SetlList setlList = (SetlList) other;
            final int      size  = this.size();
            if (size != setlList.size()) {
                throw new UndefinedOperationException(
                    "Both sides of '" + this.toString(state) + " >< " + setlList.toString(state) + "' are not of equal length."
                );
            }

            final SetlList result = new SetlList(size);

            for (int i = 1; i <= size; ++i) {
                final SetlList tuple = new SetlList(2);
                tuple.addMember(state, getMember(i));
                tuple.addMember(state, setlList.getMember(i));
                result.addMember(state, tuple);
            }

            return result;
        } else if (other.getClass() == Term.class) {
            return ((Term) other).cartesianProductFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this.toString(state) + " >< " + other.toString(state) + "' is not a list."
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
        final HashMap<Value, Integer> map         = new HashMap<>();
              Integer                 occurrences;
        for (final Value v : list) {
            occurrences = map.get(v);
            if (occurrences == null) {
                map.put(v, 1);
            } else {
                map.put(v, ++occurrences);
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
    public Value collectionAccessUnCloned(final State state, final List<Value> args) throws SetlException {
        if (args.contains(RangeDummy.RD)) {
            // uncloned access is only used in assignments, so we should never get here
            throw new UndefinedOperationException(
                "Can not access elements using the arguments '" + args + "' on '" + this.toString(state) + "';" +
                " range is not allowed in assignments."
            );
        } else if (args.size() == 1) {
            return getMemberUnCloned(state, args.get(0));
        } else {
            // uncloned access is only used in assignments, so we should never get here
            throw new UndefinedOperationException(
                "Can not access elements using the arguments '" + args + "' on '" + this.toString(state) + "';" +
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
        return getMemberZZZInternal(state, index).clone();
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

    private Value getMemberZZZInternal(final State state, final Value vIndex) throws SetlException {
        if (vIndex.isInteger() == SetlBoolean.TRUE) {
            return getMemberZZZInternal(vIndex.jIntValue());
        } else {
            throw new IncompatibleTypeException(
                "Index '" + vIndex.toString(state) + "' is not a integer."
            );
        }
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
    public Value getMembers(final State state, final int expectedNumberOfMembers, final int lowFromStart, final int highFromStart) throws SetlException {
        final SetlList result = new SetlList(expectedNumberOfMembers);
                     // in java the index is one lower
        for (int i = lowFromStart - 1; expectedNumberOfMembers > 0 && i < highFromStart && i < size(); ++i) {
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
            if (v.isNumber() == SetlBoolean.FALSE) {
                throw new IncompatibleTypeException(
                        "The list " + this.toString(state) + " is not a list of numbers."
                );
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
            if (v.isNumber() == SetlBoolean.FALSE) {
                throw new IncompatibleTypeException(
                        "The list " + this.toString(state) + " is not a list of numbers."
                );
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

        isCloned = true;
        final ArrayList<Value> p = new ArrayList<>(list);

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

        final SetlList result = new SetlList(p);
        result.isCloned = true;
        return result;
    }

    @Override
    public SetlSet permutations(final State state) throws SetlException {
        isCloned = true;
        return new SetlSet(permutations(state, list));
    }

    private Collection<SetlList> permutations(final State state, final List<Value> values) throws SetlException {
        if (state.executionStopped) {
            throw new StopExecutionException();
        }
        final int nPermutation = list.size(); // size of one final permutation
        final int valuesSize   = values.size();
        if (valuesSize == 0) {
            final ArrayList<SetlList> permutations = new ArrayList<>(1);
            permutations.add(new SetlList(nPermutation));
            return permutations;
        }
        final Value                first            = values.get(0);
        final Collection<SetlList> permutationsRest = permutations(state, values.subList(1, valuesSize));
        final Collection<SetlList> permutations;
        if (valuesSize == nPermutation || valuesSize % 3 == 0) {
            permutations = new TreeSet<>(NUMERICAL_COMPARATOR);
        } else {
            permutations = new ArrayList<>(permutationsRest.size() * (permutationsRest.iterator().next().size() + 1));
        }
        for (final SetlList permutation : permutationsRest) {
            final int size = permutation.size();
            for (int i = 0; i < size; ++i) {
                final ArrayList<Value> perm = new ArrayList<>(nPermutation);
                if (i > 0) {
                    perm.addAll(permutation.list.subList(0, i));
                }
                perm.add(first);
                perm.addAll(permutation.list.subList(i, size));
                permutations.add(new SetlList(perm));
            }
            permutation.list.add(first);
            permutations.add(permutation);
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
        final int              size    = list.size();
        // mark this list to be clone
        isCloned = true;
        // create reversed clone of this list
        final ArrayList<Value> reverse = new ArrayList<>(size);
        for (int i = size - 1; i >= 0; --i) {
            reverse.add(list.get(i));
        }
        return new SetlList(reverse);
    }

    @Override
    public void setMember(final State state, final Value index, final Value value) throws SetlException {
        if (index.isInteger() == SetlBoolean.TRUE) {
            setMember(state, index.jIntValue(), value);
        } else {
            throw new IncompatibleTypeException(
                "Index '" + index + "' is not a integer."
            );
        }
    }

    @Override
    public void setMember(final State state, int index, final Value value) throws SetlException {
        separateFromOriginal();
        if (index < 1) {
            throw new NumberToLargeException(
                    "Index '" + index + "' is lower as '1'."
            );
        }

        // in java the index is one lower
        --index;

        if (index >= list.size()) {
            if (value == Om.OM) {
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
        list.set(index, value);

        if (value == Om.OM) {
            compress();
        }
    }

    @Override
    public SetlList shuffle(final State state) {
        final ArrayList<Value> list = new ArrayList<>(this.list);
        Collections.shuffle(list, state.getRandom());
        return new SetlList(list);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public SetlList sort(final State state) {
        final ArrayList<Value> list = new ArrayList<>(this.list);
        Collections.sort(list, NUMERICAL_COMPARATOR);
        return new SetlList(list);
    }

    @Override
    public SetlList split(final State state, final Value pattern) throws IncompatibleTypeException {
        final SetlList result    = new SetlList();
              SetlList subResult = new SetlList();
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
        TermUtilities.appendCodeFragmentString(state, this, sb);
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
     * @param addBrackets Append enclosing brackets of the list.
     */
    public void canonical(final State state, final StringBuilder sb, final boolean addBrackets) {
        if (addBrackets) {
            sb.append("[");
        }

        final Iterator<Value> iter = iterator();
        while (iter.hasNext()) {
            iter.next().canonical(state, sb);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        if (addBrackets) {
            sb.append("]");
        }
    }

    /* term operations */

    @Override
    public MatchResult matchesTerm(final State state, final Value other) throws SetlException {
        if (other == IgnoreDummy.ID) {
            return new MatchResult(true);
        } else if (other.isList() != SetlBoolean.TRUE && other.isString() != SetlBoolean.TRUE) {
            return new MatchResult(false);
        }
        final IndexedCollectionValue otherCollection = (IndexedCollectionValue) other;

        if (list.size() == 1 && list.get(0).getClass() == Term.class) {
            final MatchResult result = ExplicitListWithRest.matchTerm(state, (Term) list.get(0), otherCollection);
            if (result.isMatch()) {
                return result;
            }
        }

        if (list.size() != otherCollection.size()) {
            return new MatchResult(false);
        }

        // match all members
        final MatchResult     result    = new MatchResult(true);
        final Iterator<Value> thisIter  = iterator();
        final Iterator<Value> otherIter = otherCollection.iterator();
        while (thisIter.hasNext() && otherIter.hasNext() && result.isMatch()) {
            final MatchResult subResult = thisIter.next().matchesTerm(state, otherIter.next());
            if (subResult.isMatch()) {
                result.addBindings(subResult);
            } else {
                return new MatchResult(false);
            }
        }

        return result;
    }

    @Override
    public Value toTerm(final State state) throws SetlException {
        final SetlList termList = new SetlList(list.size());
        for (final Value v: list) {
            termList.addMember(state, v.toTerm(state));
        }
        return termList;
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == SetlList.class) {
            final ArrayList<Value> otherList = ((SetlList) other).list;
            if (list == otherList) {
                return 0; // clone
            }
            final Iterator<Value> iterFirst  = list.iterator();
            final Iterator<Value> iterSecond = otherList.iterator();
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
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetlList.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        if (this == other) {
            return true;
        } else if (other.getClass() == SetlList.class) {
            final ArrayList<Value> otherList = ((SetlList) other).list;
            if (list == otherList) {
                return true; // clone
            } else if (list.size() == otherList.size()) {
                final Iterator<Value> iterFirst  = list.iterator();
                final Iterator<Value> iterSecond = otherList.iterator();
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
        final int size = list.size();
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + size;
        if (size >= 1) {
            hash = hash * 31 + list.get(0).hashCode();
            if (size >= 2) {
                hash = hash * 31 + list.get(size-1).hashCode();
            }
        }
        return hash;
    }
}


package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/* This class implements a list of arbitrary SetlX values.
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

public class SetlList extends CollectionValue {
    /* To allow initially `free' cloning, by only marking a clone without
     * actually doing any cloning, this list carries a isClone flag.
     *
     * If the contents of this SetlList is modified `separateFromOriginal()'
     * MUST be called before the modification, which then performs the real cloning,
     * if required.
     *
     * Main benefit of this technique is to perform the real cloning only
     * when a clone is actually modified, thus not performing a time consuming
     * cloning, when the clone is only used read-only, which it is in most cases.
     */

    private LinkedList<Value>   mList;
    // cache of last position in list, prevents reiterating from beginning
    private ListIterator<Value> mIterator;
    // is this list a clone
    private boolean             isCloned;

    public SetlList() {
        mList       = new LinkedList<Value>();
        mIterator   = mList.listIterator(0);
        isCloned    = false; // new lists are not a clone
    }

    /*package*/ SetlList(LinkedList<Value> list) {
        mList       = list;
        mIterator   = mList.listIterator(0);
        isCloned    = true;  // lists created from another list ARE a clone
    }

    public SetlList clone() {
        /* When cloning, THIS list is marked to be a clone as well.
         *
         * This is done, because even though THIS is the original, it must also be
         * cloned upon modification, otherwise clones which carry the same
         * member list of THIS list would not notice, e.g.
         * modifications of THIS original would bleed through to the clones.
         */
        isCloned = true;
        return new SetlList(mList);
    }

    /* If the contents of THIS SetlList is modified, the following function MUST
     * be called before the modification. It performs the real cloning,
     * if THIS is actually marked as a clone.
     *
     * While clone() is called upon all members of this list, this does not perform
     * a `deep' cloning, as the members themselves are only marked for cloning.
     */
    private void separateFromOriginal() {
        if (isCloned) {
            LinkedList<Value> original = mList;
            mList = new LinkedList<Value>();
            for (final Value v: original) {
                mList.add(v.clone());
            }
            resetIterator(0);
            isCloned = false;
        }
    }

    private void resetIterator(final int index) {
        // does it take more steps to go from current index or 0 to new position?
        final int deltaAbs = Math.abs(mIterator.nextIndex() - index);
        if (deltaAbs < index) { // go from current index
            while(mIterator.nextIndex() < index) {
                mIterator.next();
            }
            while(mIterator.nextIndex() > index) {
                mIterator.previous();
            }
        } else { // go from start
            mIterator = mList.listIterator(index);
        }
    }

    public Iterator<Value> iterator() {
        return mList.iterator();
    }

    public void compressAndResetIterator() {
        while (mList.size() > 0 && mList.getLast() == Om.OM) {
            mList.removeLast();
        }
        resetIterator(0);
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isList() {
        return SetlBoolean.TRUE;
    }

    /* arithmetic operations */

    public Value sum(Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand instanceof CollectionValue) {
            final SetlList result = this.clone();
            for (final Value v: (CollectionValue) summand) {
                result.addMember(v.clone());
            }
            return result;
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand + "' is not a list or string."
            );
        }
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    public void addMember(Value element) {
        separateFromOriginal();
        mList.add(element.clone());
        resetIterator(0);
    }

    public Value collectionAccess(List<Value> args) throws SetlException {
        final int   aSize  = args.size();
        final Value vFirst = (aSize >= 1)? args.get(0) : null;
        if (args.contains(RangeDummy.RD)) {
            if (aSize == 2 && vFirst == RangeDummy.RD) {
                // everything up to high boundary: this(  .. y);
                return getMembers(new Rational(1), args.get(1));

            } else if (aSize == 2 && args.get(1) == RangeDummy.RD) {
                // everything from low boundary:   this(x ..  );
                return getMembers(vFirst, new Rational(size()));

            } else if (aSize == 3 && args.get(1) == RangeDummy.RD) {
                // full range spec:                this(x .. y);
                return getMembers(vFirst, args.get(2));
            }
            throw new UndefinedOperationException(
                "Can not access elements using the arguments '" + args + "' on '" + this + "';" +
                " arguments are malformed."
            );
        } else if (aSize == 1) {
            return getMember(vFirst);
        } else {
            throw new UndefinedOperationException(
                "Can not access elements using the arguments '" + args + "' on '" + this + "';" +
                " arguments are malformed."
            );
        }
    }

    public Value collectionAccessUnCloned(List<Value> args) throws SetlException {
        if (args.contains(RangeDummy.RD)) {
            // uncloned access is only used in assignments, so we should never get here
            throw new UndefinedOperationException(
                "Can not access elements using the arguments '" + args + "' on '" + this + "';" +
                " range is not allowed in assignments."
            );
        } else if (args.size() == 1) {
            return getMemberUnCloned(args.get(0));
        } else {
            // uncloned access is only used in assignments, so we should never get here
            throw new UndefinedOperationException(
                "Can not access elements using the arguments '" + args + "' on '" + this + "';" +
                " arguments are malformed."
            );
        }
    }

    public SetlBoolean containsMember(Value element) {
        return SetlBoolean.get(mList.contains(element));
    }

    public Value firstMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return mList.getFirst().clone();
    }

    public Value getMember(Value index) throws SetlException {
        return getMemberZZZInternal(index).clone();
    }

    public Value getMemberUnCloned(Value index) throws SetlException {
        separateFromOriginal();
        return getMemberZZZInternal(index);
    }

    private Value getMemberZZZInternal(Value vIndex) throws SetlException {
        int index = 0;
        if (vIndex.isInteger() == SetlBoolean.TRUE) {
            index = ((Rational)vIndex).intValue();
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
        if (index > size()) {
            return Om.OM;
        }
        // in java the index is one lower
        index--;
        if (mIterator.previousIndex() == index) {
            return mIterator.previous();
        } else {
            if (mIterator.nextIndex() != index) {
                resetIterator(index);
            }
            return mIterator.next();
        }
    }

    public Value getMembers(Value vLow, Value vHigh) throws SetlException {
        int low = 0, high = 0;
        if (vLow.isInteger() == SetlBoolean.TRUE) {
            low = ((Rational)vLow).intValue();
        } else {
            throw new IncompatibleTypeException(
                "Lower bound '" + vLow + "' is not a integer."
            );
        }
        if (vHigh.isInteger() == SetlBoolean.TRUE) {
            high = ((Rational)vHigh).intValue();
        } else {
            throw new IncompatibleTypeException(
                "Upper bound '" + vHigh + "' is not a integer."
            );
        }

        if (low < 1) {
            throw new NumberToLargeException(
                "Lower bound '" + low + "' is lower as '1'."
            );
        }
        if (size() == 0) {
            throw new NumberToLargeException(
                "Lower bound '" + low + "' is larger as list size '" + size() + "'."
            );
        }
        if (high > size()) {
            throw new NumberToLargeException(
                "Upper bound '" + high + "' is larger as list size '" + size() + "'."
            );
        }

        // in java the index is one lower
        low--;
        if (mIterator.nextIndex() != low) {
            resetIterator(low);
        }

        final SetlList result = new SetlList();
        for (int i = low; i < high; i++) {
            result.addMember(mIterator.next().clone());
        }
        return result;
    }

    public Value lastMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return mList.getLast().clone();
    }

    public Value maximumMember() throws SetlException {
        Value max = Infinity.NEGATIVE;
        for (final Value v: mList) {
            if (v.maximum(max).equals(v)) {
                max = v;
            }
        }
        return max.clone();
    }

    public Value minimumMember() throws SetlException {
        Value min = Infinity.POSITIVE;
        for (final Value v: mList) {
            if (v.minimum(min).equals(v)) {
                min = v;
            }
        }
        return min.clone();
    }

    public SetlSet permutations() throws SetlException {
        if (size() == 0) {
            final SetlSet permutations = new SetlSet();
            permutations.addMember(clone());
            return permutations;
        }
        final Value     last            = lastMember();
        final SetlList  rest            = clone();
        rest.removeLastMember();
        final SetlSet   permutatateRest = rest.permutations();
        final SetlSet   permutations    = new SetlSet();
        for (final Value permutation : permutatateRest) {
            for (int i = 0; i <= permutation.size(); i++) {
                final SetlList  perm    = (SetlList) permutation.clone();
                perm.separateFromOriginal();
                perm.mList.add(i, last.clone());
                permutations.addMember(perm);
            }
        }
        return permutations;
    }

    public void removeMember(Value element) {
        separateFromOriginal();
        mList.remove(element);
        compressAndResetIterator();
    }

    public void removeFirstMember() {
        separateFromOriginal();
        mList.removeFirst();
        compressAndResetIterator();
    }

    public void removeLastMember() {
        separateFromOriginal();
        mList.removeLast();
        compressAndResetIterator();
    }

    public SetlList reverse() {
        // mark this list to be clone
        isCloned = true;
        // create reversed clone of this list
        final LinkedList<Value> reverse = new LinkedList<Value>();
        for (final Value v : mList) {
            reverse.addFirst(v);
        }
        return new SetlList(reverse);
    }

    public void setMember(Value vIndex, Value v) throws SetlException {
        separateFromOriginal();
        int index = 0;
        if (vIndex.isInteger() == SetlBoolean.TRUE) {
            index = ((Rational)vIndex).intValue();
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
        index--;

        if (index >= mList.size()) {
            if (v == Om.OM) {
                return; // nothing to do
            } else {
                // fill gap from size to index with OM, if necessary
                while (index >= mList.size()) {
                    mList.add(Om.OM);
                }
                resetIterator(0);
            }
        }

        // set index to value
        if (mIterator.previousIndex() == index) {
            mIterator.previous(); // marks index for following set(v)
        } else {
            if (mIterator.nextIndex() != index) {
                resetIterator(index);
            }
            mIterator.next();    // marks index for following set(v)
        }
        // set value at index which was previously marked
        mIterator.set(v.clone());

        if (v == Om.OM) {
            compressAndResetIterator();
        }
    }

    public int size() {
        return mList.size();
    }

    /* string and char operations */

    public String canonical() {
        String result = "[";

        final Iterator<Value> iter  = iterator();
        while (iter.hasNext()) {
            result += iter.next().canonical();
            if (iter.hasNext()) {
                result += ", ";
            }
        }

        return result + "]";
    }

    public String toString() {
        return TermConverter.valueToCodeFragment(this, false).toString();
    }

    /* term operations */

    public MatchResult matchesTerm(Value other) throws IncompatibleTypeException {
        if (other == IgnoreDummy.ID) {
            return new MatchResult(true);
        } else if ( ! (other instanceof SetlList)) {
            return new MatchResult(false);
        }
        final SetlList otherList = (SetlList) other;

        if (mList.size() != otherList.mList.size()) {
            return new MatchResult(false);
        }

        // match all members
        final MatchResult       result      = new MatchResult(true);
        final Iterator<Value>   thisIter    = iterator();
        final Iterator<Value>   otherIter   = otherList.iterator();
        while (thisIter.hasNext() && otherIter.hasNext()) {
            final MatchResult subResult   = thisIter.next().matchesTerm(otherIter.next());
            if (subResult.isMatch()) {
                result.addBindings(subResult);
            } else {
                return new MatchResult(false);
            }
        }

        return result;
    }

    public Value toTerm() {
        final SetlList termList = new SetlList();
        for (Value v: mList) {
            termList.addMember(v.toTerm());
        }
        return termList;
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
        if (v instanceof SetlList) {
            final Iterator<Value> iterFirst  = iterator();
            final Iterator<Value> iterSecond = ((SetlList) v).iterator();
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
        } else if (v instanceof Term || v instanceof ProcedureDefinition || v == Infinity.POSITIVE) {
            // only Term, ProcedureDefinition and +Infinity are bigger
            return -1;
        } else {
            // everything else is smaller
            return 1;
        }
    }

}


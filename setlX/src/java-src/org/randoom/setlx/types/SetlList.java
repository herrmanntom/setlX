package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private ArrayList<Value>    mList;
    // is this list a clone
    private boolean             isCloned;

    public SetlList() {
        this(10);
    }

    public SetlList(final int initialCapacity) {
        mList       = new ArrayList<Value>(initialCapacity);
        isCloned    = false; // new lists are not a clone
    }

    /*package*/ SetlList(final ArrayList<Value> list) {
        mList       = list;
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
            ArrayList<Value> original = mList;
            mList = new ArrayList<Value>(original.size());
            for (final Value v: original) {
                mList.add(v.clone());
            }
            isCloned = false;
        }
    }

    public Iterator<Value> iterator() {
        return mList.iterator();
    }

    public void compress() {
        while (mList.size() > 0 && mList.get(mList.size() - 1) == Om.OM) {
            mList.remove(mList.size() - 1);
        }
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isList() {
        return SetlBoolean.TRUE;
    }

    /* arithmetic operations */

    public Value sum(final Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand instanceof CollectionValue) {
            final SetlList result = this.clone();
            result.separateFromOriginal();
            result.mList.ensureCapacity(this.size() + summand.size());
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

    public void addMember(final Value element) {
        separateFromOriginal();
        mList.add(element.clone());
    }

    public Value collectionAccess(final List<Value> args) throws SetlException {
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

    public Value collectionAccessUnCloned(final List<Value> args) throws SetlException {
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

    public SetlBoolean containsMember(final Value element) {
        return SetlBoolean.get(mList.contains(element));
    }

    public Value firstMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return mList.get(0).clone();
    }

    public Value getMember(final Value index) throws SetlException {
        return getMemberZZZInternal(index).clone();
    }

    public Value getMemberUnCloned(final Value index) throws SetlException {
        separateFromOriginal();
        return getMemberZZZInternal(index);
    }

    private Value getMemberZZZInternal(final Value vIndex) throws SetlException {
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
        return mList.get(index - 1);
    }

    public Value getMembers(final Value vLow, final Value vHigh) throws SetlException {
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
        final SetlList result = new SetlList(high - low);
        for (int i = low; i < high; i++) {
            result.addMember(mList.get(i).clone());
        }
        return result;
    }

    public Value lastMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return mList.get(mList.size() - 1).clone();
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

    public void removeMember(final Value element) {
        separateFromOriginal();
        mList.remove(element);
        compress();
    }

    public void removeFirstMember() {
        separateFromOriginal();
        mList.remove(0);
        compress();
    }

    public void removeLastMember() {
        separateFromOriginal();
        mList.remove(mList.size() - 1);
        compress();
    }

    public SetlList reverse() {
        final int               size    = mList.size();
        // mark this list to be clone
        isCloned = true;
        // create reversed clone of this list
        final ArrayList<Value>  reverse = new ArrayList<Value>(size);
        for (int i = size - 1; i >= 0; --i) {
            reverse.add(mList.get(i));
        }
        return new SetlList(reverse);
    }

    public void setMember(final Value vIndex, final Value v) throws SetlException {
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
                mList.ensureCapacity(index + 1);
                // fill gap from size to index with OM, if necessary
                while (index >= mList.size()) {
                    mList.add(Om.OM);
                }
            }
        }

        // set index to value
        mList.set(index, v.clone());

        if (v == Om.OM) {
            compress();
        }
    }

    public int size() {
        return mList.size();
    }

    /* string and char operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        TermConverter.valueToCodeFragment(this, false).appendString(sb, 0);
    }

    public void canonical(final StringBuilder sb) {
        canonical(sb, true);
    }

    public void canonical(final StringBuilder sb, boolean addBracktes) {
        if (addBracktes) {
            sb.append("[");
        }

        final Iterator<Value> iter = iterator();
        while (iter.hasNext()) {
            iter.next().canonical(sb);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        if (addBracktes) {
            sb.append("]");
        }
    }

    /* term operations */

    public MatchResult matchesTerm(final Value other) throws IncompatibleTypeException {
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
            final MatchResult   subResult   = thisIter.next().matchesTerm(otherIter.next());
            if (subResult.isMatch()) {
                result.addBindings(subResult);
            } else {
                return new MatchResult(false);
            }
        }

        return result;
    }

    public Value toTerm() {
        final SetlList termList = new SetlList(mList.size());
        for (final Value v: mList) {
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
    public int compareTo(final Value v){
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


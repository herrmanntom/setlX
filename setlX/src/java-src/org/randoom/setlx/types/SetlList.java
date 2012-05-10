package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* This class implements a list of arbitrary SetlX values.
 * It will most likely be created and filled by an SetListConstructor
 * (or is result of an operation).
 *
 * Also see:
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
    private boolean             isCloned; // is this list a clone?

    public SetlList() {
        mList       = new LinkedList<Value>();
        isCloned    = false; // new lists are not a clone
    }

    /*package*/ SetlList(LinkedList<Value> list) {
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
            LinkedList<Value> original = mList;
            mList = new LinkedList<Value>();
            for (Value v: original) {
                mList.add(v.clone());
            }
            isCloned = false;
        }
    }

    public Iterator<Value> iterator() {
        return mList.iterator();
    }

    public void compress() {
        while (mList.size() > 0 && mList.getLast() == Om.OM) {
            mList.removeLast();
        }
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
            SetlList result = this.clone();
            for (Value v: (CollectionValue) summand) {
                result.addMember(v.clone());
            }
            return result;
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(this);
        }  else {
            throw new IncompatibleTypeException(
                "Right-hand-side of '" + this + " + " + summand + "' is not a list or string."
            );
        }
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    public void addMember(Value element) {
        separateFromOriginal();
        mList.add(element.clone());
    }

    public Value collectionAccess(List<Value> args) throws SetlException {
        int   aSize  = args.size();
        Value vFirst = (aSize >= 1)? args.get(0) : null;
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
        return mList.get(index - 1);
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

        SetlList result = new SetlList();
        for (int i = low - 1; i < high; i++) {
            result.addMember(mList.get(i).clone());
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
        for (Value v: mList) {
            if (v.maximum(max).equals(v)) {
                max = v;
            }
        }
        return max.clone();
    }

    public Value minimumMember() throws SetlException {
        Value min = Infinity.POSITIVE;
        for (Value v: mList) {
            if (v.minimum(min).equals(v)) {
                min = v;
            }
        }
        return min.clone();
    }

    public SetlSet permutations() throws SetlException {
        if (size() == 0) {
            SetlSet permutations = new SetlSet();
            permutations.addMember(clone());
            return permutations;
        }
        Value           last            = lastMember();
        SetlList        rest            = clone();
        rest.removeLastMember();
        SetlSet         permutatateRest = rest.permutations();
        SetlSet         permutations    = new SetlSet();
        for (Value permutation : permutatateRest) {
            for (int i = 0; i <= permutation.size(); i++) {
                SetlList    perm    = (SetlList) permutation.clone();
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
        compress();
    }

    public void removeFirstMember() {
        separateFromOriginal();
        mList.removeFirst();
        compress();
    }

    public void removeLastMember() {
        separateFromOriginal();
        mList.removeLast();
        compress();
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
        if (v == Om.OM) {
            if (index <= mList.size()) {
                mList.set(index - 1, v);
            }
            compress();
        } else {
            while (index > mList.size()) {
                mList.add(Om.OM);
            }
            mList.set(index - 1, v.clone());
        }
    }

    public int size() {
        return mList.size();
    }

    /* string and char operations */

    public String canonical() {
        String result = "[";

        Iterator<Value> iter    = iterator();
        while (iter.hasNext()) {
            Value   member  = iter.next();
            result += member.canonical();
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
        // 'other' is a list
        SetlList otherList = (SetlList) other;

        if (mList.size() != otherList.mList.size()) {
            return new MatchResult(false);
        }

        // same number of members
        MatchResult     result      = new MatchResult(true);
        Iterator<Value> thisIter    = iterator();
        Iterator<Value> otherIter   = otherList.iterator();
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

    public Value toTerm() {
        SetlList termList = new SetlList();
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
            Iterator<Value> iterFirst  = iterator();
            Iterator<Value> iterSecond = ((SetlList) v).iterator();
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                Value first  = iterFirst .next();
                Value second = iterSecond.next();
                int   cmp    = first.compareTo(second);
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


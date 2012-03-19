package interpreter.types;

import comparableSet.ComparableSet;
import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;
import interpreter.utilities.MatchResult;
import interpreter.utilities.TermConverter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SetlSet extends CollectionValue {

    private ComparableSet<Value> mSet;
    private ComparableSet<Value> mOriginalSet;

    public SetlSet(){
        mSet                = new ComparableSet<Value>();
        mOriginalSet        = null;
    }

    private SetlSet(ComparableSet<Value> set){
        mSet                = null;
        mOriginalSet        = set;
    }

    public SetlSet clone() {
        mOriginalSet = getSet();
        mSet         = null;
        return new SetlSet(mOriginalSet);
    }

    public void separateFromOriginal() {
        if (mOriginalSet != null) {
            mSet = new ComparableSet<Value>();
            for (Value v: mOriginalSet) {
                mSet.add(v.clone());
            }
            mOriginalSet = null;
        }
    }

    private ComparableSet<Value> getSet() {
        if (mSet != null) {
            return mSet;
        } else {
            return mOriginalSet;
        }
    }

    public Iterator<Value> iterator() {
        return getSet().iterator();
    }

    public SetlBoolean isLessThan(Value other) throws IncompatibleTypeException {
        if (other instanceof SetlSet) {
            SetlSet otr = (SetlSet) other;
            return SetlBoolean.get(getSet().isSubset(otr.getSet()) && getSet().compareTo(otr.getSet()) != 0);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " < " + other + "' is not a set.");
        }
    }

    /* type checks (sort of boolean operation) */

    public SetlBoolean isMap() {
        for (Value v: getSet()) {
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

    /* arithmetic operations */

    public Value difference(Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof SetlSet) {
            SetlSet s = (SetlSet) subtrahend;
            return new SetlSet(getSet().difference(s.getSet()));
        } else if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " - " + subtrahend + "' is not a set.");
        }
    }

    public Value multiply(Value multiplier) throws IncompatibleTypeException {
        if (multiplier instanceof SetlSet) {
            SetlSet m = (SetlSet) multiplier;
            return new SetlSet(getSet().intersection(m.getSet()));
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " * " + multiplier + "' is not a set.");
        }
    }

    public Value sum(Value summand) throws IncompatibleTypeException {
        if (summand instanceof SetlSet) {
            SetlSet s = (SetlSet) summand;
            return new SetlSet(getSet().union(s.getSet()));
        } else if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if(summand instanceof CollectionValue) {
            SetlSet result = this.clone();
            result.separateFromOriginal();
            for (Value v: (CollectionValue) summand) {
                result.addMember(v.clone());
            }
            return result;
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).sumFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " + " + summand + "' is not a set or string.");
        }
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    public void addMember(Value element) {
        if (element == Om.OM) {
            return;
        } else {
            separateFromOriginal();
            mSet.add(element);
        }
    }

    public Value collectionAccess(List<Value> args) throws SetlException {
        if (args.contains(RangeDummy.RD)) {
            throw new UndefinedOperationException("Range operations are unsupported on '" + this + "'.");
        } else if (args.size() != 1) {
            throw new UndefinedOperationException("Can not access elements using arguments '" + args + "' on '" + this + "'; Exactly one argument is required.");
        }
        return getMember(args.get(0));
    }

    // returns a set of all pairs which first element matches arg
    public Value collectMap(Value arg) throws SetlException {
        SetlSet result = new SetlSet();
        for (Value v: getSet()) {
            if (v instanceof SetlList) {
                if (v.size() == 2) {
                    if (v.getMember(new SetlInt(1)).equals(arg)) {
                        result.addMember(v.getMember(new SetlInt(2)));
                    }
                } else {
                    throw new IncompatibleTypeException("'" + this + "' is not a map.");
                }
            } else {
                throw new IncompatibleTypeException("'" + this + "' is not a map.");
            }
        }
        return result;
    }

    public SetlBoolean containsMember(Value element) {
        return SetlBoolean.get(getSet().member(element));
    }

    public SetlSet domain() throws SetlException {
        SetlSet result = new SetlSet();
        for (Value v: getSet()) {
            if (v instanceof SetlList) {
                if (v.size() == 2) {
                    result.addMember(v.getMember(new SetlInt(1)));
                } else {
                    throw new IncompatibleTypeException("'" + this + "' is not a map.");
                }
            } else {
                throw new IncompatibleTypeException("'" + this + "' is not a map.");
            }
        }
        return result;
    }

    public Value firstMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return getSet().getSet().first().clone();
    }

    public Value getMember(Value element) throws SetlException {
        Value result = Om.OM;
        for (Value v: getSet()) {
            if (v instanceof SetlList) {
                if (v.size() == 2) {
                    if (v.getMember(new SetlInt(1)).equals(element)) {
                        if (result instanceof Om) {
                            result = v.getMember(new SetlInt(2));
                        } else {
                            // double match!
                            result = Om.OM;
                            break;
                        }
                    }
                } else {
                    throw new IncompatibleTypeException("'" + this + "' is not a map.");
                }
            } else {
                throw new IncompatibleTypeException("'" + this + "' is not a map.");
            }
        }
        return result;
    }

    public Value lastMember() {
        if (size() < 1) {
            return Om.OM;
        }
        return getSet().getSet().last().clone();
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

    public SetlSet powerSet() {
        ComparableSet<ComparableSet<Value>> powSet = getSet().powerSet();
        ComparableSet<Value>                newSet = new ComparableSet<Value>();
        for (ComparableSet<Value> set: powSet) {
            newSet.add(new SetlSet(set));
        }
        return new SetlSet(newSet);
    }

    public SetlSet range() throws SetlException {
        SetlSet result = new SetlSet();
        for (Value v: getSet()) {
            if (v instanceof SetlList) {
                SetlList list  = (SetlList) v;
                if (list.size() == 2) {
                    result.addMember(list.getMember(new SetlInt(2)));
                } else {
                    throw new IncompatibleTypeException("'" + this + "' is not a map.");
                }
            } else {
                throw new IncompatibleTypeException("'" + this + "' is not a map.");
            }
        }
        return result;
    }

    public void setMember(Value index, Value v) throws SetlException {
        List<Value> delete = new LinkedList<Value>();
        separateFromOriginal();
        for (Value element: mSet) {
            if (element instanceof SetlList) {
                SetlList list  = (SetlList) element;
                if (list.size() == 2) {
                    if (list.getMember(new SetlInt(1)).equals(index)) {
                        // Mark all matching pairs for deletion
                        delete.add(element);
                    } else if (delete.size() > 0) {
                         /*  This pair does not match after at least one
                             matching one was marked for deletion.
                             Because this set is ordered there can't be any
                             more maching pairs left in this map.             */
                        break;
                    }
                } else {
                    throw new IncompatibleTypeException("'" + this + "' is not a map.");
                }
            } else {
                throw new IncompatibleTypeException("'" + this + "' is not a map.");
            }
        }
        /* remove all elements in delete list after loop over this set
           (prevents concurent modification of set while looping over it)     */
        for (Value element: delete) {
            mSet.getSet().remove(element);
        }
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
        return getSet().size();
    }

    public void removeMember(Value element) {
        separateFromOriginal();
        mSet.getSet().remove(element);
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

    public MatchResult matchesTerm(Value other) {
        if (other == IgnoreDummy.ID) {
            return new MatchResult(true);
        } else if ( ! (other instanceof SetlSet)) {
            return new MatchResult(false);
        }
        // 'other' is a set
        SetlSet otherSet = (SetlSet) other;

        if (getSet().size() != otherSet.getSet().size()) {
            return new MatchResult(false);
        }

        // same number of members
        MatchResult     result      = new MatchResult(true);
        Iterator<Value> thisIter    = iterator();
        Iterator<Value> otherIter   = otherSet.iterator();
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
        if (thisIter.hasNext() || otherIter.hasNext()) {
            // this should not happen, as sizes are the same
            return new MatchResult(false);
        }

        // all members match
        return result;
    }

    public Value toTerm() {
        SetlSet termSet = new SetlSet();
        for (Value v: getSet()) {
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
     * SetlError < Om < -Infinity < SetlBoolean < SetlInt & Real < SetlString < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public int compareTo(Value v){
        if (v instanceof SetlSet) {
            SetlSet s = (SetlSet) v;
            return getSet().compareTo(s.getSet());
        } else if (v instanceof SetlList || v instanceof Term || v instanceof ProcedureDefinition || v == Infinity.POSITIVE) {
            // only SetlList, Term, ProcedureDefinition and +Infinity are bigger
            return -1;
        } else {
            return 1;
        }
    }
}


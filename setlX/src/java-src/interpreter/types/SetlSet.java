package interpreter.types;

import comparableSet.ComparableSet;
import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;

import java.util.Iterator;
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

    public int size() {
        return getSet().size();
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

    public Value add(Value summand) throws IncompatibleTypeException {
        if (summand instanceof SetlSet) {
            SetlSet s = (SetlSet) summand;
            return new SetlSet(getSet().union(s.getSet()));
        } else if(summand instanceof CollectionValue) {
            SetlSet result = this.clone();
            result.separateFromOriginal();
            for (Value v: (CollectionValue) summand) {
                result.addMember(v.clone());
            }
            return result;
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).addFlipped(this);
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " + " + summand + "' is not a set or string.");
        }
    }

    public SetlSet multiply(Value multiplier) throws IncompatibleTypeException {
        if (multiplier instanceof SetlSet) {
            SetlSet m = (SetlSet) multiplier;
            return new SetlSet(getSet().intersection(m.getSet()));
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " * " + multiplier + "' is not a set.");
        }
    }

    public SetlSet subtract(Value subtrahend) throws IncompatibleTypeException {
        if (subtrahend instanceof SetlSet) {
            SetlSet s = (SetlSet) subtrahend;
            return new SetlSet(getSet().difference(s.getSet()));
        } else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " - " + subtrahend + "' is not a set.");
        }
    }

    /* operations on compound values (Lists, Sets [, Strings]) */

    public void addMember(Value element) {
        if (element == Om.OM) {
            return;
        } else {
            separateFromOriginal();
            mSet.add(element);
        }
    }

    public SetlSet collectMembers(Value element) throws SetlException {
        SetlSet result = new SetlSet();
        for (Value v: getSet()) {
            if (v instanceof SetlList) {
                if (v.size() == 2) {
                    if (v.getMember(new SetlInt(1)).equals(element)) {
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
        separateFromOriginal();
        for (Value element: mSet) {
            if (element instanceof SetlList) {
                SetlList list  = (SetlList) element;
                if (list.size() == 2) {
                    if (list.getMember(new SetlInt(1)).equals(index)) {
                        try {
                            list.setMember(new SetlInt(2), v);
                        } catch (NumberToLargeException ne) {
                            // the index can not be out of range when size() == 2
                        }
                    }
                } else {
                    throw new IncompatibleTypeException("'" + this + "' is not a relation.");
                }
            } else {
                throw new IncompatibleTypeException("'" + this + "' is not a relation.");
            }
        }
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

    /* calls (element access) */

    public Value call(List<Expr> exprs, List<Value> args) throws SetlException {
        if (args.contains(RangeDummy.RD)) {
            throw new UndefinedOperationException("Range operations are unsupported on '" + this + "'.");
        } else if (args.size() != 1) {
            throw new UndefinedOperationException("Can not perform call with arguments '" + args + "' on '" + this + "'; arguments are malformed.");
        }
        return getMember(args.get(0));
    }

    // this call returns a set, not a single value
    public Value callCollection(Value arg) throws SetlException {
        return collectMembers(arg);
    }

    /* String and Char operations */

    public String toString() {
        // unfortunately sets in java use [] in toString...
        String s = getSet().toString();
        return "{" + s.substring(1, s.length() - 1) + "}";
    }

    /* Comparisons */

    /* Compare two Values.  Returns -1 if this value is less than the value given
     * as argument, +1 if its greater and 0 if both values contain the same
     * elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * Om < -Infinity < SetlBoolean < SetlInt & Real < SetlString < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
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


package interpreter.types;

import comparableSet.ComparableList;
import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;

import java.util.Iterator;
import java.util.List;

public class SetlList extends CollectionValue {

    private ComparableList<Value> mList;
    private ComparableList<Value> mOriginalList;

    public SetlList() {
        mList               = new ComparableList<Value>();
        mOriginalList       = null;
    }

    private SetlList(ComparableList<Value> list) {
        mList               = null;
        mOriginalList       = list;
    }

    public SetlList clone() {
        mOriginalList = getList();
        mList         = null;
        return new SetlList(mOriginalList);
    }

    public void separateFromOriginal() {
        if (mOriginalList != null) {
            mList = new ComparableList<Value>();
            for (Value v: mOriginalList) {
                mList.add(v.clone());
            }
            mOriginalList = null;
        }
    }

    private ComparableList<Value> getList() {
        if (mList != null) {
            return mList;
        } else {
            return mOriginalList;
        }
    }

    public Iterator<Value> iterator() {
        return getList().iterator();
    }

    public int size() {
        return getList().size();
    }

    public void compress() {
        while (true) {
            if (getList().size() > 0 && getList().getLast() == SetlOm.OM) {
                getList().removeLast();
            } else {
                break;
            }
        }
    }

    /* arithmetic operations */

    public Value add(Value summand) throws IncompatibleTypeException {
        if (summand instanceof SetlList) {
            SetlList s      = ((SetlList) summand).clone();
            s.separateFromOriginal();
            SetlList result = this.clone();
            result.separateFromOriginal();
            result.mList.addAll(s.mList);
            return result;
        } else if (summand instanceof SetlString) {
            return ((SetlString)summand).addFlipped(this);
        }  else {
            throw new IncompatibleTypeException("Right-hand-side of '" + this + " + " + summand + "' is not a list or string.");
        }
    }

    /* operations on compound values (Lists, Sets [, Strings]) */

    public void addMember(Value element) {
        separateFromOriginal();
        mList.add(element);
    }

    public SetlBoolean containsMember(Value element) {
        // sadly the build in function seems to compare based on reference only...
        //return SetlBoolean.get(getList().contains(element));
        for (Value v: getList()) {
            if (v.equals(element)) {
                return SetlBoolean.TRUE;
            }
        }
        return SetlBoolean.FALSE;
    }

    public Value firstMember() {
        if (size() < 1) {
            return SetlOm.OM;
        }
        return getList().getFirst().clone();
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
        if (vIndex instanceof SetlInt) {
            index = ((SetlInt)vIndex).intValue();
        } else {
            throw new IncompatibleTypeException("Index '" + vIndex + "' is not a integer.");
        }
        if (index < 1) {
            throw new NumberToLargeException("Index '" + index + "' is lower as '1'.");
        }
        if (index > size()) {
            return SetlOm.OM;
        }
        return getList().get(index - 1);
    }

    public Value getMembers(Value vLow, Value vHigh) throws SetlException {
        int low = 0, high = 0;
        if (vLow instanceof SetlInt) {
            low = ((SetlInt)vLow).intValue();
        } else {
            throw new IncompatibleTypeException("Lower bound '" + vLow + "' is not a integer.");
        }
        if (vHigh instanceof SetlInt) {
            high = ((SetlInt)vHigh).intValue();
        } else {
            throw new IncompatibleTypeException("Upper bound '" + vHigh + "' is not a integer.");
        }

        SetlList result = new SetlList();

        if (high > size()) {
            throw new NumberToLargeException("Upper bound '" + high + "' is larger as list size '" + size() + "'.");
        }
        if (size() == 0) {
            throw new NumberToLargeException("Lower bound '" + low + "' is larger as list size '" + size() + "'.");
        }
        if (low < 1) {
            throw new NumberToLargeException("Lower bound '" + low + "' is lower as '1'.");
        }
        for (int i = low - 1; i < high; i++) {
            result.addMember(getList().get(i).clone());
        }
        return result;
    }

    public Value lastMember() {
        if (size() < 1) {
            return SetlOm.OM;
        }
        return getList().getLast().clone();
    }

    public Value maximumMember() throws SetlException {
        Value max = SetlOm.OM;
        for (Value v: getList()) {
            if (v.maximum(max).equals(v)) {
                max = v;
            }
        }
        return max.clone();
    }

    public Value minimumMember() throws SetlException {
        if (size() < 1) {
            return SetlOm.OM;
        }
        Value min = getList().get(0);
        if (size() > 1) {
            for (Value v: getList()) {
                if (v.minimum(min).equals(v)) {
                    min = v;
                }
            }
        }
        return min.clone();
    }

    public void removeMember(Value element) {
        separateFromOriginal();
        // sadly the build in function seems to compare based on reference only
        //mList.remove(element);
        int elm = -1;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).equals(element)) {
                elm = i;
                break;
            }
        }
        if (elm != -1) {
            mList.remove(elm);
        }
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
        if (vIndex instanceof SetlInt) {
            index = ((SetlInt)vIndex).intValue();
        } else {
            throw new IncompatibleTypeException("Index '" + vIndex + "' is not a integer.");
        }
        if (index < 1) {
            throw new NumberToLargeException("Index '" + index + "' is lower as '1'.");
        }
        if (v == SetlOm.OM) {
            if (index <= mList.size()) {
                mList.set(index - 1, v);
            }
            compress();
        } else {
            while (index > mList.size()) {
                mList.add(SetlOm.OM);
            }
            mList.set(index - 1, v.clone());
        }
    }

    /* calls (element access) */

    public Value call(List<Expr> exprs, List<Value> args) throws SetlException {
        int   aSize  = args.size();
        Value vFirst = (aSize >= 1)? args.get(0) : null;
        if (args.contains(RangeDummy.RD)) {
            if (aSize == 2 && vFirst == RangeDummy.RD) {
                // everything up to high boundary: this(  .. y);
                return getMembers(new SetlInt(1), args.get(1));

            } else if (aSize == 2 && args.get(1) == RangeDummy.RD) {
                // everything from low boundary:   this(x ..  );
                return getMembers(vFirst, new SetlInt(size()));

            } else if (aSize == 3 && args.get(1) == RangeDummy.RD) {
                // full range spec:                this(x .. y);
                return getMembers(vFirst, args.get(2));
            }
            throw new UndefinedOperationException("Can not perform call with arguments '" + args + "' on '" + this + "'; arguments are malformed.");
        } else if (aSize == 1) {
            return getMember(vFirst);
        } else {
            throw new UndefinedOperationException("Can not perform call with arguments '" + args + "' on '" + this + "'; arguments are malformed.");
        }
    }

    public String toString() {
        return getList().toString();
    }

    // Compare two Values.  Returns -1 if this value is less than the value given
    // as argument, +1 if its greater and 0 if both values contain the same
    // elements.
    // Useful output is only possible if both values are of the same type.
    // "incomparable" values, e.g. of different types are ranked as follows:
    // SetlOm < SetlBoolean < SetlInt & SetlReal < SetlString < SetlSet < SetlList < SetlDefinition
    // This ranking is necessary to allow sets and lists of different types.
    public int compareTo(Value v){
        if (v instanceof SetlList) {
            SetlList l = (SetlList) v;
            return getList().compareTo(l.getList());
        } else if (v instanceof SetlDefinition) {
            // only SetlDefinition is bigger
            return -1;
        } else {
            // everything else is smaller
            return 1;
        }
    }

}

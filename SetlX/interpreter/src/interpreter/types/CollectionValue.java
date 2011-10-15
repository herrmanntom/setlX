package interpreter.types;

import interpreter.Environment;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;

import java.util.Iterator;

public abstract class CollectionValue extends Value implements Iterable<Value> {

    public abstract Iterator<Value> iterator();

    public abstract void            addMember(Value element);

    public final    Value           addMembers() throws SetlException {
        Value sum = null;
        for (Value v: this) {
            if (sum == null) {
                sum = v.clone();
            } else {
                sum = sum.add(v);
            }
        }
        return (sum != null)? sum : SetlOm.OM;
    }

    public final    Value           arbitraryMember() throws NumberToLargeException {
        if (this.size() < 1) {
            return SetlOm.OM;
        } else {
            int needle = Environment.getRandomInt(this.size());
            int pos    = 0;
            for (Value v: this) {
                if (pos == needle) {
                    return v.clone();
                }
                pos++;
            }
            throw new NumberToLargeException("Collection index `" + pos + "´ in `" + this + "´ is out of bounds.");
        }
    }

    public abstract SetlBoolean     containsMember(Value element);

    public abstract Value           firstMember();

    public abstract Value           lastMember();

    public abstract Value           maximumMember() throws SetlException;

    public abstract Value           minimumMember() throws SetlException;

    public final    Value           multiplyMembers() throws SetlException {
        Value product = null;
        for (Value v: this) {
            if (product == null) {
                product = v.clone();
            } else {
                product = product.multiply(v);
            }
        }
        return (product != null)? product : SetlOm.OM;
    }

    public abstract void            removeMember(Value element);

    public abstract void            removeFirstMember();

    public abstract void            removeLastMember();

    public abstract int             size();
}


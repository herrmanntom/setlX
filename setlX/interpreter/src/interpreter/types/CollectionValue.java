package interpreter.types;

import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.utilities.Environment;

import java.util.Iterator;

public abstract class CollectionValue extends Value implements Iterable<Value> {

    public abstract Iterator<Value> iterator();

    /* operations on compound values (Lists/Tuples, Sets [, Strings]) */

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
        return (sum != null)? sum : new SetlInt(0);
    }

    public final    Value           arbitraryMember() {
        if (this.size() < 1) {
            return SetlOm.OM;
        } else if (this.size() % 2 == 0) {
             // lets keep some balance to avoid to many restructurings of the underling collection
            return this.firstMember();
        } else {
            return this.lastMember();
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
        return (product != null)? product : new SetlInt(1);
    }

    public final    Value           randomMember() throws NumberToLargeException {
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
            // this sould never be reached
            throw new NumberToLargeException("Collection index '" + pos + "' into '" + this + "' is out of bounds.");
        }
    }


    public abstract void            removeMember(Value element);

    public abstract void            removeFirstMember();

    public abstract void            removeLastMember();

    public abstract int             size();
}


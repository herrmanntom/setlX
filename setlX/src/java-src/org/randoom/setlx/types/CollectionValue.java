package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;

import java.util.Iterator;

public abstract class CollectionValue extends Value implements Iterable<Value> {

    public abstract Iterator<Value> iterator();

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    public abstract void            addMember(Value element);

    public final    Value           arbitraryMember() {
        if (this.size() < 1) {
            return Om.OM;
        } else if (this.size() % 2 == 0) {
             // lets keep some balance to avoid to many restructurings of the underling collection
            return this.firstMember();
        } else {
            return this.lastMember();
        }
    }

    public abstract SetlBoolean     containsMember(Value element);

    // compare not only own members, but also all members contained in own members
    public          SetlBoolean     containsMemberRecursive(Value element) {
        for (Value v: this) {
            if (v.equals(element)) {
                return SetlBoolean.TRUE;
            } else if (v instanceof CollectionValue) {
                CollectionValue innerValue = (CollectionValue) v;
                if (innerValue.containsMemberRecursive(element) == SetlBoolean.TRUE) {
                    return SetlBoolean.TRUE;
                }
            }
        }
        return SetlBoolean.FALSE;
    }

    public abstract Value           firstMember();

    public abstract Value           lastMember();

    public abstract Value           maximumMember() throws SetlException;

    public abstract Value           minimumMember() throws SetlException;

    public          Value           multiplyMembers(Value neutral) throws SetlException {
        Value product = null;
        for (Value v: this) {
            if (product == null) {
                product = v.clone();
            } else {
                product = product.multiply(v);
            }
        }
        return (product != null)? product : neutral;
    }

    public final    Value           randomMember() throws NumberToLargeException {
        if (this.size() < 1) {
            return Om.OM;
        } else {
            int needle = Environment.getRandomInt(this.size());
            int pos    = 0;
            for (Value v: this) {
                if (pos == needle) {
                    return v.clone();
                }
                pos++;
            }
            // this should never be reached
            throw new NumberToLargeException(
                "Collection index '" + pos + "' into '" + this + "' is out of bounds."
            );
        }
    }

    public abstract void            removeMember(Value element);

    public abstract void            removeFirstMember();

    public abstract void            removeLastMember();

    public abstract int             size();

    public          Value           sumMembers(Value neutral) throws SetlException {
        Value sum = null;
        for (Value v: this) {
            if (sum == null) {
                sum = v.clone();
            } else {
                sum = sum.sum(v);
            }
        }
        return (sum != null)? sum : neutral;
    }

    /* string and char operations */

    public abstract String          canonical();

    /* term operations */

    public abstract MatchResult     matchesTerm(Value other) throws IncompatibleTypeException;
}


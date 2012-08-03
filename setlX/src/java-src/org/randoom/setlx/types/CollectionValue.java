package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;

import java.util.Iterator;

public abstract class CollectionValue extends Value implements Iterable<Value> {

    public abstract Iterator<Value> iterator();

    public          Value           rnd() throws NumberToLargeException {
        if (this.size() < 1) {
            return Om.OM;
        } else {
            final int needle = Environment.getRandomInt(this.size());
                  int pos    = 0;
            for (final Value v: this) {
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

    public abstract void            addMember(final Value element);

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

    public abstract SetlBoolean     containsMember(final Value element) throws IncompatibleTypeException;

    // compare not only own members, but also all members contained in own members
    public          SetlBoolean     containsMemberRecursive(final Value element) {
        for (final Value v: this) {
            if (v.equalTo(element)) {
                return SetlBoolean.TRUE;
            } else if (v instanceof CollectionValue && !(v instanceof SetlString)) {
                final CollectionValue innerValue = (CollectionValue) v;
                if (innerValue.containsMemberRecursive(element) == SetlBoolean.TRUE) {
                    return SetlBoolean.TRUE;
                }
            }
        }
        return SetlBoolean.FALSE;
    }

    public abstract Value           firstMember();

    public abstract Value           getMember(final Value index) throws SetlException;

    public          SetlString      join(final Value separator) {
        final SetlString      sep    = separator.str();
        final SetlString      result = new SetlString();

        final Iterator<Value> iter   = iterator();
        while (iter.hasNext()) {
            result.addMember(iter.next());
            if (iter.hasNext()) {
                result.addMember(sep);
            }
        }
        return result;
    }

    public abstract Value           lastMember();

    public abstract Value           maximumMember() throws SetlException;

    public abstract Value           minimumMember() throws SetlException;

    public          Value           productOfMembers(final Value neutral) throws SetlException {
        Value product = null;
        for (final Value v: this) {
            if (product == null) {
                product = v.clone();
            } else {
                product = product.productAssign(v);
            }
        }
        return (product != null)? product : neutral;
    }

    public abstract void            removeMember(final Value element) throws IncompatibleTypeException;

    public abstract void            removeFirstMember();

    public abstract void            removeLastMember();

    public abstract int             size();

    public          Value           sumMembers(final Value neutral) throws SetlException {
        Value sum = null;
        for (final Value v: this) {
            if (sum == null) {
                sum = v.clone();
            } else {
                sum = sum.sumAssign(v);
            }
        }
        return (sum != null)? sum : neutral;
    }

    /* string and char operations */

    public abstract void            canonical(final StringBuilder sb);

    /* term operations */

    public abstract MatchResult     matchesTerm(final Value other) throws IncompatibleTypeException;
}


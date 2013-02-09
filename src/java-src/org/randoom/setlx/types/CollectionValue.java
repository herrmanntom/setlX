package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

import java.util.Iterator;

public abstract class CollectionValue extends Value implements Iterable<Value> {

    @Override
    public abstract Iterator<Value> iterator();

    public abstract Iterator<Value> descendingIterator();

    @Override
    public          Value           rnd(final State state) throws NumberToLargeException {
        final int size = this.size();
        if (size < 1) {
            return Om.OM;
        } else {
            final int             needle = state.getRandomInt(size);
                  int             pos    = 0;
                  Iterator<Value> iter   = null;
            if (needle < size / 2) {
                iter = this.iterator();
                while (iter.hasNext()) {
                    if (pos == needle) {
                        return iter.next().clone();
                    } else {
                        iter.next();
                    }
                    ++pos;
                }
            } else {
                iter = this.descendingIterator();
                pos  = size - 1;
                while (iter.hasNext()) {
                    if (pos == needle) {
                        return iter.next().clone();
                    } else {
                        iter.next();
                    }
                    --pos;
                }
            }
            // this should never be reached
            throw new NumberToLargeException(
                "Collection index '" + needle + "' into '" + this + "' is out of bounds."
            );
        }
    }

    @Override
    public abstract void            addMember(final State state, final Value element);

    @Override
    public final    Value           arbitraryMember() {
        if (this.size() < 1) {
            return Om.OM;
        } else if (this.size() % 2 == 0) {
             // lets keep some balance to avoid restructuring of the underling collection
            return this.firstMember();
        } else {
            return this.lastMember();
        }
    }

    @Override
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

    @Override
    public abstract Value           firstMember();

    @Override
    public abstract Value           getMember(final State state, final Value index) throws SetlException;

    @Override
    public          SetlString      join(final State state, final Value separator) {
        final SetlString      sep    = separator.str();
        final SetlString      result = new SetlString();

        final Iterator<Value> iter   = iterator();
        while (iter.hasNext()) {
            result.addMember(state, iter.next());
            if (iter.hasNext()) {
                result.addMember(state, sep);
            }
        }
        return result;
    }

    @Override
    public abstract Value           lastMember();

    @Override
    public abstract Value           maximumMember(final State state) throws SetlException;

    @Override
    public abstract Value           minimumMember(final State state) throws SetlException;

    @Override
    public          Value           productOfMembers(final State state, final Value neutral) throws SetlException {
        Value product = null;
        for (final Value v: this) {
            if (product == null) {
                product = v.clone();
            } else {
                product = product.productAssign(state, v);
            }
        }
        return (product != null)? product : neutral;
    }

    @Override
    public abstract void            removeMember(final Value element) throws IncompatibleTypeException;

    @Override
    public abstract Value           removeFirstMember();

    @Override
    public abstract Value           removeLastMember();

    @Override
    public abstract int             size();

    @Override
    public          Value           sumOfMembers(final State state, final Value neutral) throws SetlException {
        Value sum = null;
        for (final Value v: this) {
            if (sum == null) {
                sum = v.clone();
            } else {
                sum = sum.sumAssign(state, v);
            }
        }
        return (sum != null)? sum : neutral;
    }

    /* string and char operations */

    @Override
    public abstract void            canonical(final State state, final StringBuilder sb);

    /* term operations */

    @Override
    public abstract MatchResult     matchesTerm(final State state, final Value other) throws IncompatibleTypeException;
}


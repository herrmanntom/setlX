package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

import java.util.Iterator;

/**
 * This base class provides some functionality for all collection values.
 */
public abstract class CollectionValue extends Value implements Iterable<Value> {

    @Override
    public abstract Iterator<Value> iterator();

    /**
     * Returns an reversed iterator over the members of this collection value.
     *
     * @return an Iterator.
     */
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
                "Collection index '" + needle + "' into '" + this.toString(state) + "' is out of bounds."
            );
        }
    }

    @Override
    public abstract void            addMember(final State state, final Value element);

    @Override
    public final    Value           arbitraryMember(final State state) {
        if (this.size() < 1) {
            return Om.OM;
        } else if (this.size() % 2 == 0) {
             // lets keep some balance to avoid restructuring of the underling collection
            return this.firstMember(state);
        } else {
            return this.lastMember(state);
        }
    }

    @Override
    public abstract SetlBoolean     containsMember(final State state, final Value element) throws IncompatibleTypeException;

    /**
     * Test if this value contains the specified element.
     * Compares not only own members, but also all members contained in own members.
     *
     * @param element Element to search for.
     * @return        True if the element is contained, false otherwise.
     */
    public          SetlBoolean     containsMemberRecursive(final Value element) {
        for (final Value v : this) {
            if (v.equalTo(element)) {
                return SetlBoolean.TRUE;
            } else if (v instanceof CollectionValue && v.getClass() != SetlString.class) {
                final CollectionValue innerValue = (CollectionValue) v;
                if (innerValue.containsMemberRecursive(element) == SetlBoolean.TRUE) {
                    return SetlBoolean.TRUE;
                }
            }
        }
        return SetlBoolean.FALSE;
    }

    @Override
    public final    Value           firstMember(final State state) {
        return firstMember();
    }

    /**
     * Get the first member of this collection value.
     *
     * @return First member of this collection value.
     */
    public abstract Value           firstMember();

    @Override
    public abstract Value           getMember(final State state, final Value index) throws SetlException;

    @Override
    public          SetlString      join(final State state, final Value separator) throws SetlException {
        final SetlString      sep    = separator.str(state);
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
    public final    Value           lastMember(final State state) {
        return lastMember();
    }

    /**
     * Get the last member of this collection value.
     *
     * @return Last member of this collection value.
     */
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
    public abstract void            removeMember(final State state, final Value element) throws IncompatibleTypeException;

    @Override
    public final    Value           removeFirstMember(final State state) {
        return removeFirstMember();
    }

    /**
     * Remove the first member of this collection value.
     *
     * @return First member of this collection value.
     */
    public abstract Value           removeFirstMember();

    @Override
    public final    Value           removeLastMember(final State state) {
        return removeLastMember();
    }

    /**
     * Remove the last member of this collection value.
     *
     * @return Last member of this collection value.
     */
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
    public abstract MatchResult     matchesTerm(final State state, final Value other) throws SetlException;
}


package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * This base class provides some functionality for all collection values accessible
 * via a numeric index.
 */
public abstract class IndexedCollectionValue extends CollectionValue {

    @Override
    public final    Value           rnd(final State state) {
        if (this.size() < 1) {
            return Om.OM;
        } else {
            try {
                return getMember(state.getRandomInt(this.size()) +1);
            } catch (final SetlException se) {
                return Om.OM;
            }
        }
    }

    /**
     * Get a specified member of this collection value.
     *
     * @param index          Index of the member to get. Note: Index starts with 1, not 0.
     * @return               Member of this collection at the specified index.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract Value           getMember(final int index) throws SetlException;

    @Override
    public Value collectionAccess(final State state, final List<Value> args) throws SetlException {
        final int   aSize   = args.size();
        final Value vFirst  = (aSize >= 1)? args.get(0) : null;
        if (args.contains(RangeDummy.RD)) {
            if (aSize == 2 && vFirst == RangeDummy.RD) {
                // everything up to high boundary: this(  .. y);
                return getMembers(state, Rational.ONE, args.get(1));

            } else if (aSize == 2 && args.get(1) == RangeDummy.RD) {
                // everything from low boundary:   this(x ..  );
                return getMembers(state, vFirst, Rational.valueOf(size()));

            } else if (aSize == 3 && args.get(1) == RangeDummy.RD) {
                // full range spec:                this(x .. y);
                return getMembers(state, vFirst, args.get(2));
            }
            throw new UndefinedOperationException(
                    "Can not access elements using arguments '" + args + "' on '" + this + "';" +
                            " arguments are malformed."
            );
        } else if (aSize == 1) {
            return getMember(state, vFirst);
        } else {
            throw new UndefinedOperationException(
                    "Can not access elements using arguments '" + args + "' on '" + this + "';" +
                            " arguments are malformed."
            );
        }
    }

    /**
     * Get a range of elements contained in this value.
     *
     * @param state          Current state of the running setlX program.
     * @param low            First element to get.
     * @param high           Last element to get.
     * @return               New instance of same class as this, containing selected elements from this value.
     * @throws SetlException in case of some (user-) error.
     */
    public Value getMembers(final State state, final Value low, final Value high) throws SetlException {
        final int thisSize = size();
        final int lowFromStart;
        final int highFromStart;
        if (low.isInteger() == SetlBoolean.TRUE) {
            final int lowInt = low.jIntValue();
            if (lowInt == 0) {
                throw new NumberToLargeException(
                        "Lower bound '" + low.toString(state) + "' is invalid."
                );
            } else if (lowInt > 0) {
                lowFromStart = lowInt;
            } else /* if (lowInt < 0) */ {
                // negative index counts from end of the vector - convert it to actual index
                lowFromStart = thisSize + lowInt + 1;
            }
        } else {
            throw new IncompatibleTypeException(
                    "Lower bound '" + low.toString(state) + "' is not a integer."
            );
        }
        if (high.isInteger() == SetlBoolean.TRUE) {
            final int highInt = high.jIntValue();
            if (highInt >= 0) {
                highFromStart = highInt;
            } else /* if (highInt < 0) */ {
                // negative index counts from end of the string - convert it to actual index
                highFromStart = thisSize + highInt + 1;
            }
        } else {
            throw new IncompatibleTypeException(
                    "Upper bound '" + high.toString(state) + "' is not a integer."
            );
        }

        int size = highFromStart - (lowFromStart - 1);
        if (size < 0 || lowFromStart < 1 || highFromStart < 1 || lowFromStart > thisSize) {
            size = 0;
        }
        return getMembers(state, size, lowFromStart, highFromStart);
    }

    /**
     * Get a range of elements contained in this value.
     *
     * @param state                   Current state of the running setlX program.
     * @param expectedNumberOfMembers Number of contained elements that should be returned.
     * @param lowFromStart            First element to get.
     * @param highFromStart           Last element to get.
     * @return                        New instance of same class as this, containing selected elements from this value.
     * @throws SetlException          in case of some (user-) error.
     */
    protected Value getMembers(final State state, final int expectedNumberOfMembers, final int lowFromStart, final int highFromStart) throws SetlException {
        throw new IncompatibleTypeException(
                "Lazy programmer detected." // override this method, if getMembers() from this class is used.
        );
    }

    /**
     * Set a specified member of this collection value.
     *
     * @param state          Current state of the running setlX program.
     * @param index          The of the member to set. Note: Index starts with 1, not 0.
     * @param value          The value to set the member to.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract void setMember(final State state, final int index, final Value value) throws SetlException;

}


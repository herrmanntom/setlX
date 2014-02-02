package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.State;

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
     * Get a specified member of this list.
     *
     * @param index          Index of the member to get. Note: Index starts with 1, not 0.
     * @return               Member of this list at the specified index.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public abstract Value           getMember(final int index) throws SetlException;

}


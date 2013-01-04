package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.State;

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

    public abstract Value           getMember(final int index) throws SetlException;

}


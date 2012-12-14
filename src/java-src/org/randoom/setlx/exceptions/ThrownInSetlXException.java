package org.randoom.setlx.exceptions;

import org.randoom.setlx.types.Value;

public class ThrownInSetlXException extends CatchableInSetlXException {

    private static final long   serialVersionUID = -6807457645603862681L;
    private              Value  mValue;

    public ThrownInSetlXException(Value value) {
        super("Uncaught user exception: " + value.toString());
        mValue = value;
    }

    public Value getValue() {
        return mValue;
    }
}


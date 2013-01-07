package org.randoom.setlx.utilities;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;

public class ReturnMessage {

    public final static ReturnMessage BREAK    = new ReturnMessage(null);
    public final static ReturnMessage CONTINUE = new ReturnMessage(null);
    public final static ReturnMessage OM       = new ReturnMessage(Om.OM);

    private final Value mPayload;

    private ReturnMessage(final Value payload) {
        mPayload = payload;
    }

    public static ReturnMessage createMessage(final Value payload) {
        if (payload == Om.OM) {
            return OM;
        }
        return new ReturnMessage(payload);
    }

    @Override
    public ReturnMessage clone() {
        return createMessage(mPayload.clone());
    }

    public Value getPayload() {
        return mPayload;
    }
}


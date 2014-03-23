package org.randoom.setlx.utilities;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;

/**
 * Wrapper object to encapsulate messages send by setlX code through JAVAs
 * return value.
 */
public class ReturnMessage {

    /**
     * Message representing `break' statement in SetlX.
     */
    public final static ReturnMessage BREAK    = new ReturnMessage(null);
    /**
     * Message representing `continue' statement in SetlX.
     */
    public final static ReturnMessage CONTINUE = new ReturnMessage(null);
    /**
     * Message representing `return' statement without a value, or procedure ending
     * without `return' in SetlX.
     */
    public final static ReturnMessage OM       = new ReturnMessage(Om.OM);

    private final Value mPayload;

    private ReturnMessage(final Value payload) {
        mPayload = payload;
    }

    /**
     * Create a new message returning some SetlX value. Usually created by
     * `return <value>' where value would be the payload.
     *
     * @param payload Return value to encapsulate.
     * @return        Message containing payload.
     */
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

    /**
     * Get payload/return value contained in this message.
     *
     * @return payload/return value contained in this message; `null' if message represents break or continue.
     */
    public Value getPayload() {
        return mPayload;
    }
}


package org.randoom.setlx.exceptions;

import org.randoom.setlx.types.Value;

public class ReturnException extends SetlException {
    private Value mResult;

    public ReturnException(Value result) {
        super("Return");
        mResult = result;
    }

    public Value getValue() {
        return mResult;
    }
}


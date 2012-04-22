package org.randoom.setlx.exceptions;

/*
 * Exceptions inhereting from this class are supposed to be catchable by SetlX's
 * own try-catch block.
 */

public abstract class CatchableInSetlXException extends SetlException {
    public CatchableInSetlXException(String msg) {
        super(msg);
    }
}


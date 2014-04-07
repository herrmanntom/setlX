package org.randoom.setlx.exceptions;

import org.randoom.setlx.types.Value;

/**
 * Exception thrown explicitly by SetlX code, via throw function.
 */
public class ThrownInSetlXException extends CatchableInSetlXException {

    private static final long serialVersionUID = -6807457645603862681L;

    private final Value value;

    /**
     * Create a new ThrownInSetlXException.
     *
     * @param value User supplied value.
     */
    public ThrownInSetlXException(final Value value) {
        super("Uncaught user exception: " + value.toString());
        this.value = value;
    }

    /**
     * Get value used when creating this exception.
     *
     * @return Value used when creating this exception.
     */
    public Value getValue() {
        return value;
    }
}


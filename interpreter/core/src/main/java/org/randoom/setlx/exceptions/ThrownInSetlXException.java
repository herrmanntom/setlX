package org.randoom.setlx.exceptions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Exception thrown explicitly by SetlX code, via throw function.
 */
public class ThrownInSetlXException extends CatchableInSetlXException {

    private static final long serialVersionUID = -6807457645603862681L;

    private final Value value;

    /**
     * Create a new ThrownInSetlXException.
     *
     * @param state Current state of the running setlX program.
     * @param value User supplied value.
     */
    public ThrownInSetlXException(final State state, final Value value) {
        super("Uncaught user exception: " + value.toString(state));
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


package org.randoom.setlx.exceptions;

/**
 * Exception thrown, when the user wants to execute a function with the incorrect number of parameters.
 */
public class IncorrectNumberOfParametersException extends CatchableInSetlXException {

    private static final long serialVersionUID = 1741704469662619744L;

    /**
     * Create new IncorrectNumberOfParametersException.
     *
     * @param msg More detailed message.
     */
    public IncorrectNumberOfParametersException(final String msg) {
        super(msg);
    }
}


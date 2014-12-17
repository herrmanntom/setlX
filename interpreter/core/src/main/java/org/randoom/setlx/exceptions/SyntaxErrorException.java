package org.randoom.setlx.exceptions;

import java.util.LinkedList;

/**
 * Exception thrown, when parsing incorrect code.
 */
public class SyntaxErrorException extends ParserException {

    private static final long serialVersionUID = -8624774595405955263L;

    private final LinkedList<String> errors;

    private SyntaxErrorException(final String message, final LinkedList<String> errors) {
        super(message, null);
        this.errors  = errors;
    }

    /**
     * Create a new SyntaxErrorException.
     *
     * @param errors  List of error messages.
     * @param summary Summary of errors occurred.
     * @return        New SyntaxErrorException.
     */
    public static SyntaxErrorException create(final LinkedList<String> errors, final String summary) {
        String message = "";
        for (final String s : errors) {
            message += s + "\n";
        }
        message += summary;
        return new SyntaxErrorException(message, errors);
    }

    /**
     * Get list of error messages.
     *
     * @return List of error messages
     */
    public LinkedList<String> getErrors() {
        return this.errors;
    }
}


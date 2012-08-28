package org.randoom.setlx.exceptions;

import java.util.LinkedList;

public class SyntaxErrorException extends ParserException {

    private static final long serialVersionUID = -8624774595405955263L;

    public static SyntaxErrorException create(final LinkedList<String> errors, final String summary) {
        String message = summary;
        for (final String s : errors) {
            message = s + "\n" + message;
        }
        return new SyntaxErrorException(message, errors);
    }

    private final LinkedList<String> errors;

    private SyntaxErrorException(final String message, final LinkedList<String> errors) {
        super(message);
        this.errors  = errors;
    }

    public LinkedList<String> getErrors() {
        return this.errors;
    }
}


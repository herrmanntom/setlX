package org.randoom.setlx.exceptions;

public class SyntaxErrorException extends ParserException {

    private static final long serialVersionUID = -8624774595405955263L;

    public SyntaxErrorException(String msg) {
        super(msg);
    }
}


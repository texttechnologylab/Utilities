package org.texttechnologylab.utilities.process.exception;

public class ParamNotExistException extends Exception {

    public ParamNotExistException() {
    }

    public ParamNotExistException(String pMessage) {
        super(pMessage);
    }

    public ParamNotExistException(String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }

    public ParamNotExistException(Throwable pCause) {
        super(pCause);
    }

}


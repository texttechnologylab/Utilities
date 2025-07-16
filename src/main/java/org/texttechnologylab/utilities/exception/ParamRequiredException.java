package org.texttechnologylab.utilities.exception;

public class ParamRequiredException extends Exception {

    public ParamRequiredException() {
    }

    public ParamRequiredException(String pMessage) {
        super(pMessage);
    }

    public ParamRequiredException(String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }

    public ParamRequiredException(Throwable pCause) {
        super(pCause);
    }

}

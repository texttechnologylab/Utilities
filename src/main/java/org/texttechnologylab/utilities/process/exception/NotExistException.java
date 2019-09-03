package org.texttechnologylab.utilities.process.exception;

public class NotExistException extends Exception {

    public NotExistException() {
    }

    public NotExistException(String pMessage) {
        super(pMessage);
    }

    public NotExistException(String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }

    public NotExistException(Throwable pCause) {
        super(pCause);
    }

}

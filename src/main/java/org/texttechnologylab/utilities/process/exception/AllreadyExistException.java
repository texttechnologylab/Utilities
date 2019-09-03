package org.texttechnologylab.utilities.process.exception;

public class AllreadyExistException extends Exception {

    public AllreadyExistException() {
    }

    public AllreadyExistException(String pMessage) {
        super(pMessage);
    }

    public AllreadyExistException(String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }

    public AllreadyExistException(Throwable pCause) {
        super(pCause);
    }

}

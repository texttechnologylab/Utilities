package org.texttechnologylab.utilities.process.exception;

public class InvalidModelException extends Exception {

    public InvalidModelException() {
    }

    public InvalidModelException(String pMessage) {
        super(pMessage);
    }

    public InvalidModelException(String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }

    public InvalidModelException(Throwable pCause) {
        super(pCause);
    }

}

package org.texttechnologylab.utilities.process.exception;

public class ProgressNotExistException extends Exception {

    public ProgressNotExistException() {
    }

    public ProgressNotExistException(String pMessage) {
        super(pMessage);
    }

    public ProgressNotExistException(String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }

    public ProgressNotExistException(Throwable pCause) {
        super(pCause);
    }

}

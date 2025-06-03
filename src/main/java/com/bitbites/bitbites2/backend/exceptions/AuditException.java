package com.bitbites.bitbites2.backend.exceptions;

public class AuditException extends RuntimeException{
    public AuditException(String message) {
        super(message);
    }
}

package main.java.exceptions;

public class AuditException extends RuntimeException{
    public AuditException(String message) {
        super(message);
    }
}

package main.java.exceptions;

public class ConnectionException extends RuntimeException{
    public ConnectionException(String message) {
        super(message);
    }
}

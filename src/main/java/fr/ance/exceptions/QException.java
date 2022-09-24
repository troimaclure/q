package fr.ance.exceptions;

public class QException extends RuntimeException {
    String code;

    public QException(String message, String code) {
        super(message);
        this.code = code;
    }

}
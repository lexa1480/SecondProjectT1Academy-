package ru.T1Academy.SecondProject.aspect.exception;

public class LoggingAspectException extends RuntimeException {
    public LoggingAspectException(String message) {
        super(message);
    }
}
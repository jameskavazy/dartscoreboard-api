package com.jameskavazy.dartscoreboard.auth.exception;

public class InvalidTokenException extends Exception{
    public InvalidTokenException(String message, Throwable cause){
        super(message, cause);
    }
}

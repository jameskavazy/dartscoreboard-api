package com.jameskavazy.dartscoreboard.user.auth;

public class InvalidTokenException extends Exception{
    public InvalidTokenException(String message, Throwable cause){
        super(message, cause);
    }
}

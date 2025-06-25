package com.jameskavazy.dartscoreboard.match.exception;

public class InvalidPlayerTurnException extends RuntimeException {

    public InvalidPlayerTurnException(String msg){
        super("Could not process visit: " + msg);
    }
}

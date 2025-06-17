package com.jameskavazy.dartscoreboard.match.exception;

public class InvalidVisitScoreException extends RuntimeException{
    public InvalidVisitScoreException(){
        super("Match Not Found");
    }
}

package com.jameskavazy.dartscoreboard.match.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidHierarchyException extends RuntimeException {
    public InvalidHierarchyException(String msg){
        super("Could not perform action: " + msg);
    }
}
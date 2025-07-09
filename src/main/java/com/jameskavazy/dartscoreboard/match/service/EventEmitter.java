package com.jameskavazy.dartscoreboard.match.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EventEmitter {
    SseEmitter subscribe(String key, long timeout);
    void send(String key, Object data);
    void complete(String key);
}

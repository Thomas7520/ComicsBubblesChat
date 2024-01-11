package com.thomas7520.bubbleschat.util;

public class Message {

    private long startTime;
    private String message;

    public Message(long startTime, String message) {
        this.startTime = startTime;
        this.message = message;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getMessage() {
        return message;
    }
}

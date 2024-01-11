package com.thomas7520.bubbleschat.util;

import java.util.ArrayDeque;

public class Bubble {

    private final ArrayDeque<Message> messages = new ArrayDeque<>();

    public Bubble(long startTime, String text) {
        messages.add(new Message(startTime, text));
    }

    public ArrayDeque<Message> getMessages() {
        return messages;
    }
}

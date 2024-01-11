package com.thomas7520.bubbleschat.util;

import com.thomas7520.bubbleschat.BubblesConfig;

import java.util.ArrayDeque;

public class Bubble {

    private final ArrayDeque<Message> messages = new ArrayDeque<>();

    public Bubble(long startTime, String text) {
        messages.add(new Message(startTime, text, new SpecColor(BubblesConfig.CLIENT.colorOutline.get())
                , new SpecColor(BubblesConfig.CLIENT.colorInside.get()), new SpecColor(BubblesConfig.CLIENT.colorText.get())));
    }

    public ArrayDeque<Message> getMessages() {
        return messages;
    }
}

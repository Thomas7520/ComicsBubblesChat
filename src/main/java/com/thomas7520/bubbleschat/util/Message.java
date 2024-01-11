package com.thomas7520.bubbleschat.util;

public class Message {

    private long startTime;
    private String message;
    private SpecColor colorOutline;
    private SpecColor colorInside;
    private SpecColor colorText;

    public Message(long startTime, String message, SpecColor color, SpecColor colorInside, SpecColor colorText) {
        this.startTime = startTime;
        this.message = message;
        this.colorOutline = color;
        this.colorInside = colorInside;
        this.colorText = colorText;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getMessage() {
        return message;
    }

    public SpecColor getColorInside() {
        return colorInside;
    }

    public SpecColor getColorOutline() {
        return colorOutline;
    }

    public SpecColor getColorText() {
        return colorText;
    }
}

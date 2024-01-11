package com.thomas7520.bubbleschat.util;

import java.awt.*;
import java.util.List;

public class SpecColor {


    private int red;
    private int green;
    private int blue;
    private int alpha;


    public SpecColor(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public SpecColor(List<Integer> rgba) {
        this.red = rgba.get(0);
        this.green = rgba.get(1);
        this.blue = rgba.get(2);
        this.alpha = rgba.get(3);
    }

    public SpecColor(Integer[] values) {
        this.red = Math.round(values[0]);
        this.green = Math.round(values[1]);
        this.blue = Math.round(values[2]);
        this.alpha = Math.round(values[3]);

    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public Color getColor() {
        return new Color(red, green, blue, alpha);
    }

    public int getRGB() {
        return getColor().getRGB();
    }

    public Integer[] getValues() {
        return new Integer[]{red, green, blue, alpha};
    }

}

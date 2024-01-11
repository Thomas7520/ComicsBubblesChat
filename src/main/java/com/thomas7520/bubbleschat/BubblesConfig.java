package com.thomas7520.bubbleschat;

import net.minecraftforge.common.config.Config;

@Config(modid = ComicsBubblesChat.MODID)
public class BubblesConfig {

    public static Client client = new Client();

    public static Server server = new Server();

    public static class Client {

        @Config.Comment({"Adjust the color of the bubbles outline | arg = red,green,blue,alpha"})
        @Config.RangeInt(min = 0, max = 255)
        public int[] colorOutline;

        @Config.Comment("Adjust the color inside the bubbles | arg = red,green,blue,alpha")
        @Config.RangeInt(min = 0, max = 255)
        public int[] colorInside;

        @Config.Comment("Adjust the color inside the bubbles | arg = red,green,blue,alpha")
        @Config.RangeInt(min = 0, max = 255)
        public int[] colorText;

        @Config.Comment("Adjust duration time of bubbles")
        @Config.RangeInt(min = 0, max = 60)
        public int durationBubbles;

        @Config.Comment("Max Bubbles Stackable on a player's head")
        @Config.RangeInt(min = 0)
        public int maxBubblesStack;

        @Config.Comment("Max line width")
        @Config.RangeInt(min = 50)
        public int lineWidth;

        public Client() {
            colorOutline = new int[]{0, 0, 0, 220};
            colorInside = new int[]{203, 203, 203, 220};
            colorText = new int[]{0, 0, 0, 220};
            durationBubbles = 10;
            lineWidth = 150;
        }
    }

    public static class Server {


        @Config.Comment({"Enable Chat Listener"})
        public boolean chatListener;

        @Config.Comment("Create a command (if empty, nothing happen)")
        @Config.RequiresWorldRestart
        public String commandName;

        @Config.Comment("Radius of the message sending to the players (packet)")
        public double messageRange;

        @Config.Comment("Can players see the bubbles through blocks or not ( useful for server rp as example )")
        public boolean canThroughBlocks;

        public Server() {
            chatListener = true;
            canThroughBlocks = true;
            commandName = "";
            messageRange = 50.0D;
        }
    }
}

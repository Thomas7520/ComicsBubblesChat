package com.thomas7520.bubbleschat;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class BubblesConfig {

    public static final String CATEGORY_GENERAL = "general";

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        Pair<Client,ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        Pair<Server,ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);

        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
        SERVER_SPEC = serverSpecPair.getRight();
        SERVER = serverSpecPair.getLeft();



    }

    public static class Client {


        public ForgeConfigSpec.ConfigValue<List<Integer>> colorOutline;
        public ForgeConfigSpec.ConfigValue<List<Integer>> colorInside;
        public ForgeConfigSpec.ConfigValue<List<Integer>> colorText;
        public ForgeConfigSpec.IntValue durationBubbles;
        public ForgeConfigSpec.IntValue maxBubblesStack;
        public ForgeConfigSpec.IntValue lineWidth;
        public ForgeConfigSpec.IntValue sizeBubble;
        public ForgeConfigSpec.BooleanValue enableBubbles;
        public ForgeConfigSpec.BooleanValue canThroughBlocks;
        public ForgeConfigSpec.BooleanValue forceFormatChat;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings").push(CATEGORY_GENERAL);

            colorOutline = builder.comment("Adjust the color of the bubbles outline | args = r,g,b,a").define("outlineColor", new ArrayList<>());
            colorInside = builder.comment("Adjust the color of the bubbles inside | args = r,g,b,a").define("insideColor", new ArrayList<>());
            colorText = builder.comment("Adjust the color of the bubbles text | args = r,g,b,a").define("textColor", new ArrayList<>());
            durationBubbles = builder.comment("Adjust duration time of bubbles").defineInRange("durationBubbles", 10, 0, 60);
            maxBubblesStack = builder.comment("Max Bubbles Stackable on a player's head").defineInRange("maxBubblesStack", 0, 0, Integer.MAX_VALUE);
            lineWidth = builder.comment("Max line width").defineInRange("lineWidth", 150, 50, Integer.MAX_VALUE);
            sizeBubble = builder.comment("Size of bubble").defineInRange("sizeBubble", 0, 0, 20);
            enableBubbles = builder.comment("Enable Bubbles Listener").define("bubblesListener", true);
            canThroughBlocks = builder.comment("See the bubbles through blocks or not ( like name )").define("throughBlock", true);
            forceFormatChat = builder.comment("Force Format chat is bubbles don't appear").define("forceFormatChat", false);

        }
    }

    public static class Server {

        public ForgeConfigSpec.BooleanValue chatListener;
        public ForgeConfigSpec.ConfigValue<String> commandName;
        public ForgeConfigSpec.ConfigValue<String> messageSuccess;
        public ForgeConfigSpec.DoubleValue bubbleRange;
        public ForgeConfigSpec.BooleanValue canThroughBlocks;


        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings").push(CATEGORY_GENERAL);

            chatListener = builder.comment("Enable Chat Listener").define("chatListener", true);
            commandName = builder.comment("Create a custom command (empty = no command)").define("commandName", "");
            messageSuccess = builder.comment("Message sent when player performed command (empty = no message) You can add color (ex: &bBubbles created").define("messageSuccess", "");
            bubbleRange = builder.comment("Radius of the bubble sending to the players (packet)").defineInRange("bubbleRange", 50.0, 0, Double.MAX_VALUE);
            canThroughBlocks = builder.comment("Can players see the bubbles through blocks or not ( useful for server rp as example )").define("throughBlock", true);
        }
    }
}

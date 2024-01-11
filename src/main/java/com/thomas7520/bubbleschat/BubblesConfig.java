package com.thomas7520.bubbleschat;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class BubblesConfig {

    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.ConfigValue<List<Integer>> colorOutline;
    public static ForgeConfigSpec.ConfigValue<List<Integer>> colorInside;
    public static ForgeConfigSpec.ConfigValue<List<Integer>> colorText;
    public static ForgeConfigSpec.IntValue durationBubbles;
    public static ForgeConfigSpec.IntValue maxBubblesStack;

    public static ForgeConfigSpec.BooleanValue chatListener;
    public static ForgeConfigSpec.ConfigValue<String> commandName;
    public static ForgeConfigSpec.DoubleValue bubbleRange;
    public static ForgeConfigSpec.BooleanValue canThroughBlocks;
    public static ForgeConfigSpec.IntValue lineWidth;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        colorOutline = CLIENT_BUILDER.comment("Adjust the color of the bubbles outline | args = r,g,b,a").define("outlineColor", new ArrayList<>());
        colorInside = CLIENT_BUILDER.comment("Adjust the color of the bubbles inside | args = r,g,b,a").define("insideColor", new ArrayList<>());
        colorText = CLIENT_BUILDER.comment("Adjust the color of the bubbles text | args = r,g,b,a").define("textColor", new ArrayList<>());
        durationBubbles = CLIENT_BUILDER.comment("Adjust duration time of bubbles").defineInRange("durationBubbles", 10, 0, 60);
        maxBubblesStack = CLIENT_BUILDER.comment("Max Bubbles Stackable on a player's head").defineInRange("maxBubblesStack", 0, 0, Integer.MAX_VALUE);
        lineWidth = CLIENT_BUILDER.comment("Max line width").defineInRange("lineWidth", 150, 50, Integer.MAX_VALUE);

        CLIENT_BUILDER.pop();

        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        chatListener = SERVER_BUILDER.comment("Enable Chat Listener").define("chatListener", true);
        commandName = SERVER_BUILDER.comment("Create a custom command (empty = no command)").define("commandName", "");
        bubbleRange = SERVER_BUILDER.comment("Radius of the bubble sending to the players (packet)").defineInRange("bubbleRange", 50.0, 0, Double.MAX_VALUE);
        canThroughBlocks = SERVER_BUILDER.comment("Can players see the bubbles through blocks or not ( useful for server rp as example )").define("throughBlock", true);
        SERVER_BUILDER.pop();


        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();


    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.ConfigReloading configEvent) {

    }


}

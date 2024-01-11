package com.thomas7520.bubbleschat.client;

import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.client.screen.ScreenConfig;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import com.thomas7520.bubbleschat.util.SpecColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BubblesClientEvent {


    @SubscribeEvent
    public static void bubblesRenderEvent(RenderLivingEvent.Pre<PlayerEntity, PlayerModel<PlayerEntity>> event) {
        UUID uuid = event.getEntity().getUUID();

        if(!ClientBubblesUtil.BUBBLES_SYNC.containsKey(uuid)) return;

        Bubble bubble = ClientBubblesUtil.BUBBLES_SYNC.get(uuid);

        long endTime = bubble.getMessages().getFirst().getStartTime() + TimeUnit.SECONDS.toMillis(BubblesConfig.CLIENT.durationBubbles.get());

        if(endTime < System.currentTimeMillis()) {
            ClientBubblesUtil.BUBBLES_SYNC.remove(event.getEntity().getUUID());
            return;
        }

        int k = 0;

        for (Message message : new ArrayDeque<>(bubble.getMessages())) {
            long endTime2 = message.getStartTime() + TimeUnit.SECONDS.toMillis(BubblesConfig.CLIENT.durationBubbles.get());
            if(endTime2 < System.currentTimeMillis()) {
                bubble.getMessages().remove(message);
                continue;
            }
            ClientBubblesUtil.draw(message, bubble.getMessages(), event, endTime2, event.getEntity().isDiscrete(), true, true, k == 0, k);
            k = k + 1;
        }


    }

    @SubscribeEvent
    public static void onPress(InputEvent.KeyInputEvent event) {
        KeyBinding[] keyBindings = ClientBubblesUtil.keyBindings;

        if (keyBindings[0].isDown())
        {
            Minecraft.getInstance().setScreen(new ScreenConfig());
        }
    }

    @SubscribeEvent
    public static void onServerQuit(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientBubblesUtil.serverSupport = false;
    }

    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        if(ClientBubblesUtil.serverSupport || !BubblesConfig.CLIENT.enableBubbles.get()) return;

        if(event.getType() == ChatType.GAME_INFO) return;

        if(event.getType() == ChatType.SYSTEM && !BubblesConfig.CLIENT.forceFormatChat.get()) return;

        System.out.println("before " + event.getMessage().getString());
        String message = StringUtils.stripColor(event.getMessage().getString());
        System.out.println("after " + message);

        System.out.println("message suffix " + getMessage(message, true));
        System.out.println("message prefix " + getMessage(message, false));
        assert Minecraft.getInstance().level != null;

        TreeMap<String,UUID> players = new TreeMap<>(
                Comparator.comparingInt(String::length)
                        .thenComparing(Function.identity())
                        .reversed());

        Minecraft.getInstance().level.players()
                .stream()
                .filter(player -> !player.getName().getString().isEmpty())
                .collect(Collectors.toList())
                .forEach(playerEntity -> players.put(playerEntity.getName().getString(), playerEntity.getUUID()));

        System.out.println(players.toString());

        for (String arg : getMessage(message, false).split(" ")) {
            assert Minecraft.getInstance().level != null;

            System.out.println(arg  + " - ");
            if(players.containsKey(arg)) {
                addBubbles(getMessage(message, true), players.get(arg));
                return;
            }
        }

        for (Map.Entry<String, UUID> entry : players.entrySet()) {
            String patternFormat = ".*"+entry.getKey()+"*.";
            Pattern pattern = Pattern.compile(patternFormat);


            System.out.println(pattern.matcher(message).find() + " first");
            System.out.println(pattern.matcher(getMessage(message, false)).find() + " second");
            if(pattern.matcher(message).find()) {
                String text = getMessage(message, true);
                if(!text.isEmpty()) {
                    addBubbles(getMessage(message, true), entry.getValue());
                    return;
                }
            }
        }

        if(!BubblesConfig.CLIENT.forceFormatChat.get() || event.getType() == ChatType.SYSTEM) return;
        String[] messageFormat = message
                .replace("<", "")
                .replace(">", "")
                .split(" ", 2);

        if(messageFormat.length < 2) return;

        String name = messageFormat[0];
        String text = messageFormat[1];

        if(players.containsKey(name)) {
            addBubbles(text, players.get(name));
        }
    }


    public static String getMessage(String text, boolean suffix) {
        String[] formats = {":", ">", ">>", "»", "›" , "U+27A2", "⇝"};

        for (String format : formats) {
            if(text.split(format).length <= 1) continue;
            return suffix ? text.split(format)[1].trim() : text.split(format)[0].trim();
        }

        return "";
    }

    public static void addBubbles(String text, UUID uuid) {
        if(text.isEmpty()) return;

        long startTime = System.currentTimeMillis();

        Bubble bubble = ClientBubblesUtil.BUBBLES_SYNC.get(uuid);
        if(bubble == null) {
            bubble = new Bubble(startTime, text);
            ClientBubblesUtil.BUBBLES_SYNC.put(uuid, bubble);
        } else {
            if(bubble.getMessages().size() == BubblesConfig.CLIENT.maxBubblesStack.get()) bubble.getMessages().removeLast();
            bubble.getMessages().addFirst(new Message(startTime, text, new SpecColor(BubblesConfig.CLIENT.colorOutline.get())
                    , new SpecColor(BubblesConfig.CLIENT.colorInside.get()), new SpecColor(BubblesConfig.CLIENT.colorText.get())));
        }
    }
}

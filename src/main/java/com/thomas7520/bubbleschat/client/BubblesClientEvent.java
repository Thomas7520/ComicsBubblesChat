package com.thomas7520.bubbleschat.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.client.screen.ScreenConfig;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BubblesClientEvent {


    @SubscribeEvent
    public static void bubblesRenderEvent(RenderLivingEvent.Pre<PlayerEntity, PlayerModel<PlayerEntity>> event) {
        UUID uuid = event.getEntity().getUniqueID();

        if(!ClientBubblesUtil.BUBBLES_SYNC.containsKey(uuid)) return;

        Bubble bubble = ClientBubblesUtil.BUBBLES_SYNC.get(uuid);

        long endTime = bubble.getMessages().getFirst().getStartTime() + TimeUnit.SECONDS.toMillis(BubblesConfig.durationBubbles.get());

        if(endTime < System.currentTimeMillis()) {
            ClientBubblesUtil.BUBBLES_SYNC.remove(event.getEntity().getUniqueID());
            return;
        }

        Entity entity = event.getEntity();
        float offsetX = (float) entity.getPosX();
        float offsetY = (float) entity.getPosY() + 2.5f;
        float offsetZ = (float) entity.getPosZ();

        int k = 0;

        for (Message message : new ArrayDeque<>(bubble.getMessages())) {
            long endTime2 = message.getStartTime() + TimeUnit.SECONDS.toMillis(BubblesConfig.durationBubbles.get());
            if(endTime2 < System.currentTimeMillis()) {
                bubble.getMessages().remove(message);
                continue;
            }
            ClientBubblesUtil.draw(message, bubble.getMessages(), event, endTime2, offsetX, offsetY + 0.2f, offsetZ, event.getEntity().isSneaking(), true, true, k == 0, k);
            k = k + 1;
        }


    }

    @SubscribeEvent
    public static void onPress(InputEvent.KeyInputEvent event) {
        KeyBinding[] keyBindings = ClientBubblesUtil.keyBindings;

        if (keyBindings[0].isPressed())
        {
            Minecraft.getInstance().displayGuiScreen(new ScreenConfig());
        }
    }

    @SubscribeEvent
    public static void onServerQuit(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientBubblesUtil.serverSupport = false;
    }
}

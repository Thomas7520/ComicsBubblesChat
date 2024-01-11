package com.thomas7520.bubbleschat.client;

import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.gui.GuiConfig;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = ComicsBubblesChat.MODID, value = Side.CLIENT)
public class BubblesClientEvent {


    @SubscribeEvent
    public static void bubblesRenderEvent(RenderLivingEvent.Pre<EntityLivingBase> event) {
        UUID uuid = event.getEntity().getUniqueID();

        if(!ClientBubblesUtil.BUBBLES_SYNC.containsKey(uuid)) return;

        Bubble bubble = ClientBubblesUtil.BUBBLES_SYNC.get(uuid);

        long endTime = bubble.getMessages().getFirst().getStartTime() + TimeUnit.SECONDS.toMillis(BubblesConfig.client.durationBubbles);

        if(endTime < System.currentTimeMillis()) {
            ClientBubblesUtil.BUBBLES_SYNC.remove(event.getEntity().getUniqueID());
            return;
        }

        float offsetX = (float) event.getX();
        float offsetY = (float) event.getY() + 2.5f;
        float offsetZ = (float) event.getZ();

        int k = 0;



        for (Message message : new ArrayDeque<>(bubble.getMessages())) {
            long endTime2 = message.getStartTime() + TimeUnit.SECONDS.toMillis(BubblesConfig.client.durationBubbles);
            if(endTime2 < System.currentTimeMillis()) {
                bubble.getMessages().remove(message);
                continue;
            }

            ClientBubblesUtil.draw(message.getMessage(), bubble.getMessages(), endTime2, offsetX, offsetY + 0.2f, offsetZ, event.getEntity().isSneaking(), true, true, k == 0, k);
            k = k + 1;
        }


    }

    @SubscribeEvent
    public static void onPress(InputEvent.KeyInputEvent event) {
        KeyBinding[] keyBindings = ClientBubblesUtil.keyBindings;

        if (keyBindings[0].isPressed())
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiConfig());
        }

//        if (keyBindings[1].isPressed())
//        {
//
//        }
    }

    @SubscribeEvent
    public static void onServerQuit(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        ClientBubblesUtil.serverSupport = false;
    }
}

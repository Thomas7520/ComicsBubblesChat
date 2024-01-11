package com.thomas7520.bubbleschat.client;

import com.thomas7520.bubbleschat.ComicsBubblesChat;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ComicsBubblesChat.MODID, value = Dist.CLIENT)
public class Keybindings {

    @SubscribeEvent
    public static void registerKeyEvent(final RegisterKeyMappingsEvent event) {
        event.register(ClientBubblesUtil.keyBindings[0] = new KeyMapping("key.openoptions.desc" , GLFW.GLFW_KEY_B, "key.comicsbubbleschat.category"));
    }
}

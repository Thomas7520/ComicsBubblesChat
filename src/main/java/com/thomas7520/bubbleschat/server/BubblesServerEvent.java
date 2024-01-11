package com.thomas7520.bubbleschat.server;


import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.packet.SCSendModPresent;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod.EventBusSubscriber(modid = ComicsBubblesChat.MODID)
public class BubblesServerEvent {


    @SubscribeEvent
    public static void onSeverChatEvent(ServerChatEvent event) {
      if(!BubblesConfig.server.chatListener) return;

        EntityPlayer player = event.getPlayer();
        long startTime = System.currentTimeMillis();
        String text = event.getMessage();

        ComicsBubblesChat.networkWrapper.sendToAllAround(new SCSyncBubbleMessage(startTime, text, player.getUniqueID().toString())
                , new NetworkRegistry.TargetPoint(event.getPlayer().dimension, player.posX, player.posY, player.posZ, BubblesConfig.server.messageRange));
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ComicsBubblesChat.networkWrapper.sendTo(new SCSendModPresent(BubblesConfig.server.canThroughBlocks), (EntityPlayerMP) event.player);
    }
}

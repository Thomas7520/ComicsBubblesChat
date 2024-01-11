package com.thomas7520.bubbleschat.server;


import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.packet.SCSendModPresent;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BubblesServerEvent {


    @SubscribeEvent
    public static void onSeverChatEvent(ServerChatEvent event) {

      if(!BubblesConfig.SERVER.chatListener.get()) return;

        PlayerEntity player = event.getPlayer();
        long startTime = System.currentTimeMillis();
        String message = event.getMessage();

        double distance = BubblesConfig.SERVER.bubbleRange.get();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        ComicsBubblesChat.channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, distance, player.getCommandSenderWorld().dimension()))
                , new SCSyncBubbleMessage(startTime, message, player.getUUID()));
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ComicsBubblesChat.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer())
                , new SCSendModPresent(BubblesConfig.SERVER.canThroughBlocks.get()) );
    }
}

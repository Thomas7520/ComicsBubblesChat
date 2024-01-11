package com.thomas7520.bubbleschat.server;


import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.packet.SCSendModPresent;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class BubblesServerEvent {


    @SubscribeEvent
    public static void onSeverChatEvent(ServerChatEvent event) {

        if(!BubblesConfig.SERVER.chatListener.get()) return;

        Player player = event.getPlayer();
        long startTime = System.currentTimeMillis();
        String message = event.getMessage().getString();

        double distance = BubblesConfig.SERVER.bubbleRange.get();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, distance, player.getCommandSenderWorld().dimension()).get())
                .send(new SCSyncBubbleMessage(startTime, message, player.getUUID()));
        }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PacketDistributor.PLAYER.with((ServerPlayer) event.getEntity())
                .send(new SCSendModPresent(BubblesConfig.SERVER.canThroughBlocks.get()));
    }
}

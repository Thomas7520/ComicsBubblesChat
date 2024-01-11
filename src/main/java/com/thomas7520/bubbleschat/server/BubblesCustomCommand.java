package com.thomas7520.bubbleschat.server;

import com.mojang.brigadier.CommandDispatcher;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class BubblesCustomCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal(BubblesConfig.commandName.get()).requires((p_198627_0_) -> p_198627_0_.hasPermissionLevel(0)).then(Commands.argument("message", MessageArgument.message()).executes((p_198626_0_) -> {
            ITextComponent itextcomponent = MessageArgument.getMessage(p_198626_0_, "message");
            createBubble(p_198626_0_.getSource().asPlayer(), itextcomponent.getString());
            return 1;
        })));
    }


    public static int createBubble(PlayerEntity player, String message) {


        long startTime = System.currentTimeMillis();

        double distance = BubblesConfig.bubbleRange.get();
        double x = player.getPosition().getX();
        double y = player.getPosition().getY();
        double z = player.getPosition().getZ();

        ComicsBubblesChat.channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, distance, player.dimension))
                , new SCSyncBubbleMessage(startTime, message, player.getUniqueID()));

        player.sendMessage(new KeybindTextComponent("§cBubble created !"));

        return 0;
    }
}

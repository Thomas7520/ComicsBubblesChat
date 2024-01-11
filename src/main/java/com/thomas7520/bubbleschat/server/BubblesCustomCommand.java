package com.thomas7520.bubbleschat.server;

import com.mojang.brigadier.CommandDispatcher;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.UUID;

public class BubblesCustomCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(BubblesConfig.SERVER.commandName.get()).requires((p_198627_0_) -> p_198627_0_.hasPermission(0)).then(Commands.argument("message", MessageArgument.message()).executes((p_198626_0_) -> {
            Component textComponent = MessageArgument.getMessage(p_198626_0_, "message");
            createBubble(p_198626_0_.getSource(), textComponent.getString());
            return 1;
        })));
    }


    public static int createBubble(CommandSourceStack source, String message) {
        if(source.getEntity() == null || source.getEntity() == null) {
            source.sendFailure(new TranslatableComponent("text.mustbeplayer"));
            return 0;
        }

        Player player = (Player) source.getEntity();
        long startTime = System.currentTimeMillis();

        double distance = BubblesConfig.SERVER.bubbleRange.get();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        ComicsBubblesChat.channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, distance, player.getCommandSenderWorld().dimension()))
                , new SCSyncBubbleMessage(startTime, message, player.getUUID()));

        player.sendMessage(new TranslatableComponent("§cBubble created !"), UUID.randomUUID());

        return 0;
    }
}

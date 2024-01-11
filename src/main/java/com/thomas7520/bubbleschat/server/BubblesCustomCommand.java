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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class BubblesCustomCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal(BubblesConfig.SERVER.commandName.get()).then(Commands.argument("message", MessageArgument.message()).executes((p_198626_0_) -> {
            ITextComponent itextcomponent = MessageArgument.getMessage(p_198626_0_, "message");
            createBubble(p_198626_0_.getSource(), itextcomponent.getString());
            return 1;
        })));
    }


    public static int createBubble(CommandSource source, String message) {
        if(source.getEntity() == null || !(source.getEntity() instanceof PlayerEntity)) {
            source.sendFailure(new TranslationTextComponent("text.mustbeplayer"));
            return 0;
        }

        PlayerEntity player = (PlayerEntity) source.getEntity();
        long startTime = System.currentTimeMillis();

        double distance = BubblesConfig.SERVER.bubbleRange.get();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        ComicsBubblesChat.channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, distance, player.getCommandSenderWorld().dimension()))
                , new SCSyncBubbleMessage(startTime, message, player.getUUID()));

        player.sendMessage(new KeybindTextComponent("§cBubble created !"), UUID.randomUUID());

        return 0;
    }
}

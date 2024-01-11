package com.thomas7520.bubbleschat.server;

import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class BubblesCustomCommand extends CommandBase {

    @Override
    public String getName() {
        return BubblesConfig.server.commandName;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName() + " <message>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString("Only executable from a player"));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;

        if(args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }


        StringBuilder text = new StringBuilder();
        for (int i = 0; i != args.length; i++)
            text.append(args[i]).append(" ");

        long startTime = System.currentTimeMillis();

        ComicsBubblesChat.networkWrapper.sendToAllAround(new SCSyncBubbleMessage(startTime, text.substring(0, text.length() - 1), player.getUniqueID().toString())
                , new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, BubblesConfig.server.messageRange));

        player.sendMessage(new TextComponentString("§cBubble created !"));

    }


    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}

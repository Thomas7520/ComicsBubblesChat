package com.thomas7520.bubbleschat;

import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.packet.SCSendModPresent;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import com.thomas7520.bubbleschat.server.BubblesCustomCommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = ComicsBubblesChat.MODID, name = ComicsBubblesChat.NAME, version = ComicsBubblesChat.VERSION)
public class ComicsBubblesChat
{
    public static final String MODID = "comicsbubbleschat";
    public static final String NAME = "Comics Bubbles Chat Mod";
    public static final String VERSION = "1.0";

    private static Logger logger;

    public static SimpleNetworkWrapper networkWrapper;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        if(event.getSide().isClient()) ClientBubblesUtil.registerBindings();

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("comicsbubbleschat");
        networkWrapper.registerMessage(SCSyncBubbleMessage.Handler.class, SCSyncBubbleMessage.class, 0, Side.CLIENT);
        networkWrapper.registerMessage(SCSendModPresent.Handler.class, SCSendModPresent.class, 1, Side.CLIENT);

    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        if(BubblesConfig.server.commandName.isEmpty()) return;
        event.registerServerCommand(new BubblesCustomCommand());
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        logger.info("---------------------");
        if(!BubblesConfig.server.commandName.isEmpty())
            logger.info("Server registered /" + BubblesConfig.server.commandName + " as command for comics bubbles chat");
        logger.info("Server " + (BubblesConfig.server.chatListener ? "enable" : "disable") + " chat listener for comics bubbles chat");
        logger.info("Server " + (BubblesConfig.server.canThroughBlocks ? "enable" : "disable") + " bubbles through blocks for comics bubbles chat");
        logger.info("Server set range for comics bubbles chat packet on " + BubblesConfig.server.messageRange);
        logger.info("---------------------");
    }
}

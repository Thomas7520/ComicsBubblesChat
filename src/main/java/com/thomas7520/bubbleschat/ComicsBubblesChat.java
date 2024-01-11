package com.thomas7520.bubbleschat;


import com.mojang.brigadier.CommandDispatcher;
import com.thomas7520.bubbleschat.packet.SCSendModPresent;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import com.thomas7520.bubbleschat.server.BubblesCustomCommand;
import com.thomas7520.bubbleschat.util.ResetUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.MessageFunctions;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ComicsBubblesChat.MODID)
public class ComicsBubblesChat
{
    public static final String MODID = "comicsbubbleschat";

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID)
            , () -> PROTOCOL_VERSION
            , s -> true
            , s -> true);

    public ComicsBubblesChat(IEventBus modEventBus) {
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::setupClient);

        NeoForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BubblesConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BubblesConfig.SERVER_SPEC);
    }


    private void setup(final FMLCommonSetupEvent event) {
        int index = 0;

        channel.messageBuilder(SCSendModPresent.class, index++).encoder(SCSendModPresent::encode).decoder(SCSendModPresent::new).consumerMainThread(SCSendModPresent::handle).add();
        channel.messageBuilder(SCSyncBubbleMessage.class, index).encoder(SCSyncBubbleMessage::encode).decoder(SCSyncBubbleMessage::new).consumerMainThread(SCSyncBubbleMessage::handle).add();

    }



    private void setupClient(final FMLClientSetupEvent event) {
        if(BubblesConfig.CLIENT.colorOutline.get().isEmpty() || BubblesConfig.CLIENT.colorInside.get().isEmpty() || BubblesConfig.CLIENT.colorText.get().isEmpty()) ResetUtil.resetColors();
    }



    CommandDispatcher<CommandSourceStack> commandDispatcher;

    @SubscribeEvent
    public void onRegisterCommand(RegisterCommandsEvent event) {
        commandDispatcher = event.getDispatcher();
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        LOGGER.info("---------------------");
        if(!BubblesConfig.SERVER.commandName.get().isEmpty()) {
            BubblesCustomCommand.register(commandDispatcher);
            LOGGER.info("Server registered /" + BubblesConfig.SERVER.commandName.get() + " as command for comics bubbles chat");
            if(!BubblesConfig.SERVER.messageSuccess.get().isEmpty())
                LOGGER.info("Command /" + BubblesConfig.SERVER.commandName.get() + " return \"" + BubblesCustomCommand.translateColorCodes(BubblesConfig.SERVER.messageSuccess.get()) +"\" when performed");
        }
        LOGGER.info("Server " + (BubblesConfig.SERVER.chatListener.get() ? "enable" : "disable") + " chat listener for comics bubbles chat");
        LOGGER.info("Server " + (BubblesConfig.SERVER.canThroughBlocks.get() ? "enable" : "disable") + " bubbles through blocks for comics bubbles chat");
        LOGGER.info("Server set range for comics bubbles chat packet on " + BubblesConfig.SERVER.bubbleRange.get());
        LOGGER.info("Your server config file is on your directory of your world !");
        LOGGER.info("---------------------");
    }
}


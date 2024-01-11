package com.thomas7520.bubbleschat;


import com.mojang.brigadier.CommandDispatcher;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.packet.SCSendModPresent;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import com.thomas7520.bubbleschat.server.BubblesCustomCommand;
import com.thomas7520.bubbleschat.util.ResetUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
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
            , PROTOCOL_VERSION::equals
            , PROTOCOL_VERSION::equals);

    public ComicsBubblesChat() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BubblesConfig.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BubblesConfig.SERVER_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

        MinecraftForge.EVENT_BUS.register(this);
    }


    private void setup(final FMLCommonSetupEvent event) {
        int index = 0;

        channel.registerMessage(index++, SCSendModPresent.class, SCSendModPresent::encode, SCSendModPresent::decode, SCSendModPresent::handle);
        channel.registerMessage(index, SCSyncBubbleMessage.class, SCSyncBubbleMessage::encode, SCSyncBubbleMessage::decode, SCSyncBubbleMessage::handle);

    }

    private void setupClient(final FMLClientSetupEvent event) {
        ClientBubblesUtil.registerBindings();
        if(BubblesConfig.colorOutline.get().isEmpty() || BubblesConfig.colorInside.get().isEmpty() || BubblesConfig.colorText.get().isEmpty()) ResetUtil.resetColors();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> commandDispatcher = event.getCommandDispatcher();
        if(!BubblesConfig.commandName.get().isEmpty()) BubblesCustomCommand.register(commandDispatcher);
    }
    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        LOGGER.info("---------------------");
        if(!BubblesConfig.commandName.get().isEmpty())
            LOGGER.info("Server registered /" + BubblesConfig.commandName.get() + " as command for comics bubbles chat");
        LOGGER.info("Server " + (BubblesConfig.chatListener.get() ? "enable" : "disable") + " chat listener for comics bubbles chat");
        LOGGER.info("Server " + (BubblesConfig.canThroughBlocks.get() ? "enable" : "disable") + " bubbles through blocks for comics bubbles chat");
        LOGGER.info("Server set range for comics bubbles chat packet on " + BubblesConfig.bubbleRange.get());
        LOGGER.info("You're server config file is on your directory of your world !");
        LOGGER.info("---------------------");
    }
}

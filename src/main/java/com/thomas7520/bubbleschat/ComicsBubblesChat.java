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
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ComicsBubblesChat.MODID)
@Mod.EventBusSubscriber(modid=ComicsBubblesChat.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
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
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommand);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BubblesConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BubblesConfig.SERVER_SPEC);
    }


    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        int index = 0;

        channel.registerMessage(index++, SCSendModPresent.class, SCSendModPresent::encode, SCSendModPresent::decode, SCSendModPresent::handle);
        channel.registerMessage(index, SCSyncBubbleMessage.class, SCSyncBubbleMessage::encode, SCSyncBubbleMessage::decode, SCSyncBubbleMessage::handle);

    }

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        ClientBubblesUtil.registerBindings();
        if(BubblesConfig.CLIENT.colorOutline.get().isEmpty() || BubblesConfig.CLIENT.colorInside.get().isEmpty() || BubblesConfig.CLIENT.colorText.get().isEmpty()) ResetUtil.resetColors();
    }

    CommandDispatcher<CommandSource> commandDispatcher;

    private void onRegisterCommand(RegisterCommandsEvent event) {
        commandDispatcher = event.getDispatcher();
    }

    private void onServerStarted(FMLServerStartedEvent event) {
        LOGGER.info("---------------------");
        if(!BubblesConfig.SERVER.commandName.get().isEmpty()) {
            BubblesCustomCommand.register(commandDispatcher);
            LOGGER.info("Server registered /" + BubblesConfig.SERVER.commandName.get() + " as command for comics bubbles chat");
        }
        LOGGER.info("Server " + (BubblesConfig.SERVER.chatListener.get() ? "enable" : "disable") + " chat listener for comics bubbles chat");
        LOGGER.info("Server " + (BubblesConfig.SERVER.canThroughBlocks.get() ? "enable" : "disable") + " bubbles through blocks for comics bubbles chat");
        LOGGER.info("Server set range for comics bubbles chat packet on " + BubblesConfig.SERVER.bubbleRange.get());
        LOGGER.info("You'r server config file is on your directory of your world !");
        LOGGER.info("---------------------");    }
}

package com.thomas7520.bubbleschat;


import com.mojang.brigadier.CommandDispatcher;
import com.thomas7520.bubbleschat.client.BubblesClientEvent;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.packet.SCSendModPresent;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import com.thomas7520.bubbleschat.server.BubblesCustomCommand;
import com.thomas7520.bubbleschat.util.ResetUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.utils.Colors;
import org.lwjgl.glfw.GLFW;

@Mod(ComicsBubblesChat.MODID)
@Mod.EventBusSubscriber(modid=ComicsBubblesChat.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ComicsBubblesChat
{
    public static final String MODID = "comicsbubbleschat";

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID)
            , () -> PROTOCOL_VERSION
            , s -> true
            , s -> true);

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
        if(BubblesConfig.CLIENT.colorOutline.get().isEmpty() || BubblesConfig.CLIENT.colorInside.get().isEmpty() || BubblesConfig.CLIENT.colorText.get().isEmpty()) ResetUtil.resetColors();
    }



    CommandDispatcher<CommandSourceStack> commandDispatcher;

    private void onRegisterCommand(RegisterCommandsEvent event) {
        commandDispatcher = event.getDispatcher();
    }

    private void onServerStarted(ServerStartedEvent event) {
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


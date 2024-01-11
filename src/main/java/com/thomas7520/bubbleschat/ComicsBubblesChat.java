package com.thomas7520.bubbleschat;


import com.mojang.brigadier.CommandDispatcher;
import com.thomas7520.bubbleschat.packet.SCSendModPresent;
import com.thomas7520.bubbleschat.packet.SCSyncBubbleMessage;
import com.thomas7520.bubbleschat.server.BubblesCustomCommand;
import com.thomas7520.bubbleschat.util.ResetUtil;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ComicsBubblesChat.MODID)
public class ComicsBubblesChat
{
    public static final String MODID = "comicsbubbleschat";

    private static final Logger LOGGER = LogManager.getLogger();



    public ComicsBubblesChat(IEventBus modEventBus) {
        modEventBus.addListener(this::setupClient);
        modEventBus.addListener(this::registerPackets);

        NeoForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BubblesConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BubblesConfig.SERVER_SPEC);
    }

    private void registerPackets(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(MODID);

        registrar.play(SCSendModPresent.ID, SCSendModPresent::new, play -> play.client((packet, ctx) -> ctx.workHandler().submitAsync(() -> packet.handle(ctx))));
        registrar.play(SCSyncBubbleMessage.ID, SCSyncBubbleMessage::new, play -> play.client((packet, ctx) -> ctx.workHandler().submitAsync(() -> packet.handle(ctx))));
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


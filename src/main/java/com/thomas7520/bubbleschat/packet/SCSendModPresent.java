package com.thomas7520.bubbleschat.packet;

import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SCSendModPresent implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ComicsBubblesChat.MODID, "mod_present");

    public boolean throughBlocks;

    public SCSendModPresent(boolean canThroughBlocks) {
        this.throughBlocks = canThroughBlocks;
    }

    public SCSendModPresent() {

    }

    public SCSendModPresent (FriendlyByteBuf buf) {
        throughBlocks = buf.readBoolean();
    }





    public void handle(PlayPayloadContext ctx)
    {
        handleClient(throughBlocks);
    }

    public static void handleClient(boolean throughBlocks) {
        ClientBubblesUtil.serverSupport = true;
        ClientBubblesUtil.bubbleThroughBlocks = throughBlocks;
    }


    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(throughBlocks);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}

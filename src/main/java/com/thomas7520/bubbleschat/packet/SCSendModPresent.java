package com.thomas7520.bubbleschat.packet;

import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SCSendModPresent {

    public boolean throughBlocks;

    public SCSendModPresent(boolean canThroughBlocks) {
        this.throughBlocks = canThroughBlocks;
    }

    public SCSendModPresent() {

    }

    public SCSendModPresent (FriendlyByteBuf buf) {
        throughBlocks = buf.readBoolean();
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(throughBlocks);
    }




    public void handle(NetworkEvent.Context ctx)
    {
        ctx.enqueueWork(() -> handleClient(throughBlocks));

        ctx.setPacketHandled(true);
    }

    public static void handleClient(boolean throughBlocks) {
        ClientBubblesUtil.serverSupport = true;
        ClientBubblesUtil.bubbleThroughBlocks = throughBlocks;
    }



}

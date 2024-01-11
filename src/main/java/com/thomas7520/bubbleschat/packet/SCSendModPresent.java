package com.thomas7520.bubbleschat.packet;

import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SCSendModPresent {

    public boolean throughBlocks;

    public SCSendModPresent(boolean canThroughBlocks) {
        this.throughBlocks = canThroughBlocks;
    }

    public SCSendModPresent() {

    }

    public static void encode(SCSendModPresent packet, PacketBuffer buf) {
        buf.writeBoolean(packet.throughBlocks);
    }

    public static SCSendModPresent decode(PacketBuffer buf) {
        SCSendModPresent message = new SCSendModPresent();
        message.throughBlocks = buf.readBoolean();

        return message;
    }


    public static void handle(SCSendModPresent packet, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> handleClient(packet));

        ctx.get().setPacketHandled(true);
    }

    public static void handleClient(SCSendModPresent packet) {
        ClientBubblesUtil.serverSupport = true;
        ClientBubblesUtil.bubbleThroughBlocks = packet.throughBlocks;
    }
}

package com.thomas7520.bubbleschat.packet;

import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import com.thomas7520.bubbleschat.util.SpecColor;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SCSyncBubbleMessage {


    private long startTime;
    private String text;
    private UUID uuid;

    public SCSyncBubbleMessage(long startTime, String text, UUID uuid) {
        this.startTime = startTime;
        this.text = text;
        this.uuid = uuid;
    }

    public SCSyncBubbleMessage() {
    }


    public static void encode(SCSyncBubbleMessage message, PacketBuffer buf) {
        buf.writeLong(message.startTime);
        buf.writeUtf(message.text);
        buf.writeUUID(message.uuid);
    }

    public static SCSyncBubbleMessage decode(PacketBuffer buf) {
        SCSyncBubbleMessage message = new SCSyncBubbleMessage();

        message.startTime= buf.readLong();
        message.text = buf.readUtf(32767);
        message.uuid = buf.readUUID();
        return message;
    }




    public static void handle(SCSyncBubbleMessage packet, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> handleClient(packet));

        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(SCSyncBubbleMessage packet) {
        Bubble bubble = ClientBubblesUtil.BUBBLES_SYNC.get(packet.uuid);
        if(bubble == null) {
            bubble = new Bubble(packet.startTime, packet.text);
            ClientBubblesUtil.BUBBLES_SYNC.put(packet.uuid, bubble);
        } else {
            if(bubble.getMessages().size() == BubblesConfig.CLIENT.maxBubblesStack.get()) bubble.getMessages().removeLast();
            bubble.getMessages().addFirst(new Message(packet.startTime, packet.text, new SpecColor(BubblesConfig.CLIENT.colorOutline.get())
            , new SpecColor(BubblesConfig.CLIENT.colorInside.get()), new SpecColor(BubblesConfig.CLIENT.colorText.get())));
        }
    }
}
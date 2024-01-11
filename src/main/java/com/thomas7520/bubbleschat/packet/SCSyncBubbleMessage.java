package com.thomas7520.bubbleschat.packet;

import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import com.thomas7520.bubbleschat.util.SpecColor;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.NetworkEvent;


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

    public SCSyncBubbleMessage(FriendlyByteBuf buf) {
        startTime = buf.readLong();
        text = buf.readUtf(32767);
        uuid = buf.readUUID();
    }


    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(startTime);
        buf.writeUtf(text);
        buf.writeUUID(uuid);
    }





    public void handle(NetworkEvent.Context ctx)
    {
        ctx.enqueueWork(() -> handleClient(uuid, text, startTime));

        ctx.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient(UUID uuid, String text, long startTime) {
        Bubble bubble = ClientBubblesUtil.BUBBLES_SYNC.get(uuid);
        if(bubble == null) {
            bubble = new Bubble(startTime, text);
            ClientBubblesUtil.BUBBLES_SYNC.put(uuid, bubble);
        } else {
            if(bubble.getMessages().size() == BubblesConfig.CLIENT.maxBubblesStack.get()) bubble.getMessages().removeLast();
            bubble.getMessages().addFirst(new Message(startTime, text, new SpecColor(BubblesConfig.CLIENT.colorOutline.get())
                    , new SpecColor(BubblesConfig.CLIENT.colorInside.get()), new SpecColor(BubblesConfig.CLIENT.colorText.get())));
        }
    }
}
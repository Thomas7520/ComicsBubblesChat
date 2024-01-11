package com.thomas7520.bubbleschat.packet;

import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class SCSyncBubbleMessage implements IMessage {


    private long startTime;
    private String text;
    private String uuid;

    public SCSyncBubbleMessage(long startTime, String text, String uuid) {
        this.startTime = startTime;
        this.text = text;
        this.uuid = uuid;
    }

    public SCSyncBubbleMessage() {
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        startTime = buf.readLong();
        text = ByteBufUtils.readUTF8String(buf);
        uuid = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(startTime);
        ByteBufUtils.writeUTF8String(buf, text);
        ByteBufUtils.writeUTF8String(buf, uuid);
    }

    public static class Handler implements IMessageHandler<SCSyncBubbleMessage, IMessage> {
        @Override
        public IMessage onMessage(SCSyncBubbleMessage message, MessageContext ctx) {
            Bubble bubble = ClientBubblesUtil.BUBBLES_SYNC.get(UUID.fromString(message.uuid));

            if(bubble == null) {
                bubble = new Bubble(message.startTime, message.text);
                ClientBubblesUtil.BUBBLES_SYNC.put(UUID.fromString(message.uuid), bubble);
            } else {
                if(bubble.getMessages().size() == BubblesConfig.client.maxBubblesStack) bubble.getMessages().removeLast();
                bubble.getMessages().addFirst(new Message(message.startTime, message.text));
            }
            return null;
        }
    }
}
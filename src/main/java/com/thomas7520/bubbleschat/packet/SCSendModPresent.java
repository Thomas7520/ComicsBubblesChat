package com.thomas7520.bubbleschat.packet;

import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SCSendModPresent implements IMessage {

    public boolean throughBlocks;

    public SCSendModPresent(boolean canThroughBlocks) {
        this.throughBlocks = canThroughBlocks;
    }

    public SCSendModPresent() {

    }



    @Override
    public void fromBytes(ByteBuf buf) {
        throughBlocks = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(throughBlocks);
    }

    public static class Handler implements IMessageHandler<SCSendModPresent, IMessage> {
        @Override
        public IMessage onMessage(SCSendModPresent message, MessageContext ctx) {
            ClientBubblesUtil.serverSupport = true;
            ClientBubblesUtil.bubbleThroughBlocks = message.throughBlocks;
            return null;
        }
    }
}

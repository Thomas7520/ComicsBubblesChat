package com.thomas7520.bubbleschat.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class ClientBubblesUtil {

    public static KeyMapping[] keyBindings = new KeyMapping[1];


    public static final HashMap<UUID, Bubble> BUBBLES_SYNC = new HashMap<>();
    public static boolean serverSupport = false;
    public static boolean bubbleThroughBlocks;







    public static void draw(Message message, ArrayDeque<Message> messages, RenderLivingEvent<?, ?> event, long endTime, boolean isSneaking, boolean drawBubble, boolean drawText, boolean drawLittleBubbles, int bubbleQueue)
    {




        Color outlineColor = message.getColorOutline().getColor();
        Color insideColor = message.getColorInside().getColor();
        Color textColor = message.getColorText().getColor();


        boolean transparency = !event.getEntity().isDiscrete() && ((!serverSupport && BubblesConfig.CLIENT.canThroughBlocks.get()) || (serverSupport && bubbleThroughBlocks));

        Entity entityIn = event.getEntity();


        int colorText = textColor.getRGB();
        int colorOutline = outlineColor.getRGB();
        int colorInside = insideColor.getRGB();

        if(entityIn.isDiscrete()) {
            colorText = ((int) ((colorText >> 24 & 0xFF) * 0.30f) << 24) | (colorText & 0x00FFFFFF);
            colorOutline = ((int) ((colorOutline >> 24 & 0xFF) * 0.30f) << 24) | (colorOutline & 0x00FFFFFF);
            colorInside = ((int) ((colorInside >> 24 & 0xFF) * 0.30f) << 24) | (colorInside & 0x00FFFFFF);
        }

        long timeLeft = endTime - System.currentTimeMillis();

        if(timeLeft < 0) throw new IllegalStateException("timeleft cannot be < 0, contact author if you didn't use api");



        if(timeLeft < 3000) {
            message.getColorOutline().setAlpha((int) timeLeft * BubblesConfig.CLIENT.colorOutline.get().get(3) / 3000);
            message.getColorInside().setAlpha((int) timeLeft * BubblesConfig.CLIENT.colorInside.get().get(3) / 3000);
            message.getColorText().setAlpha((int) timeLeft * BubblesConfig.CLIENT.colorText.get().get(3) / 3000);
        }
        float size = -0.02f - BubblesConfig.CLIENT.sizeBubble.get() * 0.001f;

        PoseStack stack = event.getPoseStack();

        MultiBufferSource buffers = event.getMultiBufferSource();


        float f = entityIn.getBbHeight() + 0.8f + BubblesConfig.CLIENT.sizeBubble.get() * 0.01f;

        stack.pushPose();
        stack.translate(0.0D, f, 0.0D);
        stack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        stack.scale(size, size, size);



        Font renderer = Minecraft.getInstance().font;
        List<String> lines = renderer.getSplitter().splitLines(message.getMessage(), BubblesConfig.CLIENT.lineWidth.get(), Style.EMPTY).stream().map(FormattedText::getString).collect(Collectors.toList());

        int linesHeight = 10 * lines.size();

        int biggestLineWidth = 0;
        for (String line : lines) {
            int lineWidth = renderer.width(line);

            if(biggestLineWidth < lineWidth) {
                biggestLineWidth = lineWidth;
            }
        }

        float yTranslate = lines.size() + 15;
        int i2 = biggestLineWidth / 2;

        stack.translate(i2 + 20, -yTranslate, 0.0f);

        float scale = 1.0f;

        stack.scale(scale, scale, scale);
        stack.translate(0.0f,-linesHeight / 2f - 15, 0.0f);


        if(drawBubble) {

            int distanceBetween = -10;

            int j2 = 0;

            for (Message value : messages) {
                if (j2 == bubbleQueue + 1)
                    break;

                String message2 = value.getMessage();

                List<String> messageLines;

                messageLines = renderer.getSplitter().splitLines(message2, BubblesConfig.CLIENT.lineWidth.get(), Style.EMPTY).stream().map(FormattedText::getString).collect(Collectors.toList());

                if (j2 != bubbleQueue) {
                    distanceBetween = distanceBetween + ((messageLines.size() * 10 + 4));
                } else {
                    distanceBetween = distanceBetween - messageLines.size();
                }
                j2++;
            }

            stack.translate(0.0f, -distanceBetween, 0.0f);

            drawBubble(stack, i2, -linesHeight / 2, colorOutline, colorInside, transparency);

        }

        if(drawLittleBubbles) {
            drawLittleBubble(stack, i2, -linesHeight / 2, colorOutline, colorInside, transparency);
            drawMediumBubble(stack, i2, -linesHeight / 2, colorOutline, colorInside, transparency);
        }


        if(drawText) {
            stack.translate(0.0f, 0.0f, 0.1f);

            if((colorText >> 24 & 0xFF) > 3) {

                int lineY = -linesHeight / 2 + 3;
                for (String line : lines) {
                    renderer.drawInBatch(line, -renderer.width(line) / 2f, lineY, colorText, false, stack.last().pose(), buffers, transparency ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, event.getPackedLight());
                    lineY = lineY + renderer.lineHeight + 1;
                }
            }
        }

        RenderSystem.setShaderColor(1,1,1,1);

        stack.popPose();
    }


    public static void fill(PoseStack poseStack, int p_fill_0_, int p_fill_1_, int p_fill_2_, int p_fill_3_, int p_fill_4_, boolean transparency) {

        if(transparency)
            fill(poseStack, RenderType.guiGhostRecipeOverlay(), p_fill_0_, p_fill_1_, p_fill_2_, p_fill_3_, 0, p_fill_4_);

        fill(poseStack, RenderType.gui(), p_fill_0_, p_fill_1_, p_fill_2_, p_fill_3_, 0, p_fill_4_);

    }


    public static void drawBubble(PoseStack poseStack, int i, int size, int outlineColor, int insideColor, boolean transparency) {

        // top bar
        fill(poseStack, -i - 1, size + 1, i + 1,  size + 2 , outlineColor, transparency);

        // bottom bar
        fill(poseStack,-i - 1, - size + 4, i + 1,  - size + 5 , outlineColor, transparency);

        // corner top left
        fill(poseStack,-i - 1, size + 2, -i - 2 ,  size + 3 , outlineColor, transparency);

        // corner bottom left
        fill(poseStack,-i - 1, -size + 4, -i - 2 ,  -size + 3 , outlineColor, transparency);

        // left bar
        fill(poseStack,-i - 3, size + 3, -i  - 2 ,  -size + 3, outlineColor, transparency);

        // right bar
        fill(poseStack,i + 3, size + 3, i  +  2 ,  -size + 3, outlineColor, transparency);

        // corner bottom right
        fill(poseStack,i + 1, -size + 4, i + 2 ,  -size + 3 , outlineColor, transparency);

        // corner top right
        fill(poseStack,i + 1, size + 2, i + 2 , size + 3 , outlineColor, transparency);

        // fill middle

        fill(poseStack,-i - 1, size + 2, i + 1,  -size + 4 , insideColor, transparency);

        // fill left middle
        fill(poseStack,-i - 2, size + 3, -i - 1,  -size + 3 , insideColor, transparency);

        // fill right middle
        fill(poseStack,i + 2, size + 3, i + 1,  -size + 3 , insideColor, transparency);

    }

    public static void drawMediumBubble(PoseStack poseStack, int i, int size, int outlineColor, int insideColor, boolean transparency) {
        // top bar
        fill(poseStack,-i + 1, -size + 11, -i - 6, -size + 10, outlineColor, transparency);

        // corner top right
        fill(poseStack,-i + 2, -size + 12, -i + 1, -size + 11, outlineColor, transparency);

        // corner top buttom
        fill(poseStack,-i + 2, -size + 18, -i + 1, -size + 17, outlineColor, transparency);

        // corner top right
        fill(poseStack,-i -7, -size + 12, -i - 6, -size + 11, outlineColor, transparency);

        // corner top buttom
        fill(poseStack,-i - 7, -size + 18, -i - 6, -size + 17, outlineColor, transparency);

        // bottom bar
        fill(poseStack,-i + 1, -size + 19, -i - 6, -size + 18, outlineColor, transparency);

        // right bar
        fill(poseStack,-i + 2 , -size + 12, -i + 3 , -size + 17, outlineColor, transparency);

        // left bar
        fill(poseStack,-i - 7 , -size + 12, -i - 8, -size + 17, outlineColor, transparency);

        // fill middle
        fill(poseStack,-i + 1 , -size + 11, - i - 6, -size + 18, insideColor, transparency);

        // fill left
        fill(poseStack,-i - 7 , -size + 12, - i - 6, -size + 17, insideColor, transparency);

        // fill right
        fill(poseStack,-i + 1 , -size + 12, - i + 2, -size + 17, insideColor, transparency);
    }

    public static void drawLittleBubble(PoseStack poseStack, int i, int size, int outlineColor, int insideColor, boolean transparency) {
        // top bar
        fill(poseStack,-i + 1 - 11, -size + 11 + 17, -i - 6 - 9, -size + 10 + 17, outlineColor, transparency);

        // corner top right
        fill(poseStack,-i + 2 - 11, -size + 12+ 17, -i - 10, -size + 11 + 17, outlineColor, transparency);

        // corner right buttom
        fill(poseStack,-i + 2 - 11, -size + 18+ 16, -i - 10, -size + 17 + 16, outlineColor, transparency);

        // corner top left
        fill(poseStack,-i -7 - 8, -size + 12 + 17, -i - 6 - 10, -size + 11 + 17, outlineColor, transparency);

        // corner button left
        fill(poseStack,-i - 7 - 9, -size + 18 + 16, -i - 5 - 10, -size + 17 + 16, outlineColor, transparency);

        // bottom bar
        fill(poseStack,-i + 1 - 11, -size + 19 + 16, -i - 5 - 10, -size + 18 + 16, outlineColor, transparency);

        // right bar
        fill(poseStack,-i + 2 - 11, -size + 12+ 17, -i + 2 - 10 , -size + 17 + 16, outlineColor, transparency);

        // left bar
        fill(poseStack,-i - 6 - 11 , -size + 12+ 17, -i - 6 - 10, -size + 17 + 16, outlineColor, transparency);

        //fill middle
        fill(poseStack,-i + 1 - 11 , -size + 11+ 17, - i - 6 - 9, -size + 18 + 16, insideColor, transparency);

        // fill left
        fill(poseStack,-i - 7 - 9 , -size + 12+ 17, - i - 6 - 9, -size + 17 + 16, insideColor, transparency);

        // fill right
        fill(poseStack,-i + 1 - 11 , -size + 12+ 17, - i + 2 - 11, -size + 17 + 16, insideColor, transparency);

    }

    public static void fill(PoseStack pose, RenderType p_286711_, int p_286234_, int p_286444_, int p_286244_, int p_286411_, int p_286671_, int p_286599_) {

        Matrix4f matrix4f = pose.last().pose();
        if (p_286234_ < p_286244_) {
            int i = p_286234_;
            p_286234_ = p_286244_;
            p_286244_ = i;
        }

        if (p_286444_ < p_286411_) {
            int j = p_286444_;
            p_286444_ = p_286411_;
            p_286411_ = j;
        }

        float f3 = (float) FastColor.ARGB32.alpha(p_286599_) / 255.0F;
        float f = (float)FastColor.ARGB32.red(p_286599_) / 255.0F;
        float f1 = (float)FastColor.ARGB32.green(p_286599_) / 255.0F;
        float f2 = (float)FastColor.ARGB32.blue(p_286599_) / 255.0F;

        VertexConsumer vertexconsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(p_286711_);

        vertexconsumer.vertex(matrix4f, (float)p_286234_, (float)p_286444_, (float)p_286671_).color(f, f1, f2, f3).endVertex();
        vertexconsumer.vertex(matrix4f, (float)p_286234_, (float)p_286411_, (float)p_286671_).color(f, f1, f2, f3).endVertex();
        vertexconsumer.vertex(matrix4f, (float)p_286244_, (float)p_286411_, (float)p_286671_).color(f, f1, f2, f3).endVertex();
        vertexconsumer.vertex(matrix4f, (float)p_286244_, (float)p_286444_, (float)p_286671_).color(f, f1, f2, f3).endVertex();
        // deprecated and seems not useful
        //this.flushIfUnmanaged();
    }
}

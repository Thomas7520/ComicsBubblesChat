package com.thomas7520.bubbleschat.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

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




    public static void draw(Message message, ArrayDeque<Message> messages, RenderLivingEvent.Pre<Player, PlayerModel<Player>> event, long endTime, boolean isSneaking, boolean drawBubble, boolean drawText, boolean drawLittleBubbles, int bubbleQueue)
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

            drawBubble(i2, -linesHeight / 2, colorOutline, colorInside, stack, transparency);

        }

        if(drawLittleBubbles) {
            drawLittleBubble(i2, -linesHeight / 2, colorOutline, colorInside, stack, transparency);
            drawMediumBubble(i2, -linesHeight / 2, colorOutline, colorInside, stack, transparency);
        }



        if(drawText) {
            stack.translate(0.0f, 0.0f, 0.1f);

            if((colorText >> 24 & 0xFF) > 3) {

                int lineY = -linesHeight / 2 + 3;
                for (String line : lines) {
                    renderer.drawInBatch(line, -renderer.width(line) / 2f, lineY, colorText, false, stack.last().pose(), buffers, transparency, 0, event.getPackedLight());
                    lineY = lineY + renderer.lineHeight + 1;
                }
            }
        }


        RenderSystem.setShaderColor(1,1,1,1);
        stack.popPose();

    }

    public static void fill(PoseStack matrixStack, int p_fill_0_, int p_fill_1_, int p_fill_2_, int p_fill_3_, int p_fill_4_, boolean transparency) {

        matrixStack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);



        GuiComponent.fill(matrixStack, p_fill_0_, p_fill_1_, p_fill_2_, p_fill_3_, p_fill_4_);


        if(transparency) {
            RenderSystem.depthFunc(GL11.GL_GREATER);
            GuiComponent.fill(matrixStack, p_fill_0_, p_fill_1_, p_fill_2_, p_fill_3_, p_fill_4_);
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
        }

        RenderSystem.disableDepthTest();

        matrixStack.popPose();



    }


    public static void drawBubble(int i, int size, int outlineColor, int insideColor, PoseStack matrix, boolean transparency) {
        // top bar
        fill(matrix,-i - 1, size + 1, i + 1,  size + 2 , outlineColor, transparency);

        // bottom bar
        fill(matrix,-i - 1, - size + 4, i + 1,  - size + 5 , outlineColor, transparency);

        // corner top left
        fill(matrix,-i - 1, size + 2, -i - 2 ,  size + 3 , outlineColor, transparency);

        // corner bottom left
        fill(matrix,-i - 1, -size + 4, -i - 2 ,  -size + 3 , outlineColor, transparency);

        // left bar
        fill(matrix,-i - 3, size + 3, -i  - 2 ,  -size + 3, outlineColor, transparency);

        // right bar
        fill(matrix,i + 3, size + 3, i  +  2 ,  -size + 3, outlineColor, transparency);

        // corner bottom right
        fill(matrix,i + 1, -size + 4, i + 2 ,  -size + 3 , outlineColor, transparency);

        // corner top right
        fill(matrix,i + 1, size + 2, i + 2 , size + 3 , outlineColor, transparency);

        // fill middle

        fill(matrix,-i - 1, size + 2, i + 1,  -size + 4 , insideColor, transparency);

        // fill left middle
        fill(matrix,-i - 2, size + 3, -i - 1,  -size + 3 , insideColor, transparency);

        // fill right middle
        fill(matrix,i + 2, size + 3, i + 1,  -size + 3 , insideColor, transparency);

    }

    public static void drawMediumBubble(int i, int size, int outlineColor, int insideColor, PoseStack stack, boolean transparency) {
        // top bar
        fill(stack,-i + 1, -size + 11, -i - 6, -size + 10, outlineColor, transparency);

        // corner top right
        fill(stack,-i + 2, -size + 12, -i + 1, -size + 11, outlineColor, transparency);

        // corner top buttom
        fill(stack,-i + 2, -size + 18, -i + 1, -size + 17, outlineColor, transparency);

        // corner top right
        fill(stack,-i -7, -size + 12, -i - 6, -size + 11, outlineColor, transparency);

        // corner top buttom
        fill(stack,-i - 7, -size + 18, -i - 6, -size + 17, outlineColor, transparency);

        // bottom bar
        fill(stack,-i + 1, -size + 19, -i - 6, -size + 18, outlineColor, transparency);

        // right bar
        fill(stack,-i + 2 , -size + 12, -i + 3 , -size + 17, outlineColor, transparency);

        // left bar
        fill(stack,-i - 7 , -size + 12, -i - 8, -size + 17, outlineColor, transparency);

        // fill middle
        fill(stack,-i + 1 , -size + 11, - i - 6, -size + 18, insideColor, transparency);

        // fill left
        fill(stack,-i - 7 , -size + 12, - i - 6, -size + 17, insideColor, transparency);

        // fill right
        fill(stack,-i + 1 , -size + 12, - i + 2, -size + 17, insideColor, transparency);
    }

    public static void drawLittleBubble(int i, int size, int outlineColor, int insideColor, PoseStack stack, boolean transparency) {
        // top bar
        fill(stack,-i + 1 - 11, -size + 11 + 17, -i - 6 - 9, -size + 10 + 17, outlineColor, transparency);

        // corner top right
        fill(stack,-i + 2 - 11, -size + 12+ 17, -i - 10, -size + 11 + 17, outlineColor, transparency);

        // corner right buttom
        fill(stack,-i + 2 - 11, -size + 18+ 16, -i - 10, -size + 17 + 16, outlineColor, transparency);

        // corner top left
        fill(stack,-i -7 - 8, -size + 12 + 17, -i - 6 - 10, -size + 11 + 17, outlineColor, transparency);

        // corner button left
        fill(stack,-i - 7 - 9, -size + 18 + 16, -i - 5 - 10, -size + 17 + 16, outlineColor, transparency);

        // bottom bar
        fill(stack,-i + 1 - 11, -size + 19 + 16, -i - 5 - 10, -size + 18 + 16, outlineColor, transparency);

        // right bar
        fill(stack,-i + 2 - 11, -size + 12+ 17, -i + 2 - 10 , -size + 17 + 16, outlineColor, transparency);

        // left bar
        fill(stack,-i - 6 - 11 , -size + 12+ 17, -i - 6 - 10, -size + 17 + 16, outlineColor, transparency);

        //fill middle
        fill(stack,-i + 1 - 11 , -size + 11+ 17, - i - 6 - 9, -size + 18 + 16, insideColor, transparency);

        // fill left
        fill(stack,-i - 7 - 9 , -size + 12+ 17, - i - 6 - 9, -size + 17 + 16, insideColor, transparency);

        // fill right
        fill(stack,-i + 1 - 11 , -size + 12+ 17, - i + 2 - 11, -size + 17 + 16, insideColor, transparency);

    }


}

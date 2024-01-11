package com.thomas7520.bubbleschat.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.glfw.GLFW;

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



    public static void registerBindings() {
        keyBindings[0] = new KeyMapping("key.openoptions.desc" , GLFW.GLFW_KEY_B, "key.comicsbubbleschat.category");

        for (KeyMapping keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }


    public static void draw(Message message, ArrayDeque<Message> messages, RenderPlayerEvent.Pre event, long endTime, boolean isSneaking, boolean drawBubble, boolean drawText, boolean drawLittleBubbles, int bubbleQueue)
    {

        Color outlineColor = message.getColorOutline().getColor();
        Color insideColor = message.getColorInside().getColor();
        Color textColor = message.getColorText().getColor();

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


        Entity entityIn = event.getEntity();
        float f = entityIn.getBbHeight() + 0.8f + BubblesConfig.CLIENT.sizeBubble.get() * 0.01f;
        stack.pushPose();
        stack.translate(0.0D, f, 0.0D);
        stack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        stack.scale(size, size, size);
        
        if(!event.getEntity().isDiscrete() || !serverSupport && !BubblesConfig.CLIENT.canThroughBlocks.get() || serverSupport && bubbleThroughBlocks) {
            RenderSystem.enableDepthTest();
        }

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

            drawBubble(i2, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB(), stack);

        }

        if(drawLittleBubbles) {
            drawLittleBubble(i2, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB(), stack);
            drawMediumBubble(i2, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB(), stack);
        }

        if(drawText) {
            stack.translate(0.0f, 0.0f, 0.1f);
            if(textColor.getAlpha() > 3) {
                boolean flag = !event.getEntity().isDiscrete() || !serverSupport && !BubblesConfig.CLIENT.canThroughBlocks.get() || serverSupport && bubbleThroughBlocks;
                int lineY = -linesHeight / 2 + 3;
                for (String line : lines) {
                    renderer.drawInBatch(line, -renderer.width(line) / 2f, lineY, textColor.getRGB(), false, stack.last().pose(), buffers, flag, 0, event.getPackedLight());
                    lineY = lineY + renderer.lineHeight + 1;
                }

            }


        }



        stack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    public static void fill(PoseStack matrixStack, int p_fill_0_, int p_fill_1_, int p_fill_2_, int p_fill_3_, int p_fill_4_) {
        GuiComponent.fill(matrixStack, p_fill_0_, p_fill_1_, p_fill_2_, p_fill_3_, p_fill_4_);
    }


    public static void drawBubble(int i, int size, int outlineColor, int insideColor, PoseStack matrix) {
        // top bar
        fill(matrix,-i - 1, size + 1, i + 1,  size + 2 , outlineColor);

        // bottom bar
        fill(matrix,-i - 1, - size + 4, i + 1,  - size + 5 , outlineColor);

        // corner top left
        fill(matrix,-i - 1, size + 2, -i - 2 ,  size + 3 , outlineColor);

        // corner bottom left
        fill(matrix,-i - 1, -size + 4, -i - 2 ,  -size + 3 , outlineColor);

        // left bar
        fill(matrix,-i - 3, size + 3, -i  - 2 ,  -size + 3, outlineColor);

        // right bar
        fill(matrix,i + 3, size + 3, i  +  2 ,  -size + 3, outlineColor);

        // corner bottom right
        fill(matrix,i + 1, -size + 4, i + 2 ,  -size + 3 , outlineColor);

        // corner top right
        fill(matrix,i + 1, size + 2, i + 2 , size + 3 , outlineColor);

        // fill middle

        fill(matrix,-i - 1, size + 2, i + 1,  -size + 4 , insideColor);

        // fill left middle
        fill(matrix,-i - 2, size + 3, -i - 1,  -size + 3 , insideColor);

        // fill right middle
        fill(matrix,i + 2, size + 3, i + 1,  -size + 3 , insideColor);

    }

    public static void drawMediumBubble(int i, int size, int outlineColor, int insideColor, PoseStack stack) {
        // top bar
        fill(stack,-i + 1, -size + 11, -i - 6, -size + 10, outlineColor);

        // corner top right
        fill(stack,-i + 2, -size + 12, -i + 1, -size + 11, outlineColor);

        // corner top buttom
        fill(stack,-i + 2, -size + 18, -i + 1, -size + 17, outlineColor);

        // corner top right
        fill(stack,-i -7, -size + 12, -i - 6, -size + 11, outlineColor);

        // corner top buttom
        fill(stack,-i - 7, -size + 18, -i - 6, -size + 17, outlineColor);

        // bottom bar
        fill(stack,-i + 1, -size + 19, -i - 6, -size + 18, outlineColor);

        // right bar
        fill(stack,-i + 2 , -size + 12, -i + 3 , -size + 17, outlineColor);

        // left bar
        fill(stack,-i - 7 , -size + 12, -i - 8, -size + 17, outlineColor);

        // fill middle
        fill(stack,-i + 1 , -size + 11, - i - 6, -size + 18, insideColor);

        // fill left
        fill(stack,-i - 7 , -size + 12, - i - 6, -size + 17, insideColor);

        // fill right
        fill(stack,-i + 1 , -size + 12, - i + 2, -size + 17, insideColor);
    }

    public static void drawLittleBubble(int i, int size, int outlineColor, int insideColor, PoseStack stack) {
        // top bar
        fill(stack,-i + 1 - 11, -size + 11 + 17, -i - 6 - 9, -size + 10 + 17, outlineColor);

        // corner top right
        fill(stack,-i + 2 - 11, -size + 12+ 17, -i - 10, -size + 11 + 17, outlineColor);

        // corner right buttom
        fill(stack,-i + 2 - 11, -size + 18+ 16, -i - 10, -size + 17 + 16, outlineColor);

        // corner top left
        fill(stack,-i -7 - 8, -size + 12 + 17, -i - 6 - 10, -size + 11 + 17, outlineColor);

        // corner button left
        fill(stack,-i - 7 - 9, -size + 18 + 16, -i - 5 - 10, -size + 17 + 16, outlineColor);

        // bottom bar
        fill(stack,-i + 1 - 11, -size + 19 + 16, -i - 5 - 10, -size + 18 + 16, outlineColor);

        // right bar
        fill(stack,-i + 2 - 11, -size + 12+ 17, -i + 2 - 10 , -size + 17 + 16, outlineColor);

        // left bar
        fill(stack,-i - 6 - 11 , -size + 12+ 17, -i - 6 - 10, -size + 17 + 16, outlineColor);

        //fill middle
        fill(stack,-i + 1 - 11 , -size + 11+ 17, - i - 6 - 9, -size + 18 + 16, insideColor);

        // fill left
        fill(stack,-i - 7 - 9 , -size + 12+ 17, - i - 6 - 9, -size + 17 + 16, insideColor);

        // fill right
        fill(stack,-i + 1 - 11 , -size + 12+ 17, - i + 2 - 11, -size + 17 + 16, insideColor);

    }
}

package com.thomas7520.bubbleschat.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class ClientBubblesUtil {

    public static KeyBinding[] keyBindings = new KeyBinding[1];


    public static final HashMap<UUID, Bubble> BUBBLES_SYNC = new HashMap<>();
    public static boolean serverSupport = false;
    public static boolean bubbleThroughBlocks;



    public static void registerBindings() {
        keyBindings[0] = new KeyBinding("key.openoptions.desc" , GLFW.GLFW_KEY_B, "key.comicsbubbleschat.category");

        for (KeyBinding keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }


    public static void draw(Message message, ArrayDeque<Message> messages, RenderLivingEvent<?, ?> event, long endTime, float x, float y, float z, boolean isSneaking, boolean drawBubble, boolean drawText, boolean drawLittleBubbles, int bubbleQueue)
    {

        Color outlineColor = message.getColorOutline().getColor();
        Color insideColor = message.getColorInside().getColor();
        Color textColor = message.getColorText().getColor();

        long timeLeft = endTime - System.currentTimeMillis();

        if(timeLeft < 0) throw new IllegalStateException("timeleft cannot be < 0, contact author if you didn't use api");

        if(timeLeft < 3000) {
            message.getColorOutline().setAlpha((int) timeLeft * BubblesConfig.colorOutline.get().get(3) / 3000);
            message.getColorInside().setAlpha((int) timeLeft * BubblesConfig.colorInside.get().get(3) / 3000);
            message.getColorText().setAlpha((int) timeLeft * BubblesConfig.colorText.get().get(3) / 3000);
        }
        float size = -0.02f;

        MatrixStack stack = event.getMatrixStack();
        IRenderTypeBuffer buffers = event.getBuffers();

        Entity entityIn = event.getEntity();
        float f = entityIn.getHeight() + 0.5F;
        stack.push();

        stack.translate(0.0D, f, 0.0D);
        stack.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());

        stack.scale(size, size, size);

        if(isSneaking || !bubbleThroughBlocks) {
            RenderSystem.enableDepthTest();
        }

        Matrix4f matrix4f = stack.getLast().getMatrix();

        FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
        java.util.List<String> lines = fontrenderer.listFormattedStringToWidth(message.getMessage(), BubblesConfig.lineWidth.get());

        int linesHeight = 10 * lines.size();

        int biggestLineWidth = 0;
        for (String line : lines) {
            int lineWidth = fontrenderer.getStringWidth(line);

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

                messageLines = fontrenderer.listFormattedStringToWidth(message2, BubblesConfig.lineWidth.get());

                if (j2 != bubbleQueue) {
                    distanceBetween = distanceBetween + ((messageLines.size() * 10 + 4));
                } else {
                    distanceBetween = distanceBetween - messageLines.size();
                }
                j2++;
            }

            stack.translate(0.0f, -distanceBetween, 0.0f);

            drawBubble(i2, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB(), matrix4f);

        }

        if(drawLittleBubbles) {
            drawLittleBubble(i2, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB(), matrix4f);
            drawMediumBubble(i2, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB(), matrix4f);
        }

        if(drawText) {
            stack.translate(0.0f, 0.0f, 0.1f);

            if(textColor.getAlpha() > 3) {
                int lineY = -linesHeight / 2 + 3;
                for (String line : lines) {
                    fontrenderer.renderString(line, -fontrenderer.getStringWidth(line) / 2f, lineY, textColor.getRGB(), false, stack.getLast().getMatrix(), buffers, !event.getEntity().isSneaking(), 0, event.getLight());
                    lineY = lineY + fontrenderer.FONT_HEIGHT + 1;
                }
            }
        }

        stack.pop();
    }

    public static void fill(Matrix4f matrix4f, int p_fill_0_, int p_fill_1_, int p_fill_2_, int p_fill_3_, int p_fill_4_) {
        AbstractGui.fill(matrix4f, p_fill_0_, p_fill_1_, p_fill_2_, p_fill_3_, p_fill_4_);
    }



    public static void drawBubble(int i, int size, int outlineColor, int insideColor, Matrix4f matrix4f) {
        // top bar
        fill(matrix4f,-i - 1, size + 1, i + 1,  size + 2 , outlineColor);

        // bottom bar
        fill(matrix4f,-i - 1, - size + 4, i + 1,  - size + 5 , outlineColor);

        // corner top left
        fill(matrix4f,-i - 1, size + 2, -i - 2 ,  size + 3 , outlineColor);

        // corner bottom left
        fill(matrix4f,-i - 1, -size + 4, -i - 2 ,  -size + 3 , outlineColor);

        // left bar
        fill(matrix4f,-i - 3, size + 3, -i  - 2 ,  -size + 3, outlineColor);

        // right bar
        fill(matrix4f,i + 3, size + 3, i  +  2 ,  -size + 3, outlineColor);

        // corner bottom right
        fill(matrix4f,i + 1, -size + 4, i + 2 ,  -size + 3 , outlineColor);

        // corner top right
        fill(matrix4f,i + 1, size + 2, i + 2 , size + 3 , outlineColor);

        // fill middle

        fill(matrix4f,-i - 1, size + 2, i + 1,  -size + 4 , insideColor);

        // fill left middle
        fill(matrix4f,-i - 2, size + 3, -i - 1,  -size + 3 , insideColor);

        // fill right middle
        fill(matrix4f,i + 2, size + 3, i + 1,  -size + 3 , insideColor);

    }

    public static void drawMediumBubble(int i, int size, int outlineColor, int insideColor, Matrix4f matrix4f) {
        // top bar
        fill(matrix4f,-i + 1, -size + 11, -i - 6, -size + 10, outlineColor);

        // corner top right
        fill(matrix4f,-i + 2, -size + 12, -i + 1, -size + 11, outlineColor);

        // corner top buttom
        fill(matrix4f,-i + 2, -size + 18, -i + 1, -size + 17, outlineColor);

        // corner top right
        fill(matrix4f,-i -7, -size + 12, -i - 6, -size + 11, outlineColor);

        // corner top buttom
        fill(matrix4f,-i - 7, -size + 18, -i - 6, -size + 17, outlineColor);

        // bottom bar
        fill(matrix4f,-i + 1, -size + 19, -i - 6, -size + 18, outlineColor);

        // right bar
        fill(matrix4f,-i + 2 , -size + 12, -i + 3 , -size + 17, outlineColor);

        // left bar
        fill(matrix4f,-i - 7 , -size + 12, -i - 8, -size + 17, outlineColor);

        // fill middle
        fill(matrix4f,-i + 1 , -size + 11, - i - 6, -size + 18, insideColor);

        // fill left
        fill(matrix4f,-i - 7 , -size + 12, - i - 6, -size + 17, insideColor);

        // fill right
        fill(matrix4f,-i + 1 , -size + 12, - i + 2, -size + 17, insideColor);
    }

    public static void drawLittleBubble(int i, int size, int outlineColor, int insideColor, Matrix4f matrix4f) {
        // top bar
        fill(matrix4f,-i + 1 - 11, -size + 11 + 17, -i - 6 - 9, -size + 10 + 17, outlineColor);

        // corner top right
        fill(matrix4f,-i + 2 - 11, -size + 12+ 17, -i - 10, -size + 11 + 17, outlineColor);

        // corner right buttom
        fill(matrix4f,-i + 2 - 11, -size + 18+ 16, -i - 10, -size + 17 + 16, outlineColor);

        // corner top left
        fill(matrix4f,-i -7 - 8, -size + 12 + 17, -i - 6 - 10, -size + 11 + 17, outlineColor);

        // corner button left
        fill(matrix4f,-i - 7 - 9, -size + 18 + 16, -i - 5 - 10, -size + 17 + 16, outlineColor);

        // bottom bar
        fill(matrix4f,-i + 1 - 11, -size + 19 + 16, -i - 5 - 10, -size + 18 + 16, outlineColor);

        // right bar
        fill(matrix4f,-i + 2 - 11, -size + 12+ 17, -i + 2 - 10 , -size + 17 + 16, outlineColor);

        // left bar
        fill(matrix4f,-i - 6 - 11 , -size + 12+ 17, -i - 6 - 10, -size + 17 + 16, outlineColor);

        //fill middle
        fill(matrix4f,-i + 1 - 11 , -size + 11+ 17, - i - 6 - 9, -size + 18 + 16, insideColor);

        // fill left
        fill(matrix4f,-i - 7 - 9 , -size + 12+ 17, - i - 6 - 9, -size + 17 + 16, insideColor);

        // fill right
        fill(matrix4f,-i + 1 - 11 , -size + 12+ 17, - i + 2 - 11, -size + 17 + 16, insideColor);

    }
}

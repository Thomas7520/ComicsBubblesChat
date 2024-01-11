package com.thomas7520.bubbleschat.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import com.thomas7520.bubbleschat.util.ResetUtil;
import com.thomas7520.bubbleschat.util.SpecColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;


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

    public static void draw(Message message, ArrayDeque<Message> messages, long endTime, float x, float y, float z, boolean isSneaking, boolean drawBubble, boolean drawText, boolean drawLittleBubbles, int bubbleQueue)
    {

        float viewerYaw = Minecraft.getInstance().getRenderManager().playerViewY;
        float viewerPitch = Minecraft.getInstance().getRenderManager().playerViewX;

        FontRenderer fontRendererIn = Minecraft.getInstance().fontRenderer;
        boolean isThirdPersonFrontal = Minecraft.getInstance().getRenderManager().options.thirdPersonView == 2;

        Color outlineColor = message.getColorOutline().getColor();
        Color insideColor = message.getColorInside().getColor();
        Color textColor = message.getColorText().getColor();

        long timeLeft = endTime - System.currentTimeMillis();

        if(timeLeft < 0) throw new IllegalStateException("timeleft cannot be < 0, contact author if you didn't use api");

        if(timeLeft < 3000) {
            message.getColorOutline().setAlpha((int) timeLeft * BubblesConfig.colorOutline.get().get(3) / 3000);
            message.getColorInside().setAlpha((int) timeLeft * BubblesConfig.colorInside.get().get(3) / 3000);
            if(timeLeft < 2000) message.getColorText().setAlpha((int) timeLeft * BubblesConfig.colorText.get().get(3) / 3000);
        }


        float size = -0.02f;

        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, z);
        GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scalef(size, size, size);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(true);

        if(!isSneaking && bubbleThroughBlocks) {
            GlStateManager.disableDepthTest();
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);


        List<String> lines = fontRendererIn.listFormattedStringToWidth(message.getMessage(), BubblesConfig.lineWidth.get());

        int linesHeight = 10 * lines.size();

        int biggestLineWidth = 0;
        for (String line : lines) {
            int lineWidth = fontRendererIn.getStringWidth(line);

            if(biggestLineWidth < lineWidth) {
                biggestLineWidth = lineWidth;
            }
        }

        float yTranslate = lines.size() + 15;
        int i = biggestLineWidth / 2;

        GlStateManager.disableTexture();
        GlStateManager.translatef(i + 20, -yTranslate, 0.0f);

        float scale = 1.0f;

        GlStateManager.scalef(scale, scale, scale);
        GlStateManager.translatef(0.0f,-linesHeight / 2f - 15, 0.0f);


        if(drawBubble) {

            int distanceBetween = -10;

            int j = 0;

            for (Message value : messages) {
                if (j == bubbleQueue + 1)
                    break;

                String message2 = value.getMessage();

                List<String> messageLines;

                messageLines = fontRendererIn.listFormattedStringToWidth(message2, BubblesConfig.lineWidth.get());

                if (j != bubbleQueue) {
                    distanceBetween = distanceBetween + ((messageLines.size() * 10 + 4));
                } else {
                    distanceBetween = distanceBetween - messageLines.size();
                }
                j++;
            }

            GlStateManager.translatef(0.0f, -distanceBetween, 0.0f);

            drawBubble(i, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB());

        }

        if(drawLittleBubbles) {
            drawLittleBubble(i, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB());
            drawMediumBubble(i, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB());
        }

        if(drawText) {
            GlStateManager.translatef(0.0f, 0.0f, 0.1f);

            GlStateManager.enableTexture();

            if (textColor.getAlpha() > 25) {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


                GlStateManager.depthMask(true);

                int lineY = -linesHeight / 2 + 3;
                for (String line : lines) {
                    fontRendererIn.drawString(line, -fontRendererIn.getStringWidth(line) / 2, lineY, textColor.getRGB());
                    lineY = lineY + fontRendererIn.FONT_HEIGHT + 1;
                }


            }

            GlStateManager.enableDepthTest();
            GL11.glDisable(GL11.GL_BLEND);
        }

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void drawRect(int left, int top, int right, int bottom, int color)
    {
        if (left < right)
        {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    
    public static void drawBubble(int i, int size, int outlineColor, int insideColor) {
        // top bar
        drawRect(-i - 1, size + 1, i + 1,  size + 2 , outlineColor);

        // bottom bar
        drawRect(-i - 1, - size + 4, i + 1,  - size + 5 , outlineColor);

        // corner top left
        drawRect(-i - 1, size + 2, -i - 2 ,  size + 3 , outlineColor);

        // corner bottom left
        drawRect(-i - 1, -size + 4, -i - 2 ,  -size + 3 , outlineColor);

        // left bar
        drawRect(-i - 3, size + 3, -i  - 2 ,  -size + 3, outlineColor);

        // right bar
        drawRect(i + 3, size + 3, i  +  2 ,  -size + 3, outlineColor);

        // corner bottom right
        drawRect(i + 1, -size + 4, i + 2 ,  -size + 3 , outlineColor);

        // corner top right
        drawRect(i + 1, size + 2, i + 2 , size + 3 , outlineColor);

        // fill middle

        drawRect(-i - 1, size + 2, i + 1,  -size + 4 , insideColor);

        // fill left middle
        drawRect(-i - 2, size + 3, -i - 1,  -size + 3 , insideColor);

        // fill right middle
        drawRect(i + 2, size + 3, i + 1,  -size + 3 , insideColor);

    }

    public static void drawMediumBubble(int i, int size, int outlineColor, int insideColor) {
        // top bar
        drawRect(-i + 1, -size + 11, -i - 6, -size + 10, outlineColor);

        // corner top right
        drawRect(-i + 2, -size + 12, -i + 1, -size + 11, outlineColor);

        // corner top buttom
        drawRect(-i + 2, -size + 18, -i + 1, -size + 17, outlineColor);

        // corner top right
        drawRect(-i -7, -size + 12, -i - 6, -size + 11, outlineColor);

        // corner top buttom
        drawRect(-i - 7, -size + 18, -i - 6, -size + 17, outlineColor);

        // bottom bar
        drawRect(-i + 1, -size + 19, -i - 6, -size + 18, outlineColor);

        // right bar
        drawRect(-i + 2 , -size + 12, -i + 3 , -size + 17, outlineColor);

        // left bar
        drawRect(-i - 7 , -size + 12, -i - 8, -size + 17, outlineColor);

        // fill middle
        drawRect(-i + 1 , -size + 11, - i - 6, -size + 18, insideColor);

        // fill left
        drawRect(-i - 7 , -size + 12, - i - 6, -size + 17, insideColor);

        // fill right
        drawRect(-i + 1 , -size + 12, - i + 2, -size + 17, insideColor);
    }

    public static void drawLittleBubble(int i, int size, int outlineColor, int insideColor) {
        // top bar
        drawRect(-i + 1 - 11, -size + 11 + 17, -i - 6 - 9, -size + 10 + 17, outlineColor);

        // corner top right
        drawRect(-i + 2 - 11, -size + 12+ 17, -i - 10, -size + 11 + 17, outlineColor);

        // corner right buttom
        drawRect(-i + 2 - 11, -size + 18+ 16, -i - 10, -size + 17 + 16, outlineColor);

        // corner top left
        drawRect(-i -7 - 8, -size + 12 + 17, -i - 6 - 10, -size + 11 + 17, outlineColor);

        // corner button left
        drawRect(-i - 7 - 9, -size + 18 + 16, -i - 5 - 10, -size + 17 + 16, outlineColor);

        // bottom bar
        drawRect(-i + 1 - 11, -size + 19 + 16, -i - 5 - 10, -size + 18 + 16, outlineColor);

        // right bar
        drawRect(-i + 2 - 11, -size + 12+ 17, -i + 2 - 10 , -size + 17 + 16, outlineColor);

        // left bar
        drawRect(-i - 6 - 11 , -size + 12+ 17, -i - 6 - 10, -size + 17 + 16, outlineColor);

        //fill middle
        drawRect(-i + 1 - 11 , -size + 11+ 17, - i - 6 - 9, -size + 18 + 16, insideColor);

        // fill left
        drawRect(-i - 7 - 9 , -size + 12+ 17, - i - 6 - 9, -size + 17 + 16, insideColor);

        // fill right
        drawRect(-i + 1 - 11 , -size + 12+ 17, - i + 2 - 11, -size + 17 + 16, insideColor);

    }

}

package com.thomas7520.bubbleschat.client;

import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.util.Bubble;
import com.thomas7520.bubbleschat.util.Message;
import com.thomas7520.bubbleschat.util.ComicsUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;


public class ClientBubblesUtil {

    public static KeyBinding[] keyBindings = new KeyBinding[1];


    public static final HashMap<UUID, Bubble> BUBBLES_SYNC = new HashMap<>();
    public static boolean serverSupport = false;
    public static boolean bubbleThroughBlocks;


    private static Color outlineColorConfig;
    private static Color insideColorConfig;
    private static Color textColorConfig;

    public static void registerBindings() {
        keyBindings[0] = new KeyBinding("key.openoptions.desc", Keyboard.KEY_B, "key.comicsbubbleschat.category");
       // keyBindings[1] = new KeyBinding("key.openguitext.desc", Keyboard.KEY_M, "key.comicsbubbleschat.category");

        for (KeyBinding keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    public static void draw(String text, ArrayDeque<Message> messages, long endTime, float x, float y, float z, boolean isSneaking, boolean drawBubble, boolean drawText, boolean drawLittleBubbles, int bubbleQueue)
    {
        
        float viewerYaw = Minecraft.getMinecraft().getRenderManager().playerViewY;
        float viewerPitch = Minecraft.getMinecraft().getRenderManager().playerViewX;

        FontRenderer fontRendererIn = Minecraft.getMinecraft().fontRenderer;
        boolean isThirdPersonFrontal = Minecraft.getMinecraft().getRenderManager().options.thirdPersonView == 2;

        if(outlineColorConfig == null || insideColorConfig == null || textColorConfig == null) updateColors(false);

        Color outlineColor = outlineColorConfig;
        Color insideColor = insideColorConfig;
        Color textColor = textColorConfig;

        long timeLeft = endTime - System.currentTimeMillis();

        if(timeLeft < 0) throw new IllegalStateException("timeleft cannot be < 0, contact author if you didn't use api");

        if(timeLeft < 3000) {
            outlineColor = new Color(outlineColorConfig.getRed(), outlineColorConfig.getGreen(), outlineColorConfig.getBlue(), (int) timeLeft * BubblesConfig.client.colorOutline[3] / 3000);
            insideColor = new Color(insideColorConfig.getRed(), insideColorConfig.getGreen(), insideColorConfig.getBlue(), (int) timeLeft * BubblesConfig.client.colorInside[3] / 3000);
            if(timeLeft < 2000) textColor = new Color(textColorConfig.getRed(), textColorConfig.getGreen(), textColorConfig.getBlue(), (int) timeLeft * BubblesConfig.client.colorText[3] / 2000);
        } else if(outlineColor.getAlpha() < BubblesConfig.client.colorOutline[3]) {
            updateColors(false);
        }


        float size = -0.02f;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(size, size, size);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(true);

        if(!isSneaking && bubbleThroughBlocks) {
            GlStateManager.disableDepth();
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);


        List<String> lines = fontRendererIn.listFormattedStringToWidth(text, BubblesConfig.client.lineWidth);

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

        GlStateManager.disableTexture2D();
        GlStateManager.translate(i + 20, -yTranslate, 0.0f);

        float scale = 1.0f;

        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(0.0f,-linesHeight / 2f - 15, 0.0f);


        if(drawBubble) {

            int distanceBetween = -10;

            int j = 0;

            for (Message value : messages) {
                if (j == bubbleQueue + 1)
                    break;

                String message = value.getMessage();

                List<String> messageLines;

                messageLines = fontRendererIn.listFormattedStringToWidth(message, BubblesConfig.client.lineWidth);

                if (j != bubbleQueue) {
                    distanceBetween = distanceBetween + ((messageLines.size() * 10 + 4));
                } else {
                    distanceBetween = distanceBetween - messageLines.size();
                }
                j++;
            }

                GlStateManager.translate(0.0f, -distanceBetween, 0.0f);

            drawBubble(i, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB());

        }

        if(drawLittleBubbles) {
            drawLittleBubble(i, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB());
            drawMediumBubble(i, -linesHeight / 2, outlineColor.getRGB(), insideColor.getRGB());
        }

        if(drawText) {
            GlStateManager.translate(0.0f, 0.0f, 0.1f);

            GlStateManager.enableTexture2D();

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

            GlStateManager.enableDepth();
            GL11.glDisable(GL11.GL_BLEND);
        }

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
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

    public static void updateColors(boolean sync) {
        int[] colorOutline = BubblesConfig.client.colorOutline;
        int[] colorInside = BubblesConfig.client.colorInside;
        int[] colorText = BubblesConfig.client.colorText;

        outlineColorConfig = new Color(colorOutline[0], colorOutline[1], colorOutline[2], colorOutline[3]);
        insideColorConfig = new Color(colorInside[0], colorInside[1], colorInside[2], colorInside[3]);
        textColorConfig = new Color(colorText[0], colorText[1], colorText[2], colorText[3]);

        if(sync) {
            ComicsUtil.syncFile();
        }
    }

}

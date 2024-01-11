package com.thomas7520.bubbleschat.client.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class GuiConfig extends GuiScreen {


    private static final ResourceLocation ARROW_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/right_arrow.png");
    private static final ResourceLocation ARROW_HOVER_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/right_arrow_hover.png");

    private final int[] outlineValues = BubblesConfig.client.colorOutline;
    private final int[] insideValues = BubblesConfig.client.colorInside;
    private final int[] textValues = BubblesConfig.client.colorText;

    private final String[] stateValues = {I18n.format("text.red"), I18n.format("text.green"), I18n.format("text.blue"), I18n.format("text.alpha")};
    private final GuiSlider[] slidersOutline = new GuiSlider[4];
    private final GuiSlider[] slidersInside = new GuiSlider[4];
    private final GuiSlider[] slidersText = new GuiSlider[4];

    private int guiLeft;
    private int guiTop;

    private boolean buttonNextHover;

    @Override
    public void initGui() {

        ClientBubblesUtil.updateColors(false);

        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;

        initSliders();
        super.initGui();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int xButton = this.width - 30;
        int yButton = guiTop * 2 - 30;
        buttonNextHover = mouseX >= xButton && mouseY >= yButton && mouseX < xButton + 30 && mouseY < yButton + 30;

        if(buttonNextHover)
        {
            mc.getTextureManager().bindTexture(ARROW_HOVER_ICON);
        }

        else
        {
            mc.getTextureManager().bindTexture(ARROW_ICON);
        }

        drawScaledCustomSizeModalRect(xButton, yButton, 0,0 ,256,256, 30,30, 256,256);
        Color textColor = new Color(textValues[0], textValues[1], textValues[2], textValues[3]);
        Color outlineColor = new Color(outlineValues[0], outlineValues[1], outlineValues[2], outlineValues[3]);
        Color insideColor = new Color(insideValues[0], insideValues[1], insideValues[2], insideValues[3]);

        String title = I18n.format("text.config.title");
        drawString(fontRenderer, ChatFormatting.UNDERLINE + title + " 1/2", guiLeft - fontRenderer.getStringWidth(title) / 2 - 10, guiTop / 2 - 50, Color.WHITE.getRGB());

        GL11.glPushMatrix();
        GL11.glScalef(0.95f,0.95f,0.95f);

        String outlineTitle = I18n.format("text.config.outline");
        drawString(fontRenderer, ChatFormatting.UNDERLINE + outlineTitle, guiLeft - fontRenderer.getStringWidth(outlineTitle) / 2 - 140, guiTop / 2 + 20, Color.WHITE.getRGB());

        String insideTitle = I18n.format("text.config.inside");
        drawString(fontRenderer, ChatFormatting.UNDERLINE + insideTitle, guiLeft -fontRenderer.getStringWidth(insideTitle) / 2 + 10, guiTop / 2 + 20, Color.WHITE.getRGB());

        String textTitle = I18n.format("text.config.text");
        drawString(fontRenderer, ChatFormatting.UNDERLINE + textTitle, guiLeft -fontRenderer.getStringWidth(textTitle) / 2 + 160, guiTop / 2 + 20, Color.WHITE.getRGB());

        GL11.glPopMatrix();

        drawRect(guiLeft - 210, guiTop / 2 + 130, guiLeft - 100 + 25, guiTop / 2 + 140, outlineColor.getRGB());
        drawRect(guiLeft - 70, guiTop / 2 + 130, guiLeft + 20 + 45, guiTop / 2 + 140, insideColor.getRGB());
        drawRect(guiLeft + 40 + 30, guiTop / 2 + 130, guiLeft + 140 + 65, guiTop / 2 + 140, textColor.getRGB());

        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft, guiTop / 2f - 30f, 0.0f);
        GL11.glScalef(0.9f,0.9f,0.9f);

        ClientBubblesUtil.drawBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB());
        ClientBubblesUtil.drawMediumBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB());
        ClientBubblesUtil.drawLittleBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB());

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        fontRenderer.drawString(I18n.format("text.config.test"), -fontRenderer.getStringWidth(I18n.format("text.config.test")) / 2, 0, textColor.getRGB());

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glTranslatef(0.0f, 0.0f, 0.0f);
        GL11.glPopMatrix();

        if(!ClientBubblesUtil.serverSupport && !Minecraft.getMinecraft().isSingleplayer()) {
            String noLinked = I18n.format("text.config.nolinked");
            fontRenderer.drawString(noLinked, guiLeft - fontRenderer.getStringWidth(noLinked) / 2, guiTop * 2 - 10, Color.RED.getRGB());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void initSliders() {
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            slidersOutline[i] = new net.minecraftforge.fml.client.config.GuiSlider(finalI, guiLeft - 170 - 40, guiTop / 2 + 40 + i * 20, 135, 20, stateValues[i] + " : ", "", 0
                    , 255, outlineValues[i], false, true) {

                @Override
                protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
                    outlineValues[finalI] = getValueInt();
                    ClientBubblesUtil.updateColors(false);
                    super.mouseDragged(par1Minecraft, par2, par3);
                }

                @Override
                public void mouseReleased(int par1, int par2) {
                    ClientBubblesUtil.updateColors(true);
                    super.mouseReleased(par1, par2);
                }
            };

            slidersInside[i] = new net.minecraftforge.fml.client.config.GuiSlider(finalI + 4, guiLeft - 170 + 100, guiTop / 2 + 40 + finalI * 20, 135, 20, stateValues[finalI] + " : ", "", 0
                    , 255, insideValues[finalI], false, true) {

                @Override
                protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
                    insideValues[finalI] = getValueInt();
                    ClientBubblesUtil.updateColors(false);
                    super.mouseDragged(par1Minecraft, par2, par3);
                }

                @Override
                public void mouseReleased(int par1, int par2) {
                    ClientBubblesUtil.updateColors(true);
                    super.mouseReleased(par1, par2);
                }
            };

            slidersText[i] = new net.minecraftforge.fml.client.config.GuiSlider(i + 8, guiLeft - 170 + 200 + 40, guiTop / 2 + 40 + i * 20, 135, 20, stateValues[i] + " : ", "", i == 3 ? 25 : 0
                    , 255, textValues[i], false, true) {

                @Override
                protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
                    textValues[finalI] = getValueInt();
                    ClientBubblesUtil.updateColors(false);
                    super.mouseDragged(par1Minecraft, par2, par3);
                }

                @Override
                public void mouseReleased(int par1, int par2) {
                    ClientBubblesUtil.updateColors(true);
                    super.mouseReleased(par1, par2);
                }
            };

            buttonList.add(slidersOutline[i]);
            buttonList.add(slidersInside[i]);
            buttonList.add(slidersText[i]);

            GuiSlider sliderDuration = new GuiSlider(13, guiLeft - 170 - 40, guiTop / 2 + 145, 415, 15, I18n.format("text.config.duration.prefix") + " : ", " " + I18n.format("text.config.duration.suffix"), 0, 60, BubblesConfig.client.durationBubbles, false, true) {

                @Override
                protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
                    BubblesConfig.client.durationBubbles = getValueInt();
                    super.mouseDragged(par1Minecraft, par2, par3);
                }

                @Override
                public void mouseReleased(int par1, int par2) {
                    ConfigManager.sync(ComicsBubblesChat.MODID, Config.Type.INSTANCE);
                    super.mouseReleased(par1, par2);
                }

            };

            buttonList.add(sliderDuration);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(buttonNextHover) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiConfigNext());
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

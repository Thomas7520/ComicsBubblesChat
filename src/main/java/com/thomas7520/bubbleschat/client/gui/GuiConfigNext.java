package com.thomas7520.bubbleschat.client.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.util.ComicsUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class GuiConfigNext extends GuiScreen {

    private static final ResourceLocation ARROW_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/left_arrow.png");
    private static final ResourceLocation ARROW_HOVER_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/left_arrow_hover.png");

    private GuiButton buttonResetColor;
    private GuiButton buttonResetDuration;
    private GuiButton buttonClearBubbles;
    private GuiButton buttonResetStack;
    private GuiButton buttonResetLineWidth;
    private GuiButton buttonSaveMaxStack;
    private GuiButton buttonSaveMaxLine;

    private GuiTextField bubblesStackField;
    private GuiTextField bubblesLineWidthField;
    private int guiLeft;
    private int guiTop;
    private boolean buttonPrevHover;

    @Override
    public void initGui() {
        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;


        String resetColor = I18n.format("text.config.resetcolor");
        String resetDuration = I18n.format("text.config.resetduration");
        String clearBubbles = I18n.format("text.config.clearbubbles");
        String resetStack = I18n.format("text.config.resetstack");
        String resetLineWidth = I18n.format("text.config.resetlinewidth");

        buttonResetColor = new GuiButton(0, guiLeft - 100, guiTop / 2 - 20, 200 ,20, resetColor);
        buttonResetDuration = new GuiButton(1, guiLeft - 100, guiTop / 2, 200 ,20, resetDuration);
        buttonResetStack = new GuiButton(2, guiLeft - 100, guiTop / 2 + 20, 200 ,20, resetStack);
        buttonResetLineWidth = new GuiButton(3, guiLeft - 100, guiTop / 2 + 40, 200 ,20, resetLineWidth);
        buttonClearBubbles = new GuiButton(4, guiLeft - 100, guiTop / 2 + 60, 200 ,20, clearBubbles);

        buttonSaveMaxStack = new GuiButton(5, guiLeft + 104, guiTop / 2 + 100, 20, 20, ChatFormatting.GREEN + "✔");
        buttonSaveMaxLine = new GuiButton(6, guiLeft + 104, guiTop / 2 + 140, 20, 20, ChatFormatting.GREEN + "✔");

        bubblesStackField = new GuiTextField(0, fontRenderer,guiLeft - 99, guiTop / 2 + 100, 198, 20);
        bubblesStackField.setValidator(this::isNumeric);
        bubblesStackField.writeText(String.valueOf(BubblesConfig.client.maxBubblesStack));

        bubblesLineWidthField = new GuiTextField(1, fontRenderer,guiLeft - 99, guiTop / 2 + 140, 198, 20);
        bubblesLineWidthField.setValidator(this::isNumeric);
        bubblesLineWidthField.writeText(String.valueOf(BubblesConfig.client.lineWidth));

        buttonList.add(buttonResetColor);
        buttonList.add(buttonResetDuration);
        buttonList.add(buttonClearBubbles);
        buttonList.add(buttonResetStack);
        buttonList.add(buttonResetLineWidth);
        buttonList.add(buttonSaveMaxStack);
        buttonList.add(buttonSaveMaxLine);
        buttonSaveMaxStack.enabled = false;
        buttonSaveMaxLine.enabled = false;
        super.initGui();
    }

    @Override
    public void updateScreen() {
        bubblesStackField.updateCursorCounter();
        bubblesLineWidthField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();


        int xButton = 0;
        int yButton = height - 30;

        buttonPrevHover = mouseX >= xButton && mouseY >= yButton && mouseX < xButton + 30  && mouseY < yButton + 30;

        if(buttonPrevHover)
        {
            mc.getTextureManager().bindTexture(ARROW_HOVER_ICON);
        }

        else
        {
            mc.getTextureManager().bindTexture(ARROW_ICON);
        }


        drawScaledCustomSizeModalRect(xButton, yButton, 0,0 ,256,256, 30,30, 256,256);

        String title = I18n.format("text.config.title");
        drawString(fontRenderer, ChatFormatting.UNDERLINE + title + " 2/2", guiLeft - fontRenderer.getStringWidth(title) / 2 - 10, guiTop / 2 - 50, Color.WHITE.getRGB());

        String maxBubblesText = I18n.format("text.config.maxstackbubbles");
        drawString(fontRenderer, maxBubblesText, guiLeft - fontRenderer.getStringWidth(maxBubblesText) / 2, guiTop / 2 + 90, Color.WHITE.getRGB());

        String maxLineWidth = I18n.format("text.config.maxlinewidth");
        drawString(fontRenderer, maxLineWidth, guiLeft - fontRenderer.getStringWidth(maxLineWidth) / 2, guiTop / 2 + 130, Color.WHITE.getRGB());

        bubblesStackField.drawTextBox();
        bubblesLineWidthField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(buttonPrevHover) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiConfig());
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        if(mouseX >= bubblesStackField.x && mouseY >= bubblesStackField.y && mouseX < bubblesStackField.x + 198  && mouseY < bubblesStackField.y + 20) {
            bubblesLineWidthField.setFocused(false);
            bubblesStackField.setFocused(true);
        }

        if(mouseX >= bubblesLineWidthField.x && mouseY >= bubblesLineWidthField.y && mouseX < bubblesLineWidthField.x + 198  && mouseY < bubblesLineWidthField.y + 20) {
            bubblesStackField.setFocused(false);
            bubblesLineWidthField.setFocused(true);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 0) {
            ComicsUtil.resetColors();
        }

        if(button.id == 1) {
            ComicsUtil.resetDuration();
        }

        if(button.id == 2) {
            BubblesConfig.client.maxBubblesStack = 0;
            ComicsUtil.syncFile();
            bubblesStackField.setText("0");
            buttonSaveMaxStack.enabled = false;
        }

        if(button.id == 3) {
            BubblesConfig.client.lineWidth = 150;
            ComicsUtil.syncFile();
            bubblesLineWidthField.setText("150");
            buttonSaveMaxLine.enabled = false;
        }

        if(button.id == 4) {
            ComicsUtil.clearBubbles();
        }

        if(button.id == 5) {
            BubblesConfig.client.maxBubblesStack = Integer.parseInt(bubblesStackField.getText());
            button.enabled = false;
            ComicsUtil.syncFile();
        }

        if(button.id == 6) {
            BubblesConfig.client.lineWidth = Integer.parseInt(bubblesLineWidthField.getText());
            button.enabled = false;
            ComicsUtil.syncFile();
        }



        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(bubblesStackField.isFocused()) {
            bubblesStackField.textboxKeyTyped(typedChar,keyCode);
            String text = bubblesStackField.getText();

            buttonSaveMaxStack.enabled = !text.isEmpty() && !bubblesStackField.getText().equalsIgnoreCase(String.valueOf(BubblesConfig.client.maxBubblesStack));
        }

        if(bubblesLineWidthField.isFocused()) {
            bubblesLineWidthField.textboxKeyTyped(typedChar,keyCode);
            String text = bubblesLineWidthField.getText();

            buttonSaveMaxLine.enabled = !text.isEmpty() && Integer.parseInt(text) >= 50 &&  !bubblesLineWidthField.getText().equalsIgnoreCase(String.valueOf(BubblesConfig.client.lineWidth));
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private boolean isNumeric(final CharSequence cs) {
        if (cs.length() == 0) {
            return true;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}

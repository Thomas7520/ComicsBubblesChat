package com.thomas7520.bubbleschat.client.screen;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.util.ResetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.math.BigInteger;

public class ScreenConfigNext extends Screen {

    private static final ResourceLocation ARROW_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/left_arrow.png");
    private static final ResourceLocation ARROW_HOVER_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/left_arrow_hover.png");

    private Button buttonSaveMaxStack;
    private Button buttonSaveMaxLine;

    private TextFieldWidget bubblesStackField;
    private TextFieldWidget bubblesLineWidthField;
    private int guiLeft;
    private int guiTop;
    private boolean buttonPrevHover;

    protected ScreenConfigNext() {
        super(new TranslationTextComponent("text.config.title"));
    }

    @Override
    public void init() {
        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;


        String resetColor = I18n.format("text.config.resetcolor");
        String resetDuration = I18n.format("text.config.resetduration");
        String clearBubbles = I18n.format("text.config.clearbubbles");
        String resetStack = I18n.format("text.config.resetstack");
        String resetLineWidth = I18n.format("text.config.resetlinewidth");

        Button buttonResetColor = new Button(guiLeft - 100, guiTop / 2 - 20, 200, 20, resetColor, p_onPress_1_ -> ResetUtil.resetColors());
        Button buttonResetDuration = new Button(guiLeft - 100, guiTop / 2, 200, 20, resetDuration, p_onPress_1_ -> ResetUtil.resetDuration());
        Button buttonResetStack = new Button(guiLeft - 100, guiTop / 2 + 20, 200, 20, resetStack, p_onPress_1_ -> {
            BubblesConfig.maxBubblesStack.set(0);
            bubblesStackField.setText("0");
            buttonSaveMaxStack.active = false;
        });
        Button buttonResetLineWidth = new Button(guiLeft - 100, guiTop / 2 + 40, 200, 20, resetLineWidth, p_onPress_1_ -> {
            BubblesConfig.lineWidth.set(150);
            bubblesLineWidthField.setText("150");
            bubblesLineWidthField.active = false;
        });

        Button buttonClearBubbles = new Button(guiLeft - 100, guiTop / 2 + 60, 200, 20, clearBubbles, p_onPress_1_ -> ResetUtil.clearBubbles());

        buttonSaveMaxStack = new Button(guiLeft + 104, guiTop / 2 + 100, 20, 20, ChatFormatting.GREEN + "✔", p_onPress_1_ -> {
            BubblesConfig.maxBubblesStack.set(Integer.parseInt(bubblesStackField.getText()));
            buttonSaveMaxStack.active = false;
        });
        buttonSaveMaxLine = new Button(guiLeft + 104, guiTop / 2 + 140, 20, 20, ChatFormatting.GREEN + "✔", p_onPress_1_ -> {
            BubblesConfig.lineWidth.set(Integer.parseInt(bubblesLineWidthField.getText()));
            buttonSaveMaxLine.active = false;
        });

        bubblesStackField = new TextFieldWidget(font,guiLeft - 99, guiTop / 2 + 100, 198, 20, "");
        bubblesStackField.setValidator(this::isNumeric);
        bubblesStackField.setText(String.valueOf(BubblesConfig.maxBubblesStack.get()));

        bubblesLineWidthField = new TextFieldWidget(font,guiLeft - 99, guiTop / 2 + 140, 198, 20, "");
        bubblesLineWidthField.setValidator(this::isNumeric);
        bubblesLineWidthField.setText(String.valueOf(BubblesConfig.lineWidth.get()));

        addButton(buttonResetColor);
        addButton(buttonResetDuration);
        addButton(buttonClearBubbles);
        addButton(buttonResetStack);
        addButton(buttonResetLineWidth);
        addButton(buttonSaveMaxStack);
        addButton(buttonSaveMaxLine);
        buttonSaveMaxStack.active = false;
        buttonSaveMaxLine.active = false;
        super.init();
    }

    @Override
    public void tick() {
        bubblesLineWidthField.tick();
        bubblesStackField.tick();
        super.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float p_render_3_) {
        renderBackground();


        int xButton = 0;
        int yButton = height - 30;

        buttonPrevHover = mouseX >= xButton && mouseY >= yButton && mouseX < xButton + 30  && mouseY < yButton + 30;

        if(buttonPrevHover)
        {
            minecraft.getTextureManager().bindTexture(ARROW_HOVER_ICON);
        }

        else
        {
            minecraft.getTextureManager().bindTexture(ARROW_ICON);
        }


        blit(xButton, yButton, 10, 0F, 0F, 32, 32, 32, 32);

        String title = I18n.format("text.config.title");
        drawString(font, ChatFormatting.UNDERLINE + title + " 2/2", guiLeft - font.getStringWidth(title) / 2 - 10, guiTop / 2 - 50, Color.WHITE.getRGB());

        String maxBubblesText = I18n.format("text.config.maxstackbubbles");
        drawString(font, maxBubblesText, guiLeft - font.getStringWidth(maxBubblesText) / 2, guiTop / 2 + 70, Color.WHITE.getRGB());

        bubblesStackField.render(mouseX, mouseY, p_render_3_);
        bubblesLineWidthField.render(mouseX, mouseY, p_render_3_);
        super.render(mouseX, mouseY, p_render_3_);
    }



    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(buttonPrevHover) {
            Minecraft.getInstance().displayGuiScreen(new ScreenConfig());
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        if(mouseX >= bubblesStackField.x && mouseY >= bubblesStackField.y && mouseX < bubblesStackField.x + 198  && mouseY < bubblesStackField.y + 20) {
            bubblesStackField.setFocused2(true);
            bubblesLineWidthField.setFocused2(false);
        }

        if(mouseX >= bubblesLineWidthField.x && mouseY >= bubblesLineWidthField.y && mouseX < bubblesLineWidthField.x + 198  && mouseY < bubblesLineWidthField.y + 20) {
            bubblesLineWidthField.setFocused2(true);
            bubblesStackField.setFocused2(false);
        }
        return super.mouseClicked(mouseX,mouseY,mouseButton);
    }


    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (bubblesStackField.isFocused()) {
            bubblesStackField.charTyped(p_charTyped_1_, p_charTyped_2_);
            String text = bubblesStackField.getText();
            buttonSaveMaxStack.active = !text.isEmpty() && !bubblesStackField.getText().equalsIgnoreCase(String.valueOf(BubblesConfig.maxBubblesStack.get()));

        }

        if (bubblesLineWidthField.isFocused()) {
            bubblesLineWidthField.charTyped(p_charTyped_1_, p_charTyped_2_);
            String text = bubblesLineWidthField.getText();
            buttonSaveMaxLine.active = !text.isEmpty() && Integer.parseInt(text) >= 50 && !bubblesLineWidthField.getText().equalsIgnoreCase(String.valueOf(BubblesConfig.lineWidth.get()));
        }
        return super.charTyped(p_charTyped_1_, p_charTyped_2_);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if(keyCode == GLFW.GLFW_KEY_ESCAPE) return super.keyPressed(keyCode, scanCode, modifiers);

        if(keyCode != GLFW.GLFW_KEY_BACKSPACE) return true;

        if (bubblesStackField.isFocused()) {
            bubblesStackField.keyPressed(keyCode, scanCode, modifiers);
            String text = bubblesStackField.getText();
            buttonSaveMaxStack.active = !text.isEmpty() && !bubblesStackField.getText().equalsIgnoreCase(String.valueOf(BubblesConfig.maxBubblesStack.get()));

        }

        if (bubblesLineWidthField.isFocused()) {
            String text = bubblesLineWidthField.getText();
            buttonSaveMaxLine.active = !text.isEmpty() && Integer.parseInt(text) >= 50 && !bubblesLineWidthField.getText().equalsIgnoreCase(String.valueOf(BubblesConfig.lineWidth.get()));
            bubblesLineWidthField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean isNumeric(final CharSequence cs) {
        if (cs.length() == 0 || cs.toString().equalsIgnoreCase("\u0008")) {
            return true;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return new BigInteger(cs.toString()).compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0;
    }
}

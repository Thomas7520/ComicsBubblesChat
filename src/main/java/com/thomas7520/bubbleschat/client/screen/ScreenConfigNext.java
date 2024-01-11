package com.thomas7520.bubbleschat.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.util.ResetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.math.BigInteger;

public class ScreenConfigNext extends Screen {


    private static final ResourceLocation ARROW_ICON_PREV = new ResourceLocation(ComicsBubblesChat.MODID, "textures/left_arrow.png");
    private static final ResourceLocation ARROW_HOVER_ICON_PREV = new ResourceLocation(ComicsBubblesChat.MODID, "textures/left_arrow_hover.png");
    private static final ResourceLocation ARROW_ICON_NEXT = new ResourceLocation(ComicsBubblesChat.MODID, "textures/right_arrow.png");
    private static final ResourceLocation ARROW_HOVER_ICON_NEXT = new ResourceLocation(ComicsBubblesChat.MODID, "textures/right_arrow_hover.png");

    private Button buttonSaveMaxStack;
    private Button buttonSaveMaxLine;

    private TextFieldWidget bubblesStackField;
    private TextFieldWidget bubblesLineWidthField;
    private int guiLeft;
    private int guiTop;
    private boolean buttonPrevHover;
    private boolean buttonNextHover;

    protected ScreenConfigNext() {
        super(new TranslationTextComponent("text.config.title"));
    }

    @Override
    public void init() {
        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;


        ITextComponent resetColorDuration = new TranslationTextComponent("text.config.resetcolorduration");
        ITextComponent clearBubbles = new TranslationTextComponent("text.config.clearbubbles");
        ITextComponent resetStack = new TranslationTextComponent("text.config.resetstack");
        ITextComponent resetLineWidth = new TranslationTextComponent("text.config.resetlinewidth");

        Button buttonResetColorDuration = new Button(guiLeft - 100, guiTop / 2 - 35, 200, 20, resetColorDuration, p_onPress_1_ -> {
            ResetUtil.resetColors();
            ResetUtil.resetDuration();
        });

        Button buttonResetStack = new Button(guiLeft - 100, guiTop / 2 - 15, 200, 20, resetStack, p_onPress_1_ -> {
            BubblesConfig.CLIENT.maxBubblesStack.set(0);
            bubblesStackField.setValue("0");
            buttonSaveMaxStack.active = false;
        });
        Button buttonResetLineWidth = new Button(guiLeft - 100, guiTop / 2 + 5, 200, 20, resetLineWidth, p_onPress_1_ -> {
            BubblesConfig.CLIENT.lineWidth.set(150);
            bubblesLineWidthField.setValue("150");
            bubblesLineWidthField.active = false;
        });

        Button buttonClearBubbles = new Button(guiLeft - 100, guiTop / 2 + 25, 200, 20, clearBubbles, p_onPress_1_ -> ResetUtil.clearBubbles());

        buttonSaveMaxStack = new Button(guiLeft + 104, guiTop / 2 + 58, 20, 20, new KeybindTextComponent(TextFormatting.GREEN + "✔"), p_onPress_1_ -> {
            BubblesConfig.CLIENT.maxBubblesStack.set(Integer.parseInt(bubblesStackField.getValue()));
            buttonSaveMaxStack.active = false;
        });
        buttonSaveMaxLine = new Button(guiLeft + 104, guiTop / 2 + 98, 20, 20, new KeybindTextComponent(TextFormatting.GREEN + "✔"), p_onPress_1_ -> {
            BubblesConfig.CLIENT.lineWidth.set(Integer.parseInt(bubblesLineWidthField.getValue()));
            buttonSaveMaxLine.active = false;
        });

        bubblesStackField = new TextFieldWidget(font,guiLeft - 99, guiTop / 2 + 58, 198, 20, ITextComponent.nullToEmpty(null));
        bubblesStackField.setFilter(this::isNumeric);
        bubblesStackField.setValue(String.valueOf(BubblesConfig.CLIENT.maxBubblesStack.get()));

        bubblesLineWidthField = new TextFieldWidget(font,guiLeft - 99, guiTop / 2 + 98, 198, 20, ITextComponent.nullToEmpty(null));
        bubblesLineWidthField.setFilter(this::isNumeric);
        bubblesLineWidthField.setValue(String.valueOf(BubblesConfig.CLIENT.lineWidth.get()));


        if(!ClientBubblesUtil.serverSupport) {
            addButton(new CheckboxButton(guiLeft - 99, guiTop / 2 + 120, 100, 20, new TranslationTextComponent("text.config.forceformatchat"), BubblesConfig.CLIENT.forceFormatChat.get()) {

                @Override
                public void onPress() {
                    BubblesConfig.CLIENT.forceFormatChat.set(!selected());
                    super.onPress();
                }
            });

            addButton(new CheckboxButton(guiLeft - 99, guiTop / 2 + 140, 100, 20, new TranslationTextComponent("text.config.enablebubble"), BubblesConfig.CLIENT.enableBubbles.get()) {

                @Override
                public void onPress() {
                    BubblesConfig.CLIENT.enableBubbles.set(!selected());
                    super.onPress();
                }
            });

            addButton(new CheckboxButton(guiLeft - 99, guiTop / 2 + 160, 300, 20, new TranslationTextComponent("text.config.enablethroughblock"), BubblesConfig.CLIENT.canThroughBlocks.get()) {
                @Override
                public void onPress() {
                    BubblesConfig.CLIENT.canThroughBlocks.set(!selected());
                    super.onPress();
                }
            });
        }

        addButton(buttonResetColorDuration);
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float p_230430_4_) {
        renderBackground(stack);
        int xButtonPrev = 0;
        int yButtonPrev = height - 30;

        buttonPrevHover = mouseX >= xButtonPrev && mouseY >= yButtonPrev && mouseX < xButtonPrev + 30  && mouseY < yButtonPrev + 30;

        if(buttonPrevHover)
        {
            minecraft.getTextureManager().bind(ARROW_HOVER_ICON_PREV);
        }

        else
        {
            minecraft.getTextureManager().bind(ARROW_ICON_PREV);
        }

        blit(stack, xButtonPrev, yButtonPrev, 10, 0F, 0F, 32, 32, 32, 32);


        int xButtonNext = this.width - 30;
        int yButtonNext = guiTop * 2 - 30;
        buttonNextHover = mouseX >= xButtonNext && mouseY >= yButtonNext && mouseX < xButtonNext + 30 && mouseY < yButtonNext + 30;

        if(buttonNextHover)
        {
            minecraft.getTextureManager().bind(ARROW_HOVER_ICON_NEXT);
        }

        else
        {
            minecraft.getTextureManager().bind(ARROW_ICON_NEXT);
        }


        blit(stack, xButtonNext, yButtonNext, 10, 0F, 0F, 32, 32, 32, 32);

        String title = I18n.get("text.config.title");
        drawString(stack, font, TextFormatting.UNDERLINE + title + " 2/3", guiLeft - font.width(title) / 2 - 10, guiTop / 2 - 50, Color.WHITE.getRGB());

        String maxBubblesText = I18n.get("text.config.maxstackbubbles");
        drawString(stack, font, maxBubblesText, guiLeft - font.width(maxBubblesText) / 2, guiTop / 2 + 48, Color.WHITE.getRGB());

        String lengthLineMax = I18n.get("text.config.maxlinewidth");
        drawString(stack, font, lengthLineMax, guiLeft - font.width(lengthLineMax) / 2, guiTop / 2 + 88, Color.WHITE.getRGB());

        bubblesStackField.render(stack, mouseX, mouseY, p_230430_4_);
        bubblesLineWidthField.render(stack, mouseX, mouseY, p_230430_4_);
        super.render(stack, mouseX, mouseY, p_230430_4_);
    }





    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(buttonPrevHover) {
            Minecraft.getInstance().setScreen(new ScreenConfig());
            Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        if(buttonNextHover) {
            Minecraft.getInstance().setScreen(new ScreenConfigBox());
            Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        if(mouseX >= bubblesStackField.x && mouseY >= bubblesStackField.y && mouseX < bubblesStackField.x + 198  && mouseY < bubblesStackField.y + 20) {
            bubblesStackField.setFocus(true);
            bubblesLineWidthField.setFocus(false);
        }

        if(mouseX >= bubblesLineWidthField.x && mouseY >= bubblesLineWidthField.y && mouseX < bubblesLineWidthField.x + 198  && mouseY < bubblesLineWidthField.y + 20) {
            bubblesLineWidthField.setFocus(true);
            bubblesStackField.setFocus(false);
        }
        return super.mouseClicked(mouseX,mouseY,mouseButton);
    }


    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (bubblesStackField.isFocused()) {
            bubblesStackField.charTyped(p_charTyped_1_, p_charTyped_2_);
            String text = bubblesStackField.getValue();
            buttonSaveMaxStack.active = !text.isEmpty() && !bubblesStackField.getValue().equalsIgnoreCase(String.valueOf(BubblesConfig.CLIENT.maxBubblesStack.get()));

        }

        if (bubblesLineWidthField.isFocused()) {
            bubblesLineWidthField.charTyped(p_charTyped_1_, p_charTyped_2_);
            String text = bubblesLineWidthField.getValue();
            buttonSaveMaxLine.active = !text.isEmpty() && Integer.parseInt(text) >= 50 && !bubblesLineWidthField.getValue().equalsIgnoreCase(String.valueOf(BubblesConfig.CLIENT.lineWidth.get()));
        }
        return super.charTyped(p_charTyped_1_, p_charTyped_2_);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if(keyCode == GLFW.GLFW_KEY_ESCAPE) return super.keyPressed(keyCode, scanCode, modifiers);

        if(keyCode != GLFW.GLFW_KEY_BACKSPACE) return true;

        if (bubblesStackField.isFocused()) {
            bubblesStackField.keyPressed(keyCode, scanCode, modifiers);
            String text = bubblesStackField.getValue();
            buttonSaveMaxStack.active = !text.isEmpty() && !bubblesStackField.getValue().equalsIgnoreCase(String.valueOf(BubblesConfig.CLIENT.maxBubblesStack.get()));

        }

        if (bubblesLineWidthField.isFocused()) {
            String text = bubblesLineWidthField.getValue();
            buttonSaveMaxLine.active = !text.isEmpty() && Integer.parseInt(text) >= 50 && !bubblesLineWidthField.getValue().equalsIgnoreCase(String.valueOf(BubblesConfig.CLIENT.lineWidth.get()));
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

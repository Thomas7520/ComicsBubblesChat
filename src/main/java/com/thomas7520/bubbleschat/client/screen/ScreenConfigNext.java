package com.thomas7520.bubbleschat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.util.ResetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.math.BigInteger;

public class ScreenConfigNext extends Screen {


    private static final ResourceLocation ARROW_ICON_NEXT = new ResourceLocation(ComicsBubblesChat.MODID, "textures/left_arrow.png");
    private static final ResourceLocation ARROW_HOVER_ICON_NEXT = new ResourceLocation(ComicsBubblesChat.MODID, "textures/left_arrow_hover.png");

    private net.minecraft.client.gui.components.Button buttonSaveMaxStack;
    private net.minecraft.client.gui.components.Button buttonSaveMaxLine;

    private EditBox bubblesStackField;
    private EditBox bubblesLineWidthField;
    private int guiLeft;
    private int guiTop;
    private boolean buttonPrevHover;

    protected ScreenConfigNext() {
        super(new TranslatableComponent("text.config.title"));
    }

    @Override
    public void init() {
        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;


        Component resetColorDuration = new TranslatableComponent("text.config.resetcolorduration");
        Component clearBubbles = new TranslatableComponent("text.config.clearbubbles");
        Component resetStack = new TranslatableComponent("text.config.resetstack");
        Component resetLineWidth = new TranslatableComponent("text.config.resetlinewidth");

        net.minecraft.client.gui.components.Button buttonResetColorDuration = new net.minecraft.client.gui.components.Button(guiLeft - 100, guiTop / 2 - 35, 200, 20, resetColorDuration, p_onPress_1_ -> {
            ResetUtil.resetColors();
            ResetUtil.resetDuration();
        });

        net.minecraft.client.gui.components.Button buttonResetStack = new net.minecraft.client.gui.components.Button(guiLeft - 100, guiTop / 2 - 15, 200, 20, resetStack, p_onPress_1_ -> {
            BubblesConfig.CLIENT.maxBubblesStack.set(0);
            bubblesStackField.setValue("0");
            buttonSaveMaxStack.active = false;
        });
        net.minecraft.client.gui.components.Button buttonResetLineWidth = new net.minecraft.client.gui.components.Button(guiLeft - 100, guiTop / 2 + 5, 200, 20, resetLineWidth, p_onPress_1_ -> {
            BubblesConfig.CLIENT.lineWidth.set(150);
            bubblesLineWidthField.setValue("150");
            bubblesLineWidthField.active = false;
        });

        net.minecraft.client.gui.components.Button buttonClearBubbles = new net.minecraft.client.gui.components.Button(guiLeft - 100, guiTop / 2 + 25, 200, 20, clearBubbles, p_onPress_1_ -> ResetUtil.clearBubbles());

        buttonSaveMaxStack = new net.minecraft.client.gui.components.Button(guiLeft + 104, guiTop / 2 + 58, 20, 20, new TranslatableComponent(ChatFormatting.GREEN + "✔"), p_onPress_1_ -> {
            BubblesConfig.CLIENT.maxBubblesStack.set(Integer.parseInt(bubblesStackField.getValue()));
            buttonSaveMaxStack.active = false;
        });
        buttonSaveMaxLine = new net.minecraft.client.gui.components.Button(guiLeft + 104, guiTop / 2 + 98, 20, 20, new TextComponent(ChatFormatting.GREEN + "✔"), p_onPress_1_ -> {
            BubblesConfig.CLIENT.lineWidth.set(Integer.parseInt(bubblesLineWidthField.getValue()));
            buttonSaveMaxLine.active = false;
        });

        bubblesStackField = new EditBox(font,guiLeft - 99, guiTop / 2 + 58, 198, 20, Component.nullToEmpty(null));
        bubblesStackField.setFilter(this::isNumeric);
        bubblesStackField.setValue(String.valueOf(BubblesConfig.CLIENT.maxBubblesStack.get()));

        bubblesLineWidthField = new EditBox(font,guiLeft - 99, guiTop / 2 + 98, 198, 20, Component.nullToEmpty(null));
        bubblesLineWidthField.setFilter(this::isNumeric);
        bubblesLineWidthField.setValue(String.valueOf(BubblesConfig.CLIENT.lineWidth.get()));


        if(!ClientBubblesUtil.serverSupport) {
            addRenderableWidget(new net.minecraft.client.gui.components.Checkbox(guiLeft - 99, guiTop / 2 + 120, 100, 20, new TranslatableComponent("text.config.forceformatchat"), BubblesConfig.CLIENT.forceFormatChat.get()) {

                @Override
                public void onPress() {
                    BubblesConfig.CLIENT.forceFormatChat.set(!selected());
                    super.onPress();
                }
            });

            addRenderableWidget(new net.minecraft.client.gui.components.Checkbox(guiLeft - 99, guiTop / 2 + 140, 100, 20, new TranslatableComponent("text.config.enablebubble"), BubblesConfig.CLIENT.enableBubbles.get()) {

                @Override
                public void onPress() {
                    BubblesConfig.CLIENT.enableBubbles.set(!selected());
                    super.onPress();
                }
            });

            addRenderableWidget(new net.minecraft.client.gui.components.Checkbox(guiLeft - 99, guiTop / 2 + 160, 300, 20, new TranslatableComponent("text.config.enablethroughblock"), BubblesConfig.CLIENT.canThroughBlocks.get()) {
                @Override
                public void onPress() {
                    BubblesConfig.CLIENT.canThroughBlocks.set(!selected());
                    super.onPress();
                }
            });
        }

        addRenderableWidget(buttonResetColorDuration);
        addRenderableWidget(buttonClearBubbles);
        addRenderableWidget(buttonResetStack);
        addRenderableWidget(buttonResetLineWidth);
        addRenderableWidget(buttonSaveMaxStack);
        addRenderableWidget(buttonSaveMaxLine);
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
    public void render(PoseStack stack, int mouseX, int mouseY, float p_230430_4_) {
        renderBackground(stack);
        int xButtonPrev = 0;
        int yButtonPrev = height - 30;

        buttonPrevHover = mouseX >= xButtonPrev && mouseY >= yButtonPrev && mouseX < xButtonPrev + 30  && mouseY < yButtonPrev + 30;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if(buttonPrevHover)
        {
            RenderSystem.setShaderTexture(0, ARROW_HOVER_ICON_NEXT);
        }

        else
        {
            RenderSystem.setShaderTexture(0, ARROW_ICON_NEXT);
        }


        blit(stack, xButtonPrev, yButtonPrev, 10, 0F, 0F, 32, 32, 32, 32);

        String title = I18n.get("text.config.title");
        drawString(stack, font, ChatFormatting.UNDERLINE + title + " 2/2", guiLeft - font.width(title) / 2 - 10, guiTop / 2 - 50, Color.WHITE.getRGB());

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
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
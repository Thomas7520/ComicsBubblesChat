package com.thomas7520.bubbleschat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.util.ResetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.ForgeHooksClient;
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
        super(Component.translatable("text.config.title"));
        
    }

    @Override
    public void init() {
        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;


        Component resetColorDuration = Component.translatable("text.config.resetcolorduration");
        Component clearBubbles = Component.translatable("text.config.clearbubbles");
        Component resetStack = Component.translatable("text.config.resetstack");
        Component resetLineWidth = Component.translatable("text.config.resetlinewidth");

        Button buttonResetColorDuration = Button.builder(resetColorDuration, p_93751_ -> {
            ResetUtil.resetColors();
            ResetUtil.resetDuration();
        })
                .bounds(guiLeft - 100, guiTop / 2 - 35, 200, 20)
                .build();


        Button buttonResetStack = Button.builder(resetStack, p_93751_ -> {
            BubblesConfig.CLIENT.maxBubblesStack.set(0);
            bubblesStackField.setValue("0");
            buttonSaveMaxStack.active = false;
        })
                .bounds(guiLeft - 100, guiTop / 2 - 15, 200, 20)
                .build();

        Button buttonResetLineWidth = Button.builder(resetLineWidth, p_93751_ -> {
            BubblesConfig.CLIENT.lineWidth.set(150);
            bubblesLineWidthField.setValue("150");
            bubblesLineWidthField.active = false;
        })
                .bounds(guiLeft - 100, guiTop / 2 + 5, 200, 20)
                .build();


        Button buttonClearBubbles = Button.builder(clearBubbles, p_93751_ -> ResetUtil.clearBubbles())
                .bounds(guiLeft - 100, guiTop / 2 + 25, 200, 20)
                .build();



        buttonSaveMaxStack = Button.builder(Component.literal(ChatFormatting.GREEN + "✔"), p_93751_ -> {
            BubblesConfig.CLIENT.maxBubblesStack.set(Integer.parseInt(bubblesStackField.getValue()));
            buttonSaveMaxStack.active = false;
        })
                .bounds(guiLeft + 104, guiTop / 2 + 58, 20, 20)
                .build();


        buttonSaveMaxLine = Button.builder(Component.literal(ChatFormatting.GREEN + "✔"), p_93751_ -> {
            BubblesConfig.CLIENT.lineWidth.set(Integer.parseInt(bubblesLineWidthField.getValue()));
            buttonSaveMaxLine.active = false;
        })
                .bounds(guiLeft + 104, guiTop / 2 + 98, 20, 20)
                .build();


        bubblesStackField = new EditBox(font,guiLeft - 99, guiTop / 2 + 58, 198, 20, Component.nullToEmpty(null));
        bubblesStackField.setFilter(this::isNumeric);
        bubblesStackField.setValue(String.valueOf(BubblesConfig.CLIENT.maxBubblesStack.get()));

        bubblesLineWidthField = new EditBox(font,guiLeft - 99, guiTop / 2 + 98, 198, 20, Component.nullToEmpty(null));
        bubblesLineWidthField.setFilter(this::isNumeric);
        bubblesLineWidthField.setValue(String.valueOf(BubblesConfig.CLIENT.lineWidth.get()));


        if(!ClientBubblesUtil.serverSupport) {
            addRenderableWidget(new net.minecraft.client.gui.components.Checkbox(guiLeft - 99, guiTop / 2 + 120, 100, 20, Component.translatable("text.config.forceformatchat"), BubblesConfig.CLIENT.forceFormatChat.get()) {

                @Override
                public void onPress() {
                    BubblesConfig.CLIENT.forceFormatChat.set(!selected());
                    super.onPress();
                }
            });

            addRenderableWidget(new net.minecraft.client.gui.components.Checkbox(guiLeft - 99, guiTop / 2 + 140, 100, 20, Component.translatable("text.config.enablebubble"), BubblesConfig.CLIENT.enableBubbles.get()) {

                @Override
                public void onPress() {
                    BubblesConfig.CLIENT.enableBubbles.set(!selected());
                    super.onPress();
                }
            });

            addRenderableWidget(new net.minecraft.client.gui.components.Checkbox(guiLeft - 99, guiTop / 2 + 160, 300, 20, Component.translatable("text.config.enablethroughblock"), BubblesConfig.CLIENT.canThroughBlocks.get()) {
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float p_230430_4_) {
        renderBackground(graphics);
        int xButtonPrev = 0;
        int yButtonPrev = height - 30;

        buttonPrevHover = mouseX >= xButtonPrev && mouseY >= yButtonPrev && mouseX < xButtonPrev + 30  && mouseY < yButtonPrev + 30;

//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        if(buttonPrevHover)
//        {
//            RenderSystem.setShaderTexture(0, ARROW_HOVER_ICON_NEXT);
//        }
//
//        else
//        {
//            RenderSystem.setShaderTexture(0, ARROW_ICON_NEXT);
//        }


        graphics.blit(buttonPrevHover ? ARROW_HOVER_ICON_NEXT : ARROW_ICON_NEXT, xButtonPrev, yButtonPrev, 10, 0F, 0F, 32, 32, 32, 32);

        String title = I18n.get("text.config.title");
        graphics.drawString(font, ChatFormatting.UNDERLINE + title + " 2/2", guiLeft - font.width(title) / 2 - 10, guiTop / 2 - 50, Color.WHITE.getRGB());

        String maxBubblesText = I18n.get("text.config.maxstackbubbles");
        graphics.drawString(font, maxBubblesText, guiLeft - font.width(maxBubblesText) / 2, guiTop / 2 + 48, Color.WHITE.getRGB());

        String lengthLineMax = I18n.get("text.config.maxlinewidth");
        graphics.drawString(font, lengthLineMax, guiLeft - font.width(lengthLineMax) / 2, guiTop / 2 + 88, Color.WHITE.getRGB());

        bubblesStackField.render(graphics, mouseX, mouseY, p_230430_4_);
        bubblesLineWidthField.render(graphics, mouseX, mouseY, p_230430_4_);
        super.render(graphics, mouseX, mouseY, p_230430_4_);
    }





    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(buttonPrevHover) {
            Minecraft.getInstance().setScreen(new ScreenConfig());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        if(mouseX >= bubblesStackField.getX() && mouseY >= bubblesStackField.getY() && mouseX < bubblesStackField.getX() + 198  && mouseY < bubblesStackField.getY() + 20) {
            bubblesStackField.setFocused(true);
            bubblesLineWidthField.setFocused(false);
        }

        if(mouseX >= bubblesLineWidthField.getX() && mouseY >= bubblesLineWidthField.getY() && mouseX < bubblesLineWidthField.getX() + 198  && mouseY < bubblesLineWidthField.getY() + 20) {
            bubblesLineWidthField.setFocused(true);
            bubblesStackField.setFocused(false);
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

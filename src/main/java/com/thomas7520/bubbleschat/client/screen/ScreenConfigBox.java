package com.thomas7520.bubbleschat.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;

public class ScreenConfigBox extends Screen {


    private static final ResourceLocation ARROW_ICON_NEXT = new ResourceLocation(ComicsBubblesChat.MODID, "textures/left_arrow.png");
    private static final ResourceLocation ARROW_HOVER_ICON_NEXT = new ResourceLocation(ComicsBubblesChat.MODID, "textures/left_arrow_hover.png");

    private int guiLeft;
    private int guiTop;
    private boolean buttonPrevHover;

    protected ScreenConfigBox() {
        super(new TranslationTextComponent("text.config.title"));
    }

    @Override
    public void init() {
        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;


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

        super.init();
    }



    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float p_230430_4_) {
        renderBackground(stack);
        int xButtonPrev = 0;
        int yButtonPrev = height - 30;

        buttonPrevHover = mouseX >= xButtonPrev && mouseY >= yButtonPrev && mouseX < xButtonPrev + 30  && mouseY < yButtonPrev + 30;

        if(buttonPrevHover)
        {
            minecraft.getTextureManager().bind(ARROW_HOVER_ICON_NEXT);
        }

        else
        {
            minecraft.getTextureManager().bind(ARROW_ICON_NEXT);
        }


        blit(stack, xButtonPrev, yButtonPrev, 10, 0F, 0F, 32, 32, 32, 32);

        String title = I18n.get("text.config.title");
        drawString(stack, font, TextFormatting.UNDERLINE + title + " 3/3", guiLeft - font.width(title) / 2 - 10, guiTop / 2 - 50, Color.WHITE.getRGB());


        super.render(stack, mouseX, mouseY, p_230430_4_);
    }





    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(buttonPrevHover) {
            Minecraft.getInstance().setScreen(new ScreenConfigNext());
            Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        return super.mouseClicked(mouseX,mouseY,mouseButton);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

package com.thomas7520.bubbleschat.client.screen;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.util.SpecColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;


public class ScreenConfig extends Screen {


    private static final ResourceLocation ARROW_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/right_arrow.png");
    private static final ResourceLocation ARROW_HOVER_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/right_arrow_hover.png");

    private final SpecColor outlineColorSpec = new SpecColor(BubblesConfig.colorOutline.get());
    private final SpecColor insideColorSpec = new SpecColor(BubblesConfig.colorInside.get());
    private final SpecColor textColorSpec = new SpecColor(BubblesConfig.colorText.get());


    private final String[] stateValues = {I18n.format("text.red"), I18n.format("text.green"), I18n.format("text.blue"), I18n.format("text.alpha")};

    private int guiLeft;
    private int guiTop;

    private boolean buttonNextHover;

    public ScreenConfig() {
        super(new TranslationTextComponent("text.config.title"));
    }
    

    @Override
    public void init() {
        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;

        initSliders();

        super.init();
    }



    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();

        int xButton = this.width - 30;
        int yButton = guiTop * 2 - 30;
        buttonNextHover = mouseX >= xButton && mouseY >= yButton && mouseX < xButton + 30 && mouseY < yButton + 30;

        if(buttonNextHover)
        {
            minecraft.getTextureManager().bindTexture(ARROW_HOVER_ICON);
        }

        else
        {
            minecraft.getTextureManager().bindTexture(ARROW_ICON);
        }


        blit(xButton, yButton, 10, 0F, 0F, 32, 32, 32, 32);

        Color outlineColor = outlineColorSpec.getColor();
        Color insideColor = insideColorSpec.getColor();
        Color textColor = textColorSpec.getColor();

        drawString(font, ChatFormatting.UNDERLINE + this.title.getString() + " 1/2", guiLeft - font.getStringWidth(title.getString()) / 2 - 10, guiTop / 2 - 50, Color.WHITE.getRGB());

        GL11.glPushMatrix();
        GL11.glScalef(0.95f,0.95f,0.95f);

        String outlineTitle = I18n.format("text.config.outline");
        drawString(font, ChatFormatting.UNDERLINE + outlineTitle, guiLeft - font.getStringWidth(outlineTitle) / 2 - 140, guiTop / 2 + 20, Color.WHITE.getRGB());

        String insideTitle = I18n.format("text.config.inside");
        drawString(font, ChatFormatting.UNDERLINE + insideTitle, guiLeft -font.getStringWidth(insideTitle) / 2 + 10, guiTop / 2 + 20, Color.WHITE.getRGB());

        String textTitle = I18n.format("text.config.text");
        drawString(font, ChatFormatting.UNDERLINE + textTitle, guiLeft -font.getStringWidth(textTitle) / 2 + 160, guiTop / 2 + 20, Color.WHITE.getRGB());

        GL11.glPopMatrix();


        ClientBubblesUtil.drawRect(guiLeft - 210, guiTop / 2 + 130, guiLeft - 100 + 25, guiTop / 2 + 140, outlineColor.getRGB());
        ClientBubblesUtil.drawRect(guiLeft - 70, guiTop / 2 + 130, guiLeft + 20 + 45, guiTop / 2 + 140, insideColor.getRGB());
        ClientBubblesUtil.drawRect(guiLeft + 40 + 30, guiTop / 2 + 130, guiLeft + 140 + 65, guiTop / 2 + 140, textColor.getRGB());

        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft, guiTop / 2f - 30f, 0.0f);
        GL11.glScalef(0.9f,0.9f,0.9f);

        ClientBubblesUtil.drawBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB());
        ClientBubblesUtil.drawMediumBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB());
        ClientBubblesUtil.drawLittleBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB());

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        font.drawString(I18n.format("text.config.test"), -font.getStringWidth(I18n.format("text.config.test")) / 2f, 0, textColor.getRGB());

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glTranslatef(0.0f, 0.0f, 0.0f);
        GL11.glPopMatrix();

        if(!ClientBubblesUtil.serverSupport && !Minecraft.getInstance().isSingleplayer()) {
            String noLinked = I18n.format("text.config.nolinked");
            font.drawString(noLinked, guiLeft - font.getStringWidth(noLinked) / 2f, guiTop * 2 - 10, Color.RED.getRGB());
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    private void initSliders() {

        final Integer[] outsideValues = outlineColorSpec.getValues();
        final Integer[] insideValues = insideColorSpec.getValues();
        final Integer[] textValues = textColorSpec.getValues();

        for (int i = 0; i < 4; i++) {
            int finalI = i;
            addButton(new GuiSlider(guiLeft - 170 - 40, guiTop / 2 + 40 + finalI * 20, 135, 20,  stateValues[finalI] + " : ", "", 0.0, 255.0, outlineColorSpec.getValues()[finalI], false, true, p_onPress_1_ -> {}) {

                @Override
                protected void onDrag(double p_onDrag_1_, double p_onDrag_3_, double p_onDrag_5_, double p_onDrag_7_) {
                    outsideValues[finalI] = getValueInt();
                    outlineColorSpec.setValues(outsideValues);
                    super.onDrag(p_onDrag_1_, p_onDrag_3_, p_onDrag_5_, p_onDrag_7_);
                }


                @Override
                public void onRelease(double mouseX, double mouseY) {
                    outsideValues[finalI] = getValueInt();
                    outlineColorSpec.setValues(outsideValues);
                    BubblesConfig.colorOutline.set(Arrays.asList(outsideValues));
                    super.onRelease(mouseX, mouseY);
                }
            });

            addButton(new GuiSlider(guiLeft - 170 + 100, guiTop / 2 + 40 + i * 20, 135, 20,  stateValues[i] + " : ", "", 0.0, 255.0, insideColorSpec.getValues()[finalI], false, true, p_onPress_1_ -> {}) {

                @Override
                protected void onDrag(double p_onDrag_1_, double p_onDrag_3_, double p_onDrag_5_, double p_onDrag_7_) {
                    insideValues[finalI] = getValueInt();
                    insideColorSpec.setValues(insideValues);
                    super.onDrag(p_onDrag_1_, p_onDrag_3_, p_onDrag_5_, p_onDrag_7_);
                }

                @Override
                public void onRelease(double mouseX, double mouseY) {
                    setDragging(false);
                    insideValues[finalI] = getValueInt();
                    insideColorSpec.setValues(insideValues);
                    BubblesConfig.colorInside.set(Arrays.asList(insideValues));
                    super.onRelease(mouseX, mouseY);
                }
            });

            addButton(new GuiSlider(guiLeft - 170 + 240, guiTop / 2 + 40 + i * 20, 135, 20,  stateValues[i] + " : ", "", 0.0, 255.0, textColorSpec.getValues()[finalI], false, true, p_onPress_1_ -> {}) {

                @Override
                protected void onDrag(double p_onDrag_1_, double p_onDrag_3_, double p_onDrag_5_, double p_onDrag_7_) {
                    textValues[finalI] = getValueInt();
                    textColorSpec.setValues(textValues);
                    super.onDrag(p_onDrag_1_, p_onDrag_3_, p_onDrag_5_, p_onDrag_7_);
                }

                @Override
                public void onRelease(double mouseX, double mouseY) {
                    textValues[finalI] = getValueInt();
                    textColorSpec.setValues(textValues);
                    BubblesConfig.colorText.set(Arrays.asList(textValues));
                    super.onRelease(mouseX, mouseY);
                }
            });
        }

        addButton(new GuiSlider(guiLeft - 170 - 40, guiTop / 2 + 145, 415, 15, I18n.format("text.config.duration.prefix") + " : ", " " + I18n.format("text.config.duration.suffix"), 0, 60, BubblesConfig.durationBubbles.get(), false, true, p_onPress_1_ -> {}) {

            @Override
            public void onRelease(double mouseX, double mouseY) {
                BubblesConfig.durationBubbles.set(getValueInt());
                super.onRelease(mouseX, mouseY);
            }
        });
    }

    @Override
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        buttons.stream().filter(widget -> widget instanceof GuiSlider)
                .filter(widget -> ((GuiSlider) widget).dragging)
                .forEach(widget -> ((GuiSlider) widget).dragging = false);
        return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
    }




    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (buttonNextHover) {
            Minecraft.getInstance().displayGuiScreen(new ScreenConfigNext());
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

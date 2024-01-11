package com.thomas7520.bubbleschat.client.screen;

import com.google.common.collect.Lists;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.util.SpecColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class ScreenConfig extends Screen {


    private static final ResourceLocation ARROW_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/right_arrow.png");
    private static final ResourceLocation ARROW_HOVER_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/right_arrow_hover.png");

    private SpecColor outlineColorSpec;
    private SpecColor insideColorSpec;
    private SpecColor textColorSpec;


    private final String[] stateValues = {I18n.format("text.red"), I18n.format("text.green"), I18n.format("text.blue"), I18n.format("text.alpha")};

    private int guiLeft;
    private int guiTop;

    private boolean buttonNextHover;

    private Integer[] outlineValues;
    private Integer[] insideValues;
    private Integer[] textValues;
    private int durationBubbles;

    public ScreenConfig() {
        super(new TranslationTextComponent("text.config.title"));
    }


    @Override
    public void init() {
        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;

        outlineColorSpec = new SpecColor(BubblesConfig.colorOutline.get());
        insideColorSpec = new SpecColor(BubblesConfig.colorInside.get());
        textColorSpec = new SpecColor(BubblesConfig.colorText.get());
        durationBubbles = BubblesConfig.durationBubbles.get();

        initSliders();
        super.init();
    }


    @Override
    public void render(int mouseX, int mouseY, float p_230430_4_) {
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

        Color outlineColor = new SpecColor(outlineValues).getColor();
        Color insideColor = new SpecColor(insideValues).getColor();
        Color textColor = new SpecColor(textValues).getColor();

        drawString(font, TextFormatting.UNDERLINE + this.title.getString() + " 1/2", guiLeft - font.getStringWidth(title.getString()) / 2 - 10, guiTop / 2 - 50, Color.WHITE.getRGB());

        GL11.glPushMatrix();
        GL11.glScalef(0.95f,0.95f,0.95f);

        String outlineTitle = I18n.format("text.config.outline");
        drawString(font, TextFormatting.UNDERLINE + outlineTitle, guiLeft - font.getStringWidth(outlineTitle) / 2 - 140, guiTop / 2 + 20, Color.WHITE.getRGB());

        String insideTitle = I18n.format("text.config.inside");
        drawString(font, TextFormatting.UNDERLINE + insideTitle, guiLeft -font.getStringWidth(insideTitle) / 2 + 10, guiTop / 2 + 20, Color.WHITE.getRGB());

        String textTitle = I18n.format("text.config.text");
        drawString(font, TextFormatting.UNDERLINE + textTitle, guiLeft -font.getStringWidth(textTitle) / 2 + 160, guiTop / 2 + 20, Color.WHITE.getRGB());

        GL11.glPopMatrix();


        fill(TransformationMatrix.identity().getMatrix(), guiLeft - 210, guiTop / 2 + 130, guiLeft - 100 + 25, guiTop / 2 + 140, outlineColor.getRGB());
        fill(TransformationMatrix.identity().getMatrix(), guiLeft - 70, guiTop / 2 + 130, guiLeft + 20 + 45, guiTop / 2 + 140, insideColor.getRGB());
        fill(TransformationMatrix.identity().getMatrix(), guiLeft + 40 + 30, guiTop / 2 + 130, guiLeft + 140 + 65, guiTop / 2 + 140, textColor.getRGB());

        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft, guiTop / 2f - 30f, 0.0f);
        GL11.glScalef(0.9f,0.9f,0.9f);

        ClientBubblesUtil.drawBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB(), TransformationMatrix.identity().getMatrix());
        ClientBubblesUtil.drawMediumBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB(), TransformationMatrix.identity().getMatrix());
        ClientBubblesUtil.drawLittleBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB(), TransformationMatrix.identity().getMatrix());

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        font.drawString(I18n.format("text.config.test"), (int) (-font.getStringWidth(I18n.format("text.config.test")) / 2f), 0, textColor.getRGB());

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glTranslatef(0.0f, 0.0f, 0.0f);
        GL11.glPopMatrix();

        if(!ClientBubblesUtil.serverSupport && !Minecraft.getInstance().isSingleplayer()) {
            String noLinked = I18n.format("text.config.nolinked");
            font.drawString(noLinked, (int) (guiLeft - font.getStringWidth(noLinked) / 2f), guiTop * 2 - 10, Color.RED.getRGB());
        }
        super.render(mouseX, mouseY, p_230430_4_);
    }

    private void initSliders() {

        outlineValues = outlineColorSpec.getValues();
        insideValues = insideColorSpec.getValues();
        textValues = textColorSpec.getValues();


        for (int i = 0; i < 4; i++) {
            int finalI = i;

            SliderPercentageOption outlineSlider = new SliderPercentageOption("outlineslider" + i, 0.0d, 255d, 1.0f, gameSettings ->
                    Double.valueOf(outlineValues[finalI]),
                    (gameSettings, aDouble) -> outlineValues[finalI] = (int) Math.round(aDouble), (gameSettings, sliderPercentageOption) -> {
                int value = (int) Math.round(sliderPercentageOption.get(gameSettings));
                outlineValues[finalI] = value;
                return stateValues[finalI] + " : " + Math.round(sliderPercentageOption.get(gameSettings));
            });

            addButton(outlineSlider.createWidget(Minecraft.getInstance().gameSettings, guiLeft - 170 - 40, guiTop / 2 + 40 + finalI * 20, 135));


            SliderPercentageOption insideSlider = new SliderPercentageOption("insideslider" + i, 0.0d, 255d, 1.0f, gameSettings ->
                    Double.valueOf(insideValues[finalI]),
                    (gameSettings, aDouble) -> insideValues[finalI] = (int) Math.round(aDouble), (gameSettings, sliderPercentageOption) -> {
                int value = (int) Math.round(sliderPercentageOption.get(gameSettings));
                insideValues[finalI] = value;
                return stateValues[finalI] + " : " + Math.round(sliderPercentageOption.get(gameSettings));
            });

            addButton(insideSlider.createWidget(Minecraft.getInstance().gameSettings, guiLeft - 170 + 100, guiTop / 2 + 40 + finalI * 20, 135));



            SliderPercentageOption textSlider = new SliderPercentageOption("textslider" + i, 0.0d, 255d, 1.0f, gameSettings ->
                    Double.valueOf(textValues[finalI]),
                    (gameSettings, aDouble) -> textValues[finalI] = (int) Math.round(aDouble),
                    (gameSettings, sliderPercentageOption) -> {
                        int value = (int) Math.round(sliderPercentageOption.get(gameSettings));
                        textValues[finalI] = value;
                        return stateValues[finalI] + " : " + Math.round(sliderPercentageOption.get(gameSettings));
                    });

            addButton(textSlider.createWidget(Minecraft.getInstance().gameSettings, guiLeft - 170 + 240, guiTop / 2 + 40 + finalI * 20, 135));

        }

        SliderPercentageOption durationSlider = new SliderPercentageOption("durationslider", 0.0d, 60.0d, 1.0f, gameSettings ->
                (double) durationBubbles,
                (gameSettings, aDouble) -> durationBubbles = (int) Math.round(aDouble),
                (gameSettings, sliderPercentageOption) -> {
                    int value = (int) Math.round(sliderPercentageOption.get(gameSettings));
                    durationBubbles = value;
                    return I18n.format("text.config.duration.prefix") + " : " + value + " " + I18n.format("text.config.duration.suffix");
                });

        addButton(durationSlider.createWidget(Minecraft.getInstance().gameSettings, guiLeft - 170 - 40 + 100, guiTop / 2 + 145, 200));
    }



    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (buttonNextHover) {
            configSave();
            Minecraft.getInstance().displayGuiScreen(new ScreenConfigNext());
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @Override
    public void onClose() {
        configSave();
        super.onClose();
    }

    @Override
    public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
        configSave();
        super.resize(p_231152_1_, p_231152_2_, p_231152_3_);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void configSave() {
        BubblesConfig.colorOutline.set(Lists.newArrayList(outlineValues));
        BubblesConfig.colorInside.set(Lists.newArrayList(insideValues));
        BubblesConfig.colorText.set(Lists.newArrayList(textValues));
        BubblesConfig.durationBubbles.set(durationBubbles);
    }
}




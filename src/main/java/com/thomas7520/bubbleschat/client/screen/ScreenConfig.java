package com.thomas7520.bubbleschat.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import com.thomas7520.bubbleschat.util.SpecColor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class ScreenConfig extends Screen {


    private static final ResourceLocation ARROW_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/right_arrow.png");
    private static final ResourceLocation ARROW_HOVER_ICON = new ResourceLocation(ComicsBubblesChat.MODID, "textures/right_arrow_hover.png");

    private SpecColor outlineColorSpec;
    private SpecColor insideColorSpec;
    private SpecColor textColorSpec;


    private final String[] stateValues = {I18n.get("text.red"), I18n.get("text.green"), I18n.get("text.blue"), I18n.get("text.alpha")};

    private int guiLeft;
    private int guiTop;

    private boolean buttonNextHover;

    private Integer[] outlineValues;
    private Integer[] insideValues;
    private Integer[] textValues;
    private int durationBubbles;
    private int sizeBubble;

    public ScreenConfig() {
        super(Component.translatable("text.config.title"));
    }
    

    @Override
    public void init() {
        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;

        outlineColorSpec = new SpecColor(BubblesConfig.CLIENT.colorOutline.get());
        insideColorSpec = new SpecColor(BubblesConfig.CLIENT.colorInside.get());
        textColorSpec = new SpecColor(BubblesConfig.CLIENT.colorText.get());
        durationBubbles = BubblesConfig.CLIENT.durationBubbles.get();
        sizeBubble = BubblesConfig.CLIENT.sizeBubble.get();
        initSliders();
        super.init();
    }



    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float p_230430_4_) {

        renderBackground(stack);
        int xButton = this.width - 30;
        int yButton = guiTop * 2 - 30;
        buttonNextHover = mouseX >= xButton && mouseY >= yButton && mouseX < xButton + 30 && mouseY < yButton + 30;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if(buttonNextHover)
        {
            RenderSystem.setShaderTexture(0, ARROW_HOVER_ICON);
        }

        else
        {
            RenderSystem.setShaderTexture(0, ARROW_ICON);
        }


        blit(stack, xButton, yButton, 10, 0F, 0F, 32, 32, 32, 32);

        Color outlineColor = new SpecColor(outlineValues).getColor();
        Color insideColor = new SpecColor(insideValues).getColor();
        Color textColor = new SpecColor(textValues).getColor();

        drawString(stack,font, ChatFormatting.UNDERLINE + this.title.getString() + " 1/2", guiLeft - font.width(title.getString()) / 2 - 10, guiTop / 2 - 50, Color.WHITE.getRGB());

        stack.pushPose();
        stack.scale(0.95f,0.95f,0.95f);

        String outlineTitle = I18n.get("text.config.outline");
        drawString(stack,font, ChatFormatting.UNDERLINE + outlineTitle, guiLeft - font.width(outlineTitle) / 2 - 140, guiTop / 2 + 20, Color.WHITE.getRGB());

        String insideTitle = I18n.get("text.config.inside");
        drawString(stack,font, ChatFormatting.UNDERLINE + insideTitle, guiLeft -font.width(insideTitle) / 2 + 10, guiTop / 2 + 20, Color.WHITE.getRGB());

        String textTitle = I18n.get("text.config.text");
        drawString(stack, font, ChatFormatting.UNDERLINE + textTitle, guiLeft -font.width(textTitle) / 2 + 160, guiTop / 2 + 20, Color.WHITE.getRGB());

        stack.popPose();


        fill(stack, guiLeft - 210, guiTop / 2 + 120, guiLeft - 100 + 25, guiTop / 2 + 130, outlineColor.getRGB());
        fill(stack, guiLeft - 70, guiTop / 2 + 120, guiLeft + 20 + 45, guiTop / 2 + 130, insideColor.getRGB());
        fill(stack, guiLeft + 40 + 30, guiTop / 2 + 120, guiLeft + 140 + 65, guiTop / 2 + 130, textColor.getRGB());

        stack.pushPose();
        stack.translate(guiLeft, guiTop / 2f - 30f, 0.0f);
        stack.scale(0.9f,0.9f,0.9f);

        ClientBubblesUtil.drawBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB(), stack, true);
        ClientBubblesUtil.drawMediumBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB(), stack, true);
        ClientBubblesUtil.drawLittleBubble(50, -10, outlineColor.getRGB(), insideColor.getRGB(), stack, true);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        font.draw(stack, I18n.get("text.config.test"), (int) (-font.width(I18n.get("text.config.test")) / 2f), 0, textColor.getRGB());

        GL11.glDisable(GL11.GL_BLEND);
        stack.translate(0.0f, 0.0f, 0.0f);
        stack.popPose();

//        if(!ClientBubblesUtil.serverSupport && !Minecraft.getInstance().hasSingleplayerServer()) {
//            String noLinked = I18n.get("text.config.nolinked");
//            font.draw(stack, noLinked, (int) (guiLeft - font.width(noLinked) / 2f), guiTop * 2 - 10, Color.RED.getRGB());
//        }
        super.render(stack, mouseX, mouseY, p_230430_4_);
    }

    private void initSliders() {
        outlineValues = outlineColorSpec.getValues();
        insideValues = insideColorSpec.getValues();
        textValues = textColorSpec.getValues();

        for (int i = 0; i < 4; i++) {
            int finalI = i;


            OptionInstance<Integer> outlineSliders = new OptionInstance<>(stateValues[finalI], OptionInstance.noTooltip(), ScreenConfig::genericValueLabel, new OptionInstance.IntRange(0, 255), outlineValues[finalI], (p_232041_) -> outlineValues[finalI] = p_232041_);
            addRenderableWidget(outlineSliders.createButton(Minecraft.getInstance().options, guiLeft - 170 - 40, guiTop / 2 + 40 + finalI * 20, 135));


            OptionInstance<Integer> insideSliders = new OptionInstance<>(stateValues[finalI], OptionInstance.noTooltip(), ScreenConfig::genericValueLabel, new OptionInstance.IntRange(0, 255), insideValues[finalI], (p_232041_) -> insideValues[finalI] = p_232041_);
            addRenderableWidget(insideSliders.createButton(Minecraft.getInstance().options, guiLeft - 170 + 100, guiTop / 2 + 40 + finalI * 20, 135));

            OptionInstance<Integer> textSliders = new OptionInstance<>(stateValues[finalI], OptionInstance.noTooltip(), ScreenConfig::genericValueLabel, new OptionInstance.IntRange(0, 255), textValues[finalI], (p_232041_) -> textValues[finalI] = p_232041_);
            addRenderableWidget(textSliders.createButton(Minecraft.getInstance().options, guiLeft - 170 + 240, guiTop / 2 + 40 + finalI * 20, 135));

        }

        OptionInstance<Integer> durationSlider = new OptionInstance<>(I18n.get("text.config.duration.prefix"), OptionInstance.noTooltip(), ScreenConfig::genericValueLabel, new OptionInstance.IntRange(0, 60), durationBubbles, (p_232041_) -> durationBubbles = p_232041_);
        addRenderableWidget(durationSlider.createButton(Minecraft.getInstance().options, guiLeft - 170 - 40 + 100, guiTop / 2 + 140, 200));

        OptionInstance<Integer> sizeBubbleSlider = new OptionInstance<>(I18n.get("text.config.sizebubble.prefix"), OptionInstance.noTooltip(), ScreenConfig::genericValueLabel, new OptionInstance.IntRange(0, 20), sizeBubble, (p_232041_) -> sizeBubble = p_232041_);
        addRenderableWidget(sizeBubbleSlider.createButton(Minecraft.getInstance().options, guiLeft - 170 - 40 + 100, guiTop / 2 + 160, 200));
    }



    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (buttonNextHover) {
            configSave();
            Minecraft.getInstance().setScreen(new ScreenConfigNext());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
        BubblesConfig.CLIENT.colorOutline.set(Lists.newArrayList(outlineValues));
        BubblesConfig.CLIENT.colorInside.set(Lists.newArrayList(insideValues));
        BubblesConfig.CLIENT.colorText.set(Lists.newArrayList(textValues));
        BubblesConfig.CLIENT.durationBubbles.set(durationBubbles);
        BubblesConfig.CLIENT.sizeBubble.set(sizeBubble);
    }

    public static Component genericValueLabel(Component p_231922_, Component p_231923_) {
        return Component.translatable("options.generic_value", p_231922_, p_231923_);
    }

    public static Component genericValueLabel(Component p_231901_, int p_231902_) {
        return genericValueLabel(p_231901_, Component.literal(Integer.toString(p_231902_)));
    }

}

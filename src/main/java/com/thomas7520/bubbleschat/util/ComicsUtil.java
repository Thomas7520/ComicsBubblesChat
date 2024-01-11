package com.thomas7520.bubbleschat.util;

import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.ComicsBubblesChat;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

public class ComicsUtil {

    public static void resetColors() {
        int[] outlineRGB = {0, 0 ,0 ,220};
        int[] insideRGB = {203, 203, 203, 220};
        int[] textRGB = {0, 0 ,0 ,220};

        for(int i = 0; i < 4; i++) {
            BubblesConfig.client.colorOutline[i] = outlineRGB[i];
            BubblesConfig.client.colorInside[i] = insideRGB[i];
            BubblesConfig.client.colorText[i] = textRGB[i];
        }

        syncFile();
    }

    public static void resetDuration() {
        BubblesConfig.client.durationBubbles = 10;
        syncFile();
    }

    public static void syncFile() {
        ConfigManager.sync(ComicsBubblesChat.MODID, Config.Type.INSTANCE);
    }

    public static void clearBubbles() {
        ClientBubblesUtil.BUBBLES_SYNC.clear();
    }
}

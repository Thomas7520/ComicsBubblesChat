package com.thomas7520.bubbleschat.util;

import com.google.common.collect.Lists;
import com.thomas7520.bubbleschat.BubblesConfig;
import com.thomas7520.bubbleschat.client.ClientBubblesUtil;

public class ResetUtil {

    public static void resetColors() {
        BubblesConfig.colorOutline.set(Lists.newArrayList(0, 0, 0, 220));
        BubblesConfig.colorInside.set(Lists.newArrayList(203, 203, 203, 220));
        BubblesConfig.colorText.set(Lists.newArrayList(0, 0, 0, 220));
    }

    public static void resetDuration() {
        BubblesConfig.durationBubbles.set(10);
    }

    public static void clearBubbles() {
        ClientBubblesUtil.BUBBLES_SYNC.clear();
    }

}

package com.xxs.igcsandroid.util;

import android.widget.ImageView;

public class ImageViewUtil {
    public static void rotate(ImageView iv, float rotation) {
        iv.setPivotX(iv.getWidth() / 2);
        iv.setPivotY(iv.getHeight() / 2);
        iv.setRotation(rotation);
    }
}

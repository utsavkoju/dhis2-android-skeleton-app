package com.example.android.androidskeletonapp.data.service;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.example.android.androidskeletonapp.ui.base.ListItemWithStyleHolder;

import org.hisp.dhis.android.core.common.ObjectStyle;

import androidx.core.content.ContextCompat;

public class StyleBinderHelper {

    public static void bindStyle(ListItemWithStyleHolder holder, ObjectStyle style) {
        // TODO Bind style
    }

    public static void setBackgroundColor(int color, ImageView imageView) {
        int col = ContextCompat.getColor(imageView.getContext(), color);
        Drawable drawable = imageView.getBackground();
        drawable.setColorFilter(col, PorterDuff.Mode.ADD);
        imageView.setBackground(drawable);
    }
}

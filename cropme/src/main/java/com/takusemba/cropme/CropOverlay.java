package com.takusemba.cropme;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

abstract class CropOverlay extends View {

    public CropOverlay(@NonNull Context context) {
        super(context);
    }

    public CropOverlay(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CropOverlay(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // TODO comment called after onPreDrawListener
    abstract RectF getFrame();
}

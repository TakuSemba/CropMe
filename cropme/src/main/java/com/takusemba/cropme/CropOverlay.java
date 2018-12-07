package com.takusemba.cropme;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * CropOverlay is laid on {@link CropImageView}.
 * You can extend this view to show your custom shaped overlay.
 */
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

    /**
     * This RectF is used to restrict smooth animations,
     * and cropped image also will be cropped in this shape.
     */
    abstract RectF getFrame();
}

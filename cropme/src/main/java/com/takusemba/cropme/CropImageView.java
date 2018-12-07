package com.takusemba.cropme;

import android.content.Context;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * ImageView to hold an Image to be animated.
 **/
class CropImageView extends AppCompatImageView {

    private RectF frame;

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * set image size along with {@link CropImageView#frame}
     **/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float height = getMeasuredHeight();
        float width = getMeasuredWidth();
        float widthScale = frame != null ? frame.width() / width : 1;
        float heightScale = frame != null ? frame.height() / height : 1;
        float scale = Math.max(widthScale, heightScale);
        setMeasuredDimension((int) (width * scale), (int) (height * scale));
    }

    void setFrame(RectF frame) {
        this.frame = frame;
    }
}

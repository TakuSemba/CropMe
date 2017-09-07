package com.takusemba.cropme;

import android.content.Context;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * CropImageView
 *
 * @author takusemba
 * @since 05/09/2017
 **/
class CropImageView extends AppCompatImageView {

    private RectF resultRect;

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
     * set image size along with {@link CropImageView#resultRect}
     **/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float height = getMeasuredHeight();
        float width = getMeasuredWidth();
        float widthScale = resultRect != null ? resultRect.width() / width : 1;
        float heightScale = resultRect != null ? resultRect.height() / height : 1;
        float scale = Math.max(widthScale, heightScale);
        setMeasuredDimension((int) (width * scale), (int) (height * scale));
    }

    void setResultRect(RectF resultRect) {
        this.resultRect = resultRect;
    }
}

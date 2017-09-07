package com.takusemba.cropme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * CropOverlayView
 *
 * @author takusemba
 * @since 05/09/2017
 **/
class CropOverlayView extends FrameLayout {

    private final Paint background = new Paint();
    private final Paint border = new Paint();
    private final Paint cropPaint = new Paint();

    private RectF resultRect;

    public CropOverlayView(@NonNull Context context) {
        this(context, null);
    }

    public CropOverlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropOverlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        cropPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        background.setColor(ContextCompat.getColor(getContext(), R.color.background));
        border.setColor(ContextCompat.getColor(getContext(), R.color.white));
        border.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);
        canvas.drawRect(resultRect, cropPaint);

        float borderHeight = resultRect.height() / 3;
        canvas.drawLine(resultRect.left, resultRect.top, resultRect.right, resultRect.top, border);
        canvas.drawLine(resultRect.left, resultRect.top + borderHeight, resultRect.right, resultRect.top + borderHeight, border);
        canvas.drawLine(resultRect.left, resultRect.top + borderHeight * 2, resultRect.right, resultRect.top + borderHeight * 2, border);
        canvas.drawLine(resultRect.left, resultRect.bottom, resultRect.right, resultRect.bottom, border);

        float borderWidth = resultRect.width() / 3;
        canvas.drawLine(resultRect.left, resultRect.top, resultRect.left, resultRect.bottom, border);
        canvas.drawLine(resultRect.left + borderWidth, resultRect.top, resultRect.left + borderWidth, resultRect.bottom, border);
        canvas.drawLine(resultRect.left + borderWidth * 2, resultRect.top, resultRect.left + borderWidth * 2, resultRect.bottom, border);
        canvas.drawLine(resultRect.right, resultRect.top, resultRect.right, resultRect.bottom, border);
    }

    void setResultRect(RectF resultRect) {
        this.resultRect = resultRect;
    }
}

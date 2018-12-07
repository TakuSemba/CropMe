package com.takusemba.cropme;

import android.content.Context;
import android.content.res.TypedArray;
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

public class SquareCropOverlay extends CropOverlay {

    private static final int BORDER_WIDTH = 5;

    private final Paint background = new Paint();
    private final Paint border = new Paint();
    private final Paint cropPaint = new Paint();

    private int backgroundAlpha;
    private boolean withBorder;
    private float percentWidth;
    private float percentHeight;

    private static final int DEFAULT_BASE = 1;
    private static final int DEFAULT_PBASE = 1;

    private static final int MIN_PERCENT = 0;
    private static final int MAX_PERCENT = 1;

    private static final float DEFAULT_PERCENT_WIDTH = 0.8f;
    private static final float DEFAULT_PERCENT_HEIGHT = 0.8f;

    private static final float DEFAULT_BACKGROUND_ALPHA = 0.8f;
    private static final float COLOR_DENSITY = 255;

    private static final boolean DEFAULT_WITH_BORDER = true;

    public SquareCropOverlay(@NonNull Context context) {
        this(context, null);
    }

    public SquareCropOverlay(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareCropOverlay(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SquareCropOverlay);

        percentWidth = a.getFraction(R.styleable.SquareCropOverlay_cropme_result_width, DEFAULT_BASE, DEFAULT_PBASE, DEFAULT_PERCENT_WIDTH);
        if (percentWidth < MIN_PERCENT || MAX_PERCENT < percentWidth) {
            throw new IllegalArgumentException("cropme_result_width must be set from 0% to 100%");
        }

        percentHeight = a.getFraction(R.styleable.SquareCropOverlay_cropme_result_height, DEFAULT_BASE, DEFAULT_PBASE, DEFAULT_PERCENT_HEIGHT);
        if (percentHeight < MIN_PERCENT || MAX_PERCENT < percentHeight) {
            throw new IllegalArgumentException("cropme_result_width must be set from 0% to 100%");
        }

        backgroundAlpha = (int) (a.getFraction(R.styleable.SquareCropOverlay_cropme_background_alpha, DEFAULT_BASE, DEFAULT_PBASE, DEFAULT_BACKGROUND_ALPHA) * COLOR_DENSITY);
        if (percentWidth < MIN_PERCENT || MAX_PERCENT < percentWidth) {
            throw new IllegalArgumentException("cropme_background_alpha must be set between 0% to 100%");
        }

        withBorder = a.getBoolean(R.styleable.SquareCropOverlay_cropme_with_border, DEFAULT_WITH_BORDER);

        a.recycle();

        init();
    }

    @Override
    public RectF getFrame() {
        float totalWidth = getMeasuredWidth();
        float totalHeight = getMeasuredHeight();
        float frameWidth = getMeasuredWidth() * percentWidth;
        float frameHeight = getMeasuredHeight() * percentHeight;
        return new RectF((totalWidth - frameWidth) / 2f, (totalHeight - frameHeight) / 2f,
                (totalWidth + frameWidth) / 2f, (totalHeight + frameHeight) / 2f);
    }

    private void init() {
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        cropPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        border.setStrokeWidth(BORDER_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        background.setColor(ContextCompat.getColor(getContext(), android.R.color.black));
        background.setAlpha(backgroundAlpha);
        border.setColor(ContextCompat.getColor(getContext(), R.color.light_white));

        float frameWidth = getMeasuredWidth() * percentWidth;
        float frameHeight = getMeasuredHeight() * percentHeight;
        float left = (getWidth() - frameWidth) / 2f;
        float top = (getHeight() - frameHeight) / 2f;
        float right = (getWidth() + frameWidth) / 2f;
        float bottom = (getHeight() + frameHeight) / 2f;

        canvas.drawRect(0, 0, getWidth(), getHeight(), background);
        canvas.drawRect(left, top, right, bottom, cropPaint);

        if (withBorder) {
            float borderHeight = frameHeight / 3;
            canvas.drawLine(left, top, right, top, border);
            canvas.drawLine(left, top + borderHeight, right, top + borderHeight, border);
            canvas.drawLine(left, top + borderHeight * 2, right, top + borderHeight * 2, border);
            canvas.drawLine(left, bottom, right, bottom, border);

            float borderWidth = frameWidth / 3;
            canvas.drawLine(left, top, left, bottom, border);
            canvas.drawLine(left + borderWidth, top, left + borderWidth, bottom, border);
            canvas.drawLine(left + borderWidth * 2, top, left + borderWidth * 2, bottom, border);
            canvas.drawLine(right, top, right, bottom, border);
        }
    }
}

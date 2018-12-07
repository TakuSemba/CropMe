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
import android.view.ViewTreeObserver;

public class SquareCropOverlay extends CropOverlay {

    private static final int BORDER_WIDTH = 5;

    private final Paint background = new Paint();
    private final Paint border = new Paint();
    private final Paint cropPaint = new Paint();

    private RectF frame;
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

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                float resultWidth = getWidth() * percentWidth;
                float resultHeight = getHeight() * percentHeight;
                frame = new RectF((getWidth() - resultWidth) / 2f, (getHeight() - resultHeight) / 2f,
                        (getWidth() + resultWidth) / 2f, (getHeight() + resultHeight) / 2f);
                return true;
            }
        });

        init();
    }

    @Override
    public RectF getFrame() {
        if (frame != null) {
            return frame;
        } else {
            float resultWidth = getMeasuredWidth() * percentWidth;
            float resultHeight = getMeasuredHeight() * percentHeight;
            frame = new RectF((getMeasuredWidth() - resultWidth) / 2f, (getMeasuredHeight() - resultHeight) / 2f,
                    (getMeasuredWidth() + resultWidth) / 2f, (getMeasuredHeight() + resultHeight) / 2f);
            return frame;
        }
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

        if (frame == null) return;

        background.setColor(ContextCompat.getColor(getContext(), android.R.color.black));
        background.setAlpha(backgroundAlpha);
        border.setColor(ContextCompat.getColor(getContext(), R.color.light_white));

        canvas.drawRect(0, 0, getWidth(), getHeight(), background);
        canvas.drawRect(frame, cropPaint);

        if (withBorder) {
            float borderHeight = frame.height() / 3;
            canvas.drawLine(frame.left, frame.top, frame.right, frame.top, border);
            canvas.drawLine(frame.left, frame.top + borderHeight, frame.right, frame.top + borderHeight, border);
            canvas.drawLine(frame.left, frame.top + borderHeight * 2, frame.right, frame.top + borderHeight * 2, border);
            canvas.drawLine(frame.left, frame.bottom, frame.right, frame.bottom, border);

            float borderWidth = frame.width() / 3;
            canvas.drawLine(frame.left, frame.top, frame.left, frame.bottom, border);
            canvas.drawLine(frame.left + borderWidth, frame.top, frame.left + borderWidth, frame.bottom, border);
            canvas.drawLine(frame.left + borderWidth * 2, frame.top, frame.left + borderWidth * 2, frame.bottom, border);
            canvas.drawLine(frame.right, frame.top, frame.right, frame.bottom, border);
        }
    }
}

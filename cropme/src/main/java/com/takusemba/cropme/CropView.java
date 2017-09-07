package com.takusemba.cropme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * CropView
 *
 * @author takusemba
 * @since 05/09/2017
 **/
public class CropView extends FrameLayout implements Croppable {

    private static final int MIN_PERCENT = 0;
    private static final int MAX_PERCENT = 1;
    private static final int DEFAULT_BASE = 1;
    private static final int DEFAULT_PBASE = 1;
    private static final float DEFAULT_PERCENT_WIDTH = 0.8f;
    private static final float DEFAULT_PERCENT_HEIGHT = 0.8f;

    private static final int DEFAULT_MAX_SCALE = 2;
    private static final int MIN_SCALE = 1;
    private static final int MAX_SCALE = 5;

    private MoveAnimator horizontalAnimator;
    private MoveAnimator verticalAnimator;
    private ScaleAnimator scaleAnimator;

    private ActionDetector actionDetector;

    private float percentWidth;
    private float percentHeight;
    private int maxScale;
    private RectF restriction;

    public CropView(@NonNull Context context) {
        this(context, null);
    }

    public CropView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CropView);

        percentWidth = a.getFraction(R.styleable.CropView_cropme_result_width, DEFAULT_BASE, DEFAULT_PBASE, DEFAULT_PERCENT_WIDTH);
        if (percentWidth < MIN_PERCENT || MAX_PERCENT < percentWidth) {
            throw new IllegalArgumentException("sr_result_width must be set from 0% to 100%");
        }

        percentHeight = a.getFraction(R.styleable.CropView_cropme_result_height, DEFAULT_BASE, DEFAULT_PBASE, DEFAULT_PERCENT_HEIGHT);
        if (percentHeight < MIN_PERCENT || MAX_PERCENT < percentHeight) {
            throw new IllegalArgumentException("sr_result_height must be set from 0% to 100%");
        }

        maxScale = a.getInt(R.styleable.CropView_cropme_max_scale, DEFAULT_MAX_SCALE);
        if (maxScale < MIN_SCALE || MAX_SCALE < maxScale) {
            throw new IllegalArgumentException("sr_max_scale must be set from 1 to 5");
        }

        a.recycle();

        init();
    }

    private void init() {

        startActionDetector();
        addLayouts();

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                CropImageView target = (CropImageView) findViewById(R.id.cropme_image_view);
                CropOverlayView overlayView = (CropOverlayView) findViewById(R.id.cropme_overlay);

                float resultWidth = getWidth() * percentWidth;
                float resultHeight = getHeight() * percentHeight;

                restriction = new RectF((getWidth() - resultWidth) / 2f, (getHeight() - resultHeight) / 2f,
                        (getWidth() + resultWidth) / 2f, (getHeight() + resultHeight) / 2f);

                horizontalAnimator = new HorizontalMoveAnimatorImpl(target, restriction);
                verticalAnimator = new VerticalMoveAnimatorImpl(target, restriction);
                scaleAnimator = new ScaleAnimatorImpl(target, maxScale);

                target.setResultRect(restriction);
                overlayView.setResultRect(restriction);

                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    private void startActionDetector() {
        actionDetector = new ActionDetector(getContext(), new ActionListener() {

            @Override
            public void onScaled(float scale) {
                scaleAnimator.scale(scale);
            }

            @Override
            public void onScaleEnded() {
                scaleAnimator.reScaleIfNeeded();
            }

            @Override
            public void onMoved(float dx, float dy) {
                horizontalAnimator.move(dx);
                verticalAnimator.move(dy);
            }

            @Override
            public void onFlinged(float velocityX, float velocityY) {
                horizontalAnimator.fling(velocityX);
                verticalAnimator.fling(velocityY);
            }

            @Override
            public void onMoveEnded() {
                if (horizontalAnimator.isNotFlinging()) {
                    horizontalAnimator.reMoveIfNeeded(0);
                }

                if (verticalAnimator.isNotFlinging()) {
                    verticalAnimator.reMoveIfNeeded(0);
                }
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                actionDetector.detectAction(event);
                return true;
            }
        });
    }

    private void addLayouts() {
        CropImageView imageView = new CropImageView(getContext());
        imageView.setId(R.id.cropme_image_view);
        LayoutParams imageParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView, imageParams);

        CropOverlayView overlayView = new CropOverlayView(getContext());
        overlayView.setId(R.id.cropme_overlay);
        LayoutParams overlayParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(overlayView, overlayParams);
    }

    @Override
    public void setUri(Uri uri) {
        ((ImageView) findViewById(R.id.cropme_image_view)).setImageURI(uri);
    }

    @Override
    public void crop(OnCropListener listener) {
        CropImageView target = (CropImageView) findViewById(R.id.cropme_image_view);
        Rect targetRect = new Rect();
        target.getHitRect(targetRect);
        Bitmap bitmap = ((BitmapDrawable) target.getDrawable()).getBitmap();
        int leftOffset = (int) (restriction.left - targetRect.left);
        int topOffset = (int) (restriction.top - targetRect.top);
        int width = (int) restriction.width();
        int height = (int) restriction.height();
        if (bitmap.getWidth() < leftOffset + width || bitmap.getHeight() < topOffset + height) {
            listener.onFailure();
            return;
        }
        if (leftOffset < 0) {
            leftOffset = 0;
            width += leftOffset;
        }
        if (topOffset < 0) {
            topOffset = 0;
            height += topOffset;
        }
        Bitmap result = Bitmap.createBitmap(bitmap, leftOffset, topOffset, width, height);
        if (result != null) {
            listener.onSuccess(result);
        } else {
            listener.onFailure();
        }
    }
}

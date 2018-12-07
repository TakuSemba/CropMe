package com.takusemba.cropme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.MainThread;
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
 * CropLayout is a parent layout that has {@link CropOverlay}.
 * Image can be set thorough this class. see {@link Croppable}
 **/
public final class CropLayout extends FrameLayout implements Croppable {

    private static final int DEFAULT_MAX_SCALE = 2;
    private static final int MIN_SCALE = 1;
    private static final int MAX_SCALE = 5;

    private MoveAnimator horizontalAnimator;
    private MoveAnimator verticalAnimator;
    private ScaleAnimator scaleAnimator;

    private ActionDetector actionDetector;

    private int maxScale;
    private RectF frame;

    public CropLayout(@NonNull Context context) {
        this(context, null);
    }

    public CropLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CropLayout);

        maxScale = a.getInt(R.styleable.CropLayout_cropme_max_scale, DEFAULT_MAX_SCALE);
        if (maxScale < MIN_SCALE || MAX_SCALE < maxScale) {
            throw new IllegalArgumentException("cropme_max_scale must be set from 1 to 5");
        }

        a.recycle();

        init();
    }

    private void init() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                final CropOverlay overlayView = findViewById(R.id.cropme_overlay);
                frame = overlayView.getFrame();
                CropImageView cropImageView = new CropImageView(getContext());
                cropImageView.setFrame(frame);
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                cropImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                cropImageView.setAdjustViewBounds(true);
                cropImageView.setLayoutParams(layoutParams);
                cropImageView.setId(R.id.cropme_image_view);
                addView(cropImageView, 0);

                horizontalAnimator = new HorizontalMoveAnimatorImpl(cropImageView, frame, maxScale);
                verticalAnimator = new VerticalMoveAnimatorImpl(cropImageView, frame, maxScale);
                scaleAnimator = new ScaleAnimatorImpl(cropImageView, maxScale);

                startActionDetector();

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

    @Override
    public void setUri(Uri uri) {
        ImageView image = findViewById(R.id.cropme_image_view);
        image.setImageURI(uri);
        image.requestLayout();
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        ImageView image = findViewById(R.id.cropme_image_view);
        image.setImageBitmap(bitmap);
        image.requestLayout();
    }

    @Override
    public boolean isOffOfFrame() {
        ImageView imageView = findViewById(R.id.cropme_image_view);
        Rect targetRect = new Rect();
        imageView.getHitRect(targetRect);
        return !targetRect.contains((int) frame.left, (int) frame.top, (int) frame.right, (int) frame.bottom);
    }

    @Override
    @MainThread
    public void crop(final OnCropListener listener) {
        ImageView imageView = findViewById(R.id.cropme_image_view);
        final Rect targetRect = new Rect();
        imageView.getHitRect(targetRect);
        final Bitmap sourceBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createScaledBitmap(sourceBitmap, targetRect.width(), targetRect.height(), false);
                int leftOffset = (int) frame.left - targetRect.left;
                int topOffset = (int) frame.top - targetRect.top;
                int rightOffset = (int) (targetRect.right - frame.right);
                int bottomOffset = (int) (targetRect.bottom - frame.bottom);
                int width = (int) frame.width();
                int height = (int) frame.height();

                if (leftOffset < 0) {
                    width += leftOffset;
                    leftOffset = 0;
                }
                if (topOffset < 0) {
                    height += topOffset;
                    topOffset = 0;
                }
                if (rightOffset < 0) {
                    width += rightOffset;
                }
                if (bottomOffset < 0) {
                    height += bottomOffset;
                }
                if (width < 0 || height < 0) {
                    throw new IllegalStateException("width or heigt is less than 0");
                }

                final Bitmap result = Bitmap.createBitmap(bitmap, leftOffset, topOffset, width, height);

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null) {
                            listener.onSuccess(result);
                        } else {
                            listener.onFailure();
                        }
                    }
                });
            }
        }).start();
    }
}

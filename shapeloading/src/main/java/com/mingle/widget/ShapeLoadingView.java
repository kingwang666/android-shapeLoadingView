package com.mingle.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mingle.shapeloading.R;

public class ShapeLoadingView extends LinearLayout {

    private static final int ANIMATION_DURATION = 500;

    private static final float FACTOR = 1.2f;

    private static float mDistance = 200;

    private ShapeView mShapeView;

    private ImageView mIndicationImg;

    private TextView mLoadTV;

    private AnimatorSet mUpAnimatorSet;
    private AnimatorSet mDownAnimatorSet;

    private boolean mStopped = false;

    private int mDelay;

    public ShapeLoadingView(Context context) {
        super(context);
        init(context, null);
    }

    public ShapeLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);

    }


    public ShapeLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShapeLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        mDistance = dip2px(context, 54f);
        LayoutInflater.from(context).inflate(R.layout.shape_load_view, this, true);
        mShapeView = (ShapeView) findViewById(R.id.shape_view);
        mIndicationImg = (ImageView) findViewById(R.id.indication_img);
        mLoadTV = (TextView) findViewById(R.id.load_tv);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeLoadingView);
        String loadText = typedArray.getString(R.styleable.ShapeLoadingView_slt_text);
        int textAppearance = typedArray.getResourceId(R.styleable.ShapeLoadingView_slt_textAppearance, -1);
        mDelay = typedArray.getInteger(R.styleable.ShapeLoadingView_slt_delay, 80);
        typedArray.recycle();

        if (textAppearance != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLoadTV.setTextAppearance(textAppearance);
            } else {
                mLoadTV.setTextAppearance(getContext(), textAppearance);
            }
        }
        setLoadingText(loadText);
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getVisibility() == VISIBLE) {
            startLoading(mDelay);
        }
    }

    private Runnable mFreeFallRunnable = new Runnable() {
        @Override
        public void run() {
            mShapeView.setRotation(180f);
            mShapeView.setTranslationY(0);
            mIndicationImg.setScaleX(0.2f);
            mStopped = false;
            freeFall();
        }
    };

    private void startLoading(long delay) {
        if (mDownAnimatorSet != null && mDownAnimatorSet.isRunning()) {
            return;
        }
        this.removeCallbacks(mFreeFallRunnable);
        if (delay > 0) {
            this.postDelayed(mFreeFallRunnable, delay);
        } else {
            this.post(mFreeFallRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoading();
    }

    private void stopLoading() {
        mStopped = true;
        if (mUpAnimatorSet != null) {
            if (mUpAnimatorSet.isRunning()) {
                mUpAnimatorSet.cancel();
            }
            mUpAnimatorSet.removeAllListeners();
            for (Animator animator : mUpAnimatorSet.getChildAnimations()) {
                animator.removeAllListeners();
            }
            mUpAnimatorSet = null;
        }
        if (mDownAnimatorSet != null) {
            if (mDownAnimatorSet.isRunning()) {
                mDownAnimatorSet.cancel();
            }
            mDownAnimatorSet.removeAllListeners();
            for (Animator animator : mDownAnimatorSet.getChildAnimations()) {
                animator.removeAllListeners();
            }
            mDownAnimatorSet = null;
        }
        this.removeCallbacks(mFreeFallRunnable);
    }

    @Override
    public void setVisibility(int visibility) {
        this.setVisibility(visibility, mDelay);
    }

    public void setVisibility(int visibility, int delay) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            startLoading(delay);
        } else {
            stopLoading();
        }
    }

    public void setDelay(int delay) {
        mDelay = delay;
    }

    public int getDelay() {
        return mDelay;
    }

    public void setLoadingText(CharSequence loadingText) {
        if (TextUtils.isEmpty(loadingText)) {
            mLoadTV.setVisibility(GONE);
        } else {
            mLoadTV.setVisibility(VISIBLE);
        }
        mLoadTV.setText(loadingText);
    }

    public CharSequence getLoadingText(){
        return mLoadTV.getText();
    }

    /**
     * 上抛
     */
    private void upThrow() {
        if (mUpAnimatorSet == null) {
            mUpAnimatorSet = new AnimatorSet();
            mUpAnimatorSet.playTogether(
                    ObjectAnimator.ofFloat(mShapeView, "translationY", mDistance, 0),
                    ObjectAnimator.ofFloat(mIndicationImg, "scaleX", 1f, 0.2f),
                    ObjectAnimator.ofFloat(mShapeView, "rotation", 0, 180)
            );
            mUpAnimatorSet.setDuration(ANIMATION_DURATION);
            mUpAnimatorSet.setInterpolator(new DecelerateInterpolator(FACTOR));
            mUpAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!mStopped) {
                        freeFall();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        mUpAnimatorSet.start();


    }

    /**
     * 下落
     */
    private void freeFall() {
        if (mDownAnimatorSet == null) {
            mDownAnimatorSet = new AnimatorSet();
            mDownAnimatorSet.playTogether(
                    ObjectAnimator.ofFloat(mShapeView, "translationY", 0, mDistance),
                    ObjectAnimator.ofFloat(mIndicationImg, "scaleX", 0.2f, 1f)
            );
            mDownAnimatorSet.setDuration(ANIMATION_DURATION);
            mDownAnimatorSet.setInterpolator(new AccelerateInterpolator(FACTOR));
            mDownAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!mStopped) {
                        mShapeView.changeShape();
                        upThrow();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        mDownAnimatorSet.start();
    }

}

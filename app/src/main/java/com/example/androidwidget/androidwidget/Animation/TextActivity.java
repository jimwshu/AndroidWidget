package com.example.androidwidget.androidwidget.Animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.androidwidget.androidwidget.Helpers.UIHelper;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.RunnableFuture;

/**
 * Created by wangshu on 16/9/2.  animation text
 *
 *  一个100 * 300 的区域,最多出现100个点赞动画,然后点赞动画通过贝塞尔曲线随机移动,动画是自己创建ImageView,然后通过point的点的移动来处理移动
 *  在移动过程中,通过监听器不断改变透明度和point
 *
 *
 */

public class TextActivity extends RelativeLayout {

    private Drawable mGiftDrawable;
    private ArrayList<Integer> mWaiting = new ArrayList<>();

    private int mHeight;
    private int mWidth;
    private Interpolator line = new LinearInterpolator();

    private int cHeight;
    private int cWidth;

    private RelativeLayout.LayoutParams lp;

    Random random = new Random();

    public TextActivity(Context context) {
        super(context);
        init();
    }

    public TextActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextActivity(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGiftDrawable = getResources().getDrawable(android.R.mipmap.sym_def_app_icon);
        mWaiting = new ArrayList<>(100);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        cHeight = mGiftDrawable.getIntrinsicHeight();
        cWidth = mGiftDrawable.getIntrinsicWidth();

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        lp = new RelativeLayout.LayoutParams(mHeight, mWidth);
        lp.addRule(ALIGN_PARENT_BOTTOM);
        lp.leftMargin = (mWidth - cWidth) / 2 + UIHelper.dip2px(getContext(), 16);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void addFavor(int index) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(mGiftDrawable);
        imageView.setLayoutParams(lp);
        addView(imageView);

        Animator set = getAnimator(imageView);
        set.addListener(new AnimEndListener(imageView));
        set.start();
    }

    private Animator getAnimator(ImageView imageView) {
        AnimatorSet set = getEnterAnimator(imageView);

        ValueAnimator bezierValueAnimator = getBezierValueAnimator(imageView);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playTogether(set, bezierValueAnimator);
        finalSet.setInterpolator(line);//实现随机变速
        finalSet.setTarget(imageView);
        return finalSet;

    }

    private class AnimEndListener extends AnimatorListenerAdapter {
        private ImageView imageView;

        public AnimEndListener(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            imageView.setVisibility(View.GONE);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                drawable.setCallback(null);
            }
            imageView.setImageDrawable(null);
            imageView.clearAnimation();
            imageView.clearFocus();

            post(new Runnable() {
                @Override
                public void run() {
                    removeView(imageView);
                }
            });
            int index = 0;
            if (mWaiting.size() > 0) {
                synchronized (this) {
                    index = mWaiting.remove(0);
                }
                addFavor(index);
            }
        }
    }


    private ValueAnimator getBezierValueAnimator(View target) {
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(2), getPointF(1));
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF((mWidth - cWidth) / 2 + UIHelper.dip2px(getContext(), 17), mHeight - cHeight), new PointF(random.nextInt(mWidth - cWidth / 2), 0));
        animator.addUpdateListener(new BazierListenr(target));
        animator.setTarget(target);
        animator.setDuration(200);
        return animator;
    }

    private PointF getPointF(int scale) {
        PointF pointF = new PointF();
        pointF.x = random.nextInt((mWidth - cWidth));
        if (scale == 1) {
            pointF.y = random.nextInt((mHeight - cHeight) / 2);

        } else {
            pointF.y = random.nextInt((mHeight - cHeight) / 4) + (mHeight - cHeight) / 2;
        }
        return pointF;
    }

    private class BazierListenr implements ValueAnimator.AnimatorUpdateListener {
        private ImageView imageView;

        public BazierListenr() {
        }

        public BazierListenr(View imageView) {
            this.imageView = (ImageView) imageView;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointF = (PointF) animation.getAnimatedValue();
            imageView.setX(pointF.x);
            imageView.setY(pointF.y);
            imageView.setAlpha(1 - animation.getAnimatedFraction());
        }
    }




    public AnimatorSet getEnterAnimator(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, View.SCALE_X, 0f, 1f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, animator1, animator2);
        animatorSet.setDuration(5000);
        animatorSet.setTarget(view);
        return animatorSet;
    }


    /**
     * 让点赞动画像贝塞尔曲线一样移动
     */
    public class BezierEvaluator implements TypeEvaluator<PointF> {
        private PointF pointF1;
        private PointF pointF2;

        public BezierEvaluator() {
        }

        public BezierEvaluator(PointF f1, PointF f2) {
            this.pointF1 = f1;
            this.pointF2 = f2;
        }

        @Override
        public PointF evaluate(float time, PointF startValue, PointF endValue) {
            float timeLeft = 1.0f - time;
            PointF point = new PointF();
            PointF point0 = startValue;//起点

            PointF point3 = endValue;//终点

            point.x = timeLeft * timeLeft * timeLeft * (point0.x)
                    + 3 * timeLeft * timeLeft * time * (pointF1.x)
                    + 3 * timeLeft * time * time * (pointF2.x)
                    + time * time * time * (point3.x);

            point.y = timeLeft * timeLeft * timeLeft * (point0.y)
                    + 3 * timeLeft * timeLeft * time * (pointF1.y)
                    + 3 * timeLeft * time * time * (pointF2.y)
                    + time * time * time * (point3.y);
            return point;
        }
    }

}

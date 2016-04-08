package com.example.androidwidget.androidwidget;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wangshu on 16/4/8.
 */
public class TextViewActionAnimationActivity extends Activity {

    private TextView mTextView;
    private ImageView mImageView;
    private Animation mFlipAnimation;
    private Animation mReverseAnimation;
    private Animation anim;

    public static final int MAX_LINE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textview_animation_layout);
        mTextView = (TextView) findViewById(R.id.topic_intro);
        mImageView = (ImageView) findViewById(R.id.topic_des_more_icon);
        mTextView.setMaxLines(MAX_LINE);

        MoreOnClickListener moreOnClickListener = new MoreOnClickListener();
        mTextView.setOnClickListener(moreOnClickListener);
        mImageView.setOnClickListener(moreOnClickListener);

    }

    class MoreOnClickListener implements View.OnClickListener {

        private  TextView textView = mTextView;
        private int start;
        private int end;
        private boolean hasMore = true;
        private void createAnimationIfNeed() {
            if (anim != null) {
                return;
            }

            /**
             * rotate逆时针或者顺时针，第二个参数减去第一个，大于0，顺时针，小于0逆时针
             */
            mFlipAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mFlipAnimation.setDuration(1500);
            mFlipAnimation.setInterpolator(new LinearInterpolator());
            mFlipAnimation.setFillAfter(true);

            mReverseAnimation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mReverseAnimation.setDuration(1500);
            mReverseAnimation.setInterpolator(new LinearInterpolator());
            mReverseAnimation.setFillAfter(true);

            anim = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    textView.setHeight((int) (start + (end-start) * interpolatedTime));
                    super.applyTransformation(interpolatedTime, t);
                }
            };
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (hasMore) {
                        textView.setMaxLines(MAX_LINE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
        @Override
        public void onClick(View v) {
            final int temp = mTextView.getHeight();
            createAnimationIfNeed();
            hasMore = !hasMore;
            if (!hasMore) {
                /**
                 * 一开始的时候，textview的maxLine只有一行，重新设置maxlines之后，必须对textview重新进行onMeasure，调用measure方法，重新获取textview最后的高度
                 * 然后调用animation，只能用View.MeasureSpec.UNSPECIFIED模式，因为这个是突破了0-spaceSize的限制，是扩大了其的height，其他的模式都不能让
                 * textView的height变大的
                 */
                mTextView.setMaxLines(Integer.MAX_VALUE);
                mTextView.clearAnimation();
                mTextView.measure(View.MeasureSpec.makeMeasureSpec(mTextView.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                start = temp;
                end = mTextView.getMeasuredHeight();
                mTextView.setHeight(start);
                anim.setDuration(Math.abs(end - start) + 150);
                mTextView.startAnimation(anim);
                mImageView.clearAnimation();
                mImageView.startAnimation(mFlipAnimation);


            } else {
                /**
                 * Return the vertical position of the top of the specified line (0…getLineCount()). If the specified line is equal to the line count, returns the bottom of the last line.
                 * 获取第一行的高度，比如一个textview的最大行数是5行，总共的高度是170，那么第一行的的高度就是37(注意，并不是单纯的170/5,获取的是第一行最底部到y轴的距离)，然后后面的就是一个等差数列（我觉得应该是textview
                 * 第一行其实离最上边还有点距离，或者说有padding，从第二行开始就没有这个问题，获取的就是第二行的底部到最上边的距离）;
                 */
                end = mTextView.getLayout().getLineBottom(0) + mTextView.getPaddingTop() + mTextView.getPaddingBottom();
                start = temp;
                mTextView.clearAnimation();
                anim.setDuration(Math.abs(end - start) + 150);
                mTextView.startAnimation(anim);
                mImageView.clearAnimation();
                mImageView.startAnimation(mReverseAnimation);
            }


        }
    }

}

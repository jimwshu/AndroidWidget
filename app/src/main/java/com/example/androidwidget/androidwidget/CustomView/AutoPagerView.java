package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by wangshu on 16/4/12.
 */
public class AutoPagerView extends ViewPager {
    String TAG = "AutoPagerView";
    public final static  int REFRESH_TIME  = 2300;

    public AutoPagerView(Context context) {
        super(context);
    }

    public AutoPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int height = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);


        /**
         * 设置自身的宽高。。。，参数是宽高,为什么无作用？？
         */
        //setMeasuredDimension(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) (height * 0.5), MeasureSpec.EXACTLY));
        //setMeasuredDimension(resolveSize(height, widthMeasureSpec), resolveSize(200, heightMeasureSpec));

    }




    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            removeCallbacks(this);
            int current = getCurrentItem();
            setCurrentItem(current+1, true);
            postDelayed(this, REFRESH_TIME);

        }
    };


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        postDelayed(runnable, REFRESH_TIME);
        Log.e(TAG, " 44");

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(runnable);
        Log.e(TAG, " 55");

    }
}

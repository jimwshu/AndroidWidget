package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidwidget.androidwidget.R;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by wangshu on 15/11/1.
 * 自定义的View
 * onMeasure --> onLayout --> onDraw（测算控件所占的控件 --》 测算控件所在的位置 --》汇出该控件）
 * requestLayout --> 重新执行onMeasure， onLayout
 * postInvalidate（非主线程执行） 或者 invalidate --》 onDraw
 */
public class CustomTextView extends View {

    private String title;
    private int titleSize;
    private int titleColor;
    private Drawable textViewBackground;

    private static String TAG = CustomTextView.class.getSimpleName();
    int i = 1;
    int j = 1;
    int k = 1;

    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    private Paint mPaint;


    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        title = typedArray.getString(R.styleable.CustomTextView_title);
        titleSize  = typedArray.getDimensionPixelSize(R.styleable.CustomTextView_titleSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));

        titleColor = typedArray.getColor(R.styleable.CustomTextView_titleColor, Color.BLACK);
        textViewBackground = typedArray.getDrawable(R.styleable.CustomTextView_textViewBackground);

        typedArray.recycle();

        /**
         * 获得绘制文本的宽和高
         */
        mPaint = new Paint();
        mPaint.setTextSize(titleSize);
        // mPaint.setColor(mTitleTextColor);
        mBound = new Rect();
        mPaint.getTextBounds(title, 0, title.length(), mBound);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                title = randomText();
                requestLayout();
                postInvalidate();
            }
        });
    }

    private String randomText()
    {
        Random random = new Random();
        Set<Integer> set = new HashSet<Integer>();
        while (set.size() < 4)
        {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        StringBuffer sb = new StringBuffer();
        for (Integer i : set)
        {
            sb.append("" + i);
        }

        return sb.toString();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;

        switch (widthMode) {
            case MeasureSpec.EXACTLY :// 使用的是具体的值，例如layout_width="200dp"或者"match_parent",是这个模式
                width = widthSize;
                break;

            case MeasureSpec.AT_MOST ://使用的是“wrap_content"的时候，是这个模式，需要计算text所占据的宽度，然后加上左右边距
                mPaint.setTextSize(titleSize);
                mPaint.getTextBounds(title, 0, title.length(), mBound);
                float textWidth = mBound.width();
                int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
                width = desired;
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY :
                height = heightSize;
                break;

            case MeasureSpec.AT_MOST :
                mPaint.setTextSize(titleSize);
                mPaint.getTextBounds(title, 0, title.length(), mBound);
                float textHeight = mBound.height();
                int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
                height = desired;
                break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        j++;
        if (j == 3) {
            mPaint.setColor(Color.BLACK);
        } else {
            mPaint.setColor(Color.YELLOW);
        }


        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        mPaint.setColor(titleColor);
        canvas.drawText(title, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
    }


    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }
}

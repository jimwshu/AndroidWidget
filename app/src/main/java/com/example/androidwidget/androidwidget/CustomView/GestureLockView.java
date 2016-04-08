package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangshu on 16/2/26.
 */
public class GestureLockView extends View{

    public GestureLockView(Context context) {
        super(context);
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * GestureLockView的三种状态
     */
    enum Mode
    {
        STATUS_NO_FINGER, STATUS_FINGER_ON, STATUS_FINGER_UP;
    }

    /**
     * 当前状态，状态改变需要重绘
     */
    private Mode mCurrentStatus = Mode.STATUS_NO_FINGER;

    /**
     * 宽高
     */
    private int mHeight;
    private int mWidth;

    /**
     * 外圆半径
     */
    private int mRedius;


    /**
     * 画笔的宽度
     */
    private int mStrokeWidth = 2;

    /**
     * 圆心坐标
     */
    private int mCenterX;
    private int mCenterY;

    /**
     * 箭头（小三角最长边的一半长度 = mArrawRate * mWidth / 2 ）
     */
    private float mArrowRate = 0.333f;
    private int mArrowDegree = -1;

    private Path mArrowPath;

    /**
     * 内圆的半径 = mInnerCircleRadiusRate * mRadus
     *
     */
    private float mInnerCircleRadiusRate = 0.3F;


    /**
     * 静止的时候圆圈内外的颜色，手指在上面的时候颜色
     */
    private int colorNoFingerInner, colorNoFingerOutter, colorFingerOn, colorFingerUp;

    private Paint mPaint;

    public GestureLockView(Context context, int colorNoFingerInner , int colorNoFingerOutter , int colorFingerOn , int colorFingerUp) {
        super(context);
        this.colorFingerOn = colorFingerOn;
        this.colorFingerUp = colorFingerUp;
        this.colorNoFingerInner = colorNoFingerInner;
        this.colorNoFingerOutter = colorNoFingerOutter;
        /**
         * 抗锯齿
         */
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArrowPath = new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {



        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

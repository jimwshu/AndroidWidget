package com.example.androidwidget.androidwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by wangshu on 16/7/6.
 *
 * 1. 在初始化的时候通过mPath 画三角形箭头，并设置角度为-1
 * 2. 在onDraw中根据不同的状态画不同颜色的圆圈。
 * 3. 特定状态下画三角形箭头，此时其有个特定的角度（由viewgroup传进来）
 * 4. 提供方法设置角度和状态，每次状态改变都需要调用onDraw方法
 */
public class GestureLockView extends View {

    public static final int STATUS_NO_FINGER = 0, STATUS_FINGER_ON = 1, STATUS_FINGER_UP = 2;

    private int mCurrentStatus = STATUS_NO_FINGER;

    private int mWidth;
    private int mHeight;

    //外圆半径
    private int mRadius;
    //画笔宽度
    private int mStrokeWidth;

    private Paint mPaint;

    //小圆半径
    private int mSmallRadius;

    //中心点坐标
    private int mCenterX;
    private int mCenterY;

    // 箭头相关属性
    private Path mArrowPath;
    private int mArrowDegree = -1;
    private float mArrowRate = 0.333f;


    /**
     * 四个颜色，可由用户自定义，初始化时由GestureLockViewGroup传入
     */
    private int mColorNoFingerInner;
    private int mColorNoFingerOutter;
    private int mColorFingerOn;
    private int mColorFingerUp;




    public GestureLockView(Context context) {
        this(context, null);
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mColorNoFingerInner = 0xff00ff00;
        mColorNoFingerOutter = 0xff00ff00;
        mColorFingerOn = 0xff0000ff;
        mColorFingerUp = 0xff68228b;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mArrowPath = new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mHeight = mWidth = mHeight > mWidth ? mWidth : mHeight;

        mCenterX = mWidth/2;
        mCenterY = mWidth/2;
        mRadius = mWidth/2;
        mSmallRadius = (int) (mRadius * 0.3);
        mRadius -= mStrokeWidth/2;


        // 绘制三角形，初始时是个默认箭头朝上的一个等腰三角形，用户绘制结束后，根据由两个GestureLockView决定需要旋转多少度
        float mArrowLength = mWidth / 2 * mArrowRate;
        mArrowPath.moveTo(mWidth / 2, mStrokeWidth + 2);
        mArrowPath.lineTo(mWidth / 2 - mArrowLength, mStrokeWidth + 2
                + mArrowLength);
        mArrowPath.lineTo(mWidth / 2 + mArrowLength, mStrokeWidth + 2
                + mArrowLength);
        // 回到初始点，形成封闭曲线
        mArrowPath.close();
        mArrowPath.setFillType(Path.FillType.WINDING);

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);
        Log.e("tag:", "view ontouch " + b);
        return b;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (mCurrentStatus) {
            case STATUS_NO_FINGER:
                mPaint.setColor(mColorNoFingerInner);
                drawSmallCircle(canvas);
                mPaint.setColor(mColorNoFingerOutter);
                drawBigCircle(canvas);
                break;
            case STATUS_FINGER_ON:
                mPaint.setColor(mColorFingerOn);
                drawSmallCircle(canvas);
                drawBigCircle(canvas);
                break;
            case STATUS_FINGER_UP:
                mPaint.setColor(mColorFingerUp);
                drawSmallCircle(canvas);
                drawBigCircle(canvas);
                drawArrow(canvas);
                break;
        }


    }

    private void drawSmallCircle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCenterX, mCenterY, mSmallRadius, mPaint);
    }

    private void drawBigCircle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
    }

    private void drawArrow(Canvas canvas) {
        if (mArrowDegree != -1) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(0xffff0000);
            canvas.save();
            canvas.rotate(mArrowDegree, mCenterX, mCenterY);
            canvas.drawPath(mArrowPath, mPaint);

            canvas.restore();
        }

    }

    /**
     * 设置当前模式并重绘界面
     *
     * @param mode
     */
    public void setMode(int mode)
    {
        this.mCurrentStatus = mode;
        invalidate();
    }

    public void setArrowDegree(int degree)
    {
        this.mArrowDegree = degree;
    }

    public int getArrowDegree()
    {
        return this.mArrowDegree;
    }


}

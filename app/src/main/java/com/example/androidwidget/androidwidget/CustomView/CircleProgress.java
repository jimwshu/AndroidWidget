package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.androidwidget.androidwidget.Helpers.UIHelper;
import com.example.androidwidget.androidwidget.R;


/**
 * Created by wangshu on 16/7/4.  圆环进度条
 */
public class CircleProgress extends View {

    private int round;
    private int mFirstColor;
    private int mSecondColor;
    private int speed;

    private int mProgress;

    private Paint mPaint;

    private boolean isNext = false;

    private Context mContext;

    public CircleProgress(Context context) {
        super(context);
        init(context);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private void init(Context context) {
        mContext = context;
        round = UIHelper.dip2px(context, 15);
        mFirstColor = getResources().getColor(R.color.common_signin_btn_text_dark);
        mSecondColor = getResources().getColor(R.color.common_signin_btn_text_light);
        speed = 5;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mProgress = 0;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int center = getWidth() / 2;
        int r = center - round;
        mPaint.setStrokeWidth(round);
        mPaint.setStyle(Paint.Style.STROKE);
        RectF rectF = new RectF(center - r, center - r, center + r, center + r);
        if (!isNext)
        {// 第一颜色的圈完整，第二颜色跑
            mPaint.setColor(mFirstColor); // 设置圆环的颜色
            canvas.drawCircle(center, center, r, mPaint); // 画出圆环
            mPaint.setColor(mSecondColor); // 设置圆环的颜色
            /**
             * public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)
             * oval :指定圆弧的外轮廓矩形区域。
             * startAngle: 圆弧起始角度，单位为度。 右边水平方向为0度，顺时针为正，-90度，就是0点，0度就是3点
             * sweepAngle: 圆弧扫过的角度，顺时针方向，单位为度。
             * useCenter: 如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。
             * paint: 绘制圆弧的画板属性，如颜色，是否填充等。
             */
            canvas.drawArc(rectF, -90, mProgress, false, mPaint); // 根据进度画圆弧
        } else
        {
            mPaint.setColor(mSecondColor); // 设置圆环的颜色
            canvas.drawCircle(center, center, r, mPaint); // 画出圆环
            mPaint.setColor(mFirstColor); // 设置圆环的颜色
            canvas.drawArc(rectF, -90, mProgress, false, mPaint); // 根据进度画圆弧
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mProgress < 360) {
                    mProgress++;
                    invalidate();
                } else {
                    mProgress = 0;
                    isNext = !isNext;
                    invalidate();
                }

            }
        }, 20);


        super.onDraw(canvas);
    }
}

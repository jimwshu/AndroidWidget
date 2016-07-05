package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.androidwidget.androidwidget.Helpers.UIHelper;
import com.example.androidwidget.androidwidget.R;

/**
 * Created by wangshu on 16/7/5.
 */
public class VoiceView extends View {

    private int mCircleWidth;
    private int firstColor;
    private int secondColor;
    private int mCount;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private Context mContext;
    private Paint mPaint;

    private int mCurrent = 3;

    private int mSpace;

    private RectF rectF;

    public VoiceView(Context context) {
        this(context,null);
    }

    public VoiceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mCircleWidth = UIHelper.dip2px(mContext, 5);
        firstColor = getResources().getColor(android.R.color.black);
        secondColor = getResources().getColor(android.R.color.holo_red_dark);
        mCount = 12;
        mSpace = UIHelper.dip2px(mContext, 3);
        mCurrent = 2;
        mDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (width > height) {
            width = height;
        } else {
            height = width;
        }
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;
        int radius = center - mCircleWidth;
        drawOval(center, radius, canvas);
        drawBitmap(canvas, radius, center);


    }

    /**
     * 绘制中心的图像
     * @param canvas
     */
    private void drawBitmap(Canvas canvas, int radius, int center) {
        RectF rectF = new RectF();
        /**
         * 计算内切正方形的位置 (图片在圆的内部再套一个圆环)
         */
        int relRadius = radius - mCircleWidth / 2;// 获得内圆的半径
        /**
         * 内切正方形的距离顶部 = mCircleWidth + relRadius（1 - √2 / 2） 画示意图可得出
         */
        rectF.left = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;
        /**
         * 内切正方形的距离左边 = mCircleWidth + relRadius - √2 / 2
         */
        rectF.top = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;

        // 我觉得是 √2 / 2 relRadius + left
        rectF.bottom = (int) (rectF.top + Math.sqrt(2) * relRadius);
        rectF.right = (int) (rectF.left + Math.sqrt(2) * relRadius);

        /**
         * 如果图片比较小，那么根据图片的尺寸放置到正中心
         */
        if (mBitmap.getWidth() < Math.sqrt(2) * relRadius)
        {
            rectF.left = (int) (rectF.left + Math.sqrt(2) * relRadius * 1.0f / 2 - mBitmap.getWidth() * 1.0f / 2);
            rectF.top = (int) (rectF.top + Math.sqrt(2) * relRadius * 1.0f / 2 - mBitmap.getHeight() * 1.0f / 2);
            rectF.right = (int) (rectF.left + mBitmap.getWidth());
            rectF.bottom = (int) (rectF.top + mBitmap.getHeight());

        }
        // 绘图
        canvas.drawBitmap(mBitmap, null, rectF, mPaint);

    }

    /**
     * 1. 计算每个item所占的角度   (360 - count * mCubeWidth)/count
     * 2. 绘制第一种颜色的块
     * 3. 绘制第二种颜色的块
     * @param center
     * @param radius
     * @param canvas
     */
    private void drawOval(int center, int radius, Canvas canvas) {

        float angel = (360f - mCount * mSpace)/ mCount;
        RectF rec = new RectF(center - radius, center -radius, center + radius, center + radius);
        mPaint.setColor(firstColor);
        for (int i = 0; i < mCount; i ++) {
            canvas.drawArc(rec, i * mSpace + i * angel, angel, false, mPaint);
        }

        mPaint.setColor(secondColor);
        for (int i = 0; i < mCurrent; i++) {
            canvas.drawArc(rec, i * mSpace + i * angel, angel, false, mPaint);
        }


    }

    public void up() {
        mCurrent++;
        invalidate();
    }

    public void down() {
        mCurrent--;
        invalidate();
    }



    private int downX, upX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                upX = (int) event.getY();
                if (upX > downX) {
                    down();
                } else {
                    up();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //由滑动冲突，具体怎么写法需要去了解~
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }



        return true;
    }
}


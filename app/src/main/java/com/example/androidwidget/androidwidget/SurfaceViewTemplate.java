package com.example.androidwidget.androidwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * Created by wangshu on 15/11/30.
 */
public class SurfaceViewTemplate extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private SurfaceHolder holder;
    private boolean isRunning;
    private Canvas mCanvas;
    private Thread t;

    private int mItemCount = 6;
    String [] mStrs = new String[] {"单反相机","单反相机","单反相机","单反相机","单反相机","单反相机"};

    private int[] mImgs= new int[] {R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher};

    private Bitmap[] mImgsBitmap;

    private int [] mColors = new int[] {0xffffc300, 0xfff17e01, 0xffffc300,0xfff17e01, 0xffffc300,0xfff17e01};

    private RectF mRange = new RectF();

    private int mRadius;

    private Paint mArcPaint;
    private Paint mTextPaint;

    private double mSpeed = 0;
    /**
     * 保证线程之间变量的可见性？？？
     */
    private volatile float mStartAngle = 0;

    public boolean isShouleEnd;

    public boolean isShouleEnd() {
        return isShouleEnd;
    }

    /**
     * 转盘的中心位置
     */
    private int mCenter;

    /**
     * padding取最小值或者paddingleft为主
     */
    private int mPadding;

    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());


    public SurfaceViewTemplate(Context context) {
        super(context);
        init();
    }

    public SurfaceViewTemplate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SurfaceViewTemplate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        holder = getHolder();
        holder.addCallback(this);


        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredHeight(), getMeasuredWidth());

        mPadding = getPaddingLeft();
        //直径
        mRadius = width - mPadding * 2;

        mCenter = width/2;

        setMeasuredDimension(width, width);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);

        mImgsBitmap = new Bitmap[mItemCount];
        for (int i = 0; i < mItemCount; i++) {
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),mImgs[i]);
        }


        isRunning = true;

        t = new Thread(this);
        t.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();

            draw();

            long end = System.currentTimeMillis();

            if (end - start < 50) {
                try {
                    Thread.sleep(50 - (end - start));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        try {
            mCanvas = holder.lockCanvas();

            if (mCanvas != null) {
                //绘制背景
                drawBg();

                //绘制盘块

                float tmpAngle = mStartAngle;

                float sweepAngle = 360/mItemCount;

                for (int i = 0; i < mItemCount; i++) {
                    mArcPaint.setColor(mColors[i]);

                    //绘制盘块
                    mCanvas.drawArc(mRange,tmpAngle, sweepAngle, true, mArcPaint);

                    //绘制文本
                    drawText(tmpAngle,sweepAngle, mStrs[i]);

                    //绘制icon
                    drawIcon(tmpAngle, mImgsBitmap[i]);

                    tmpAngle += sweepAngle;
                }

                mStartAngle += mSpeed;

                //如果点击了停止按钮
                if (isShouleEnd) {
                    mSpeed -= 1;
                }

                if (mSpeed <= 0) {
                    mSpeed = 0;
                    isShouleEnd = false;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (mCanvas != null) {
                holder.unlockCanvasAndPost(mCanvas);
            }
        }
    }


    //控制停下来的位置
    public void panStart(int index) {

        //计算每一项的角度
        float angle = 360/mItemCount;

        //计算传入的index的中奖范围

        float from = 270 - (index + 1) * angle;
        float end = from + angle;

        //设置停下来需要旋转的距离
        float targetFrom = 4 * 360 + from;
        float targetEnd = 4 * 360 + end;

        // v1 - > 0 且每次-1，

        // (v1 + 0) * (v1 + 1) / 2 = targetFrom
        // v1 * v1 + v1 - 2 * targetFrom = 0;  一元二次方程求解

        float v1 = (float) ( (-1 + Math.sqrt(1 + 8*targetFrom))/2 );

        float v2 = (float) ( (-1 + Math.sqrt(1 + 8*targetEnd))/2 );


        mSpeed = v1 + Math.random()*(v2 - v1);
        isShouleEnd = false;
    }

    public void panEnd() {
        mStartAngle = 0;
        isShouleEnd = true;
    }

    //转盘是否在旋转
    public boolean isStart() {
        return mSpeed != 0;
    }


    /**
     * 绘制背景
     */
    private  void drawBg() {
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding/2, mPadding/2, getMeasuredWidth() - mPadding/2, getMeasuredHeight() - mPadding/2), null);

    }

    private void drawText(float temAngle, float sweepAngle, String string) {
        Path path = new Path();
        path.addArc(mRange, temAngle, sweepAngle);

        //利用水平偏移量让文字居中

        float textWidth = mTextPaint.measureText(string);
        int hOffset = (int) (mRadius * Math.PI / mItemCount/2 - textWidth/2);
        int vOffset = mRadius/6/2;

        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
    }

    private void drawIcon(float tmpAngle, Bitmap bitmap) {

        //设置图片的宽度为直径的1/8
        int imgWidth = mRadius / 8;

        float angle = (float)((tmpAngle + 360/mItemCount/2) * Math.PI/180);

        //图片中心点的坐标
        int x = (int)(mCenter + mRadius/2/2 * Math.cos(angle));
        int y = (int)(mCenter + mRadius/2/2 * Math.sin(angle));

        Rect rect = new Rect(x-imgWidth/2, y-imgWidth/2, x+imgWidth/2, y+imgWidth/2);

        mCanvas.drawBitmap(bitmap, null, rect, null);

    }

}

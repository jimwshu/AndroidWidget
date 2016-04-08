package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by wangshu on 16/1/20.
 */
public class MyImageView extends ImageView{

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    private Bitmap bitmap = null;

    private DisplayMetrics displayMetrics;

    /**
     * 最小和最大放大倍数
     */
    float minScale = 1.0f;
    float maxScale = 15.0f;

    /**
     * 三种状态，拖动和放大缩小
     */
    public final static int NONE = 0;
    public final static int DRAG = 1;
    public final static int ZOOM = 2;

    /**
     * 当前状态
     */
    int mode = NONE;

    /**
     * 多点触控时，按下的xy值
     */
    PointF prev = new PointF();
    /**
     * 两个手指之间的中点
     */
    PointF mid = new PointF();

    float dist = 1f;



    public MyImageView(Context context) {
        super(context);
        initView();
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        displayMetrics = getContext().getResources().getDisplayMetrics();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();
        if (bitmapDrawable != null) {
            bitmap = bitmapDrawable.getBitmap();
        }

        this.setScaleType(ScaleType.MATRIX);
        this.setImageBitmap(bitmap);

        if (bitmap != null) {
            center(true,true);
        }

        this.setImageMatrix(matrix);

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 主点按下
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        prev.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    // 副点按下
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dist = spacing(event);
                        // 如果连续两点距离大于10，则判定为多点模式
                        if (spacing(event) > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP: {
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        //savedMatrix.set(matrix);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - prev.x, event.getY()
                                    - prev.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float tScale = newDist / dist;
                                matrix.postScale(tScale, tScale, mid.x, mid.y);
                            }
                        }
                        break;
                }
                MyImageView.this.setImageMatrix(matrix);
                checkView();
                return true;
            }
        });

    }

    /**
     * matrix是一个3*3矩阵
     */
    private void checkView() {
        float [] p = new float [9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScale) {
                matrix.setScale(minScale, maxScale);
            }

            if (p[0] > maxScale) {
                matrix.setScale(maxScale, maxScale);
            }
        }

        center(true, true);
    }

    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt((x * x + y * y));
    }

    /**
     * 两点的中点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    protected void center(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix();
        m.set(matrix);

        /**
         * RectF rect = new RectF(0, 0, width, height);
         *  m.mapRect(rect);
         *这样rect.left，rect.right,rect.top,rect.bottom分别就就是当前屏幕离你的图片的边界的距离。
         */
        RectF rectF = new RectF(0,0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rectF);

        float height = rectF.height();
        float width = rectF.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
            int screenHeight = displayMetrics.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rectF.top;
            } else if (rectF.top > 0) {
                deltaY = -rectF.top;
            } else if (rectF.bottom < screenHeight) {
                deltaY = this.getHeight() - rectF.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = displayMetrics.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rectF.left;
            } else if (rectF.left > 0) {
                deltaX = -rectF.left;
            } else if (rectF.right < screenWidth) {
                deltaX = screenWidth - rectF.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);

    }
}

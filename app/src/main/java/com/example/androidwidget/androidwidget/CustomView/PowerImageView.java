package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.example.androidwidget.androidwidget.R;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Created by wangshu on 16/1/19.
 */
public class PowerImageView extends ImageView implements View.OnClickListener{

    /**
     * GIF的容器
     */
    private Movie mMovie;

    /**
     * 自动播放的图片
     */
    private Bitmap mBitmap;

    /**
     * 是否自动播放
     */
    private boolean autoPlay;

    /**
     * 记录开始播放的时间
     */
    private long mStartTime;

    /**
     * GIF的宽高
     */
    private int mMovieHeight;
    private int mMovieWidth;


    /**
     * 是否在播放状态
     */
    private boolean isPlaying;



    public PowerImageView(Context context) {
        super(context);
    }

    public PowerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PowerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PowerImageView);
        int res = getResourcesId(context, typedArray, attrs);

        if (res != 0) {
            InputStream is = getResources().openRawResource(res);
            mMovie = Movie.decodeStream(is);
            if (mMovie != null) {
                autoPlay = typedArray.getBoolean(R.styleable.PowerImageView_auto_play,false);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                mMovieHeight = bitmap.getHeight();
                mMovieWidth = bitmap.getWidth();
                bitmap.recycle();
                if (!autoPlay) {
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    setOnClickListener(this);
                }
            }
        }

    }

    /**
     * 通过反射拿到ResourcesId？什么原理呢？
     * @param context
     * @param typeArray
     * @param set
     * @return
     */
    private int getResourcesId(Context context, TypedArray typeArray, AttributeSet set) {

        try {
            Field field = TypedArray.class.getDeclaredField("mValue");
            field.setAccessible(true);
            TypedValue typedValue = (TypedValue) field.get("a");
            return typedValue.resourceId;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (typeArray != null) {
                typeArray.recycle();
            }
        }


       /*
       获取resourceId的另一种方法
       for (int i = 0; i < set.getAttributeCount(); i++) {
            if(set.getAttributeName(i).equals("src")) {
                return set.getAttributeResourceValue(i, 0);
            }
        }*/
        return 0;
    }


    public void onClick(View view) {
        if (view.getId() == getId()) {
            isPlaying = true;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie != null) {
            setMeasuredDimension(mMovieWidth, mMovieHeight);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie == null) {
            super.onDraw(canvas);
        } else {
            if (autoPlay) {
                playGif(canvas);
                invalidate();
            } else {
                if (isPlaying) {
                    if (playGif(canvas)) {
                        isPlaying = false;
                    }
                    invalidate();
                } else {
                    mMovie.setTime(0);
                    mMovie.draw(canvas, 0, 0);
                    int offsetX = (mMovieWidth - mBitmap.getWidth())/2;
                    int offsetY = (mMovieHeight - mBitmap.getHeight())/2;
                    canvas.drawBitmap(mBitmap, offsetX, offsetY, null);
                }
            }
        }
    }

    /**
     * 一直不断地在重绘，第一次重绘，mStartTime = now；会一直等于开始时间，
     * 第二次重绘，就可以计算当前播放的时间了now赋 新值，当前的时间
     * now - mStartTime 就是播放时间
     * @param canvas
     * @return
     */
    private boolean playGif(Canvas canvas) {

        /**
         * 从开机到现在的毫秒数
         */
        long now = SystemClock.uptimeMillis();

        if (mStartTime == 0) {
            mStartTime = now;
        }

        int duration = mMovie.duration();

        if (duration == 0) {
            duration = 1000;
        }

        int relTime = (int)((now - mStartTime)%duration);
        mMovie.setTime(relTime);
        mMovie.draw(canvas, 0 , 0);

        if (now - mStartTime >= duration) {
            mStartTime = 0;
            return  true;
        }

        return false;
    }
}

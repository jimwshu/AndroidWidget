package com.example.androidwidget.androidwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by wangshu on 16/7/12.
 * 1. 自定义View里有四个Relativilayout，通过onMeasure和onLayout来保证每个RelativitLayout大小与屏幕的大小一致，每次通过mCurrentPosition来展示某一个Relativilayout
 * 2. 判断滑动，向上滑，向下滑等，并且通过滑动来展示某一个Relativilayout
 *
 * 涉及到的知识点：
 *    1. VelocityTracker的使用（加速度）
 *    2. Scroller 辅助类，滑动的辅助类
 *    3. onMeasure中，measureChild，measureChildWithMargins()的使用以及不同
 *    4. onLayout中，child.onLayout,通过LayoutParams来设置该View的宽高
 *    5. getScrollY(),event.getY(),getRawY(),
 *
 */
public class VerticalLinearLayout extends ViewGroup {

    //屏幕高度
    private int mScreenHeight;

    // 开始滑动的 y 和 结束的y坐标
    private int mStartY;
    private int mEndY;

    //记录滑动过程中的Y坐标
    private int mLastY;

    //计算加速度的类
    private VelocityTracker mVelocityTracker;
    //滑动的类 滑动的辅助类
    private Scroller mScroller;
    private boolean isScrolling;

    private int mCurrentPage = 0;

    private Context mContext;



    public VerticalLinearLayout(Context context) {
        this(context, null);
    }

    public VerticalLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mScroller = new Scroller(mContext);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenHeight = metrics.heightPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            //measureChildWithMargins() 该方法需要重载 generateDefaultLayoutParams() 返回一个继承自 ViewGroup.MarginLayoutParams 的 布局类（例如 LinearLayout.LayoutParams),
            //如果未重载将导致 本 View 的XML属性 layout_margin 失效并且不能使用 measureChildWithMargins()
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
            layoutParams.height = childCount * mScreenHeight;
            setLayoutParams(layoutParams);

            for (int i = 0; i < childCount; i ++) {
                View child = getChildAt(i);
                if (child.getVisibility() != View.GONE) {
                    child.layout(l, i * mScreenHeight, r, (i + 1) * mScreenHeight);
                }
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isScrolling) {
            return super.onTouchEvent(event);
        }

        int action = event.getAction();
        int y = (int) event.getY();
        obtainTraker(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // View 有两个内部属性mScrollX，mScrollY，通过getScrollX()，getScrollY()得到。
                //在滑动过程中，mScrollX的值总是等于View左边缘和View内容左边缘在水平方向的距离（注意有正负）
                mStartY = getScrollY();
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                int dy = mLastY - y;

                int scrollY = getScrollY();

                if (dy < 0 && scrollY + dy < 0) {
                    dy = -scrollY;
                }

                if (dy > 0 && dy + scrollY > getHeight() - mScreenHeight) {
                    dy = getHeight() - mScreenHeight -scrollY;
                }
                scrollBy(0, dy);
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                mEndY = getScrollY();
                int dScrollY = mEndY - mStartY;

                if (wantScrollToNext()) {
                    if (shouldScrollerToNext()) {
                        mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - dScrollY);
                    } else {
                        mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    }
                }

                if (wantScrollToPre()) {
                    if (shouldScrollerToPre()) {
                        mScroller.startScroll(0, getScrollY(), 0, -mScreenHeight - dScrollY);
                    } else {
                        mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    }
                }

                isScrolling = true;
                postInvalidate();
                recycleTraker();
                break;

        }

        return true;
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        } else {
            int position = getScrollY()/mScreenHeight;
            if (position != mCurrentPage) {
                mCurrentPage = position;
            }
            isScrolling = false;
        }
    }

    private boolean wantScrollToNext() {
        return mEndY > mStartY;
    }

    private boolean shouldScrollerToNext() {
        return mEndY - mStartY > mScreenHeight/2 || Math.abs(getTraker()) > 600;
    }

    private boolean wantScrollToPre() {
        return mEndY < mStartY;
    }

    private boolean shouldScrollerToPre() {
        return -mEndY + mStartY > mScreenHeight/2 || Math.abs(getTraker()) > 600;
    }



    /**
     * 加速度类的初始化
     * @param event
     */
    private void obtainTraker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 获得加速度
     */

    private int getTraker() {
        return (int) mVelocityTracker.getYVelocity();
    }

    private void recycleTraker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

}

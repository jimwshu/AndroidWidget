package com.example.androidwidget.androidwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidwidget.androidwidget.Helpers.UIHelper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wangshu on 16/7/14.
 *
 * 1. dispatchDraw 会在onDraw方法中调用，去绘制子元素，先执行完onDraw，再去执行dispachDraw（暂时是这样，等重绘的时候再看看）
 * 2. onSizeChange会在页面大小改变的时候调用   ((1) 在Activity onCreate方法中初始化了View，此时会调用View的构造方法 ,并 调用了View 的onFinishInflate
 *                                          (2) 在执行完 Activity的 onPostResume方法之后(第一次onResume之后)才真正开始了View的绘制工作： onAttachedToWindow --> onMeasure -->onSizeChanged --> onLayout --> onDraw)
 * 3. 如果要支持在XML里设置显示Tab个数，可以在onFinishInflate内设置我们需要的参数（当然其实也可以在onMeasure中使用，再往前可以在构造函数中使用，因为参数是固定的。
 * 但是如果在onMeasure中使用的话，有时候如果我们调用了requestLayout 会重新调用onMeasure方法，会重复计算固定的参数值，没有这个必要，所以建议在构造函数或者onFinishInflate中使用)
 * 4. 页面大小改变的时候回调用onSizeChange方法，里面可以初始化一些与控件的宽高有关的方法，其实这些方法也可以放在onMeasure，onLayout，onDraw中去计算，但是可能会引起重复计算，
 * 而该回调方法又返回了控件的宽和高（在onMeasure和onLayout同样可以通过方法获取）
 * 5. 如果自定义View中用了某种回调方法，一定要定义接口给用户使用
 *
 */
public class ViewPagerIndicator extends LinearLayout {

    // 三角形箭头宽度所占的比例以及宽高
    private final static float INDICATOR_WIDTH_RADIUS = 1/6f;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    //初始的距离
    private int mInitIndicatorWidth;
    // 后续的移动距离
    private int mTranslationX;


    private Paint mPaint;
    private Path mPath;

    private int mVisibleCount = 3;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffff0000"));
        mPaint.setStyle(Paint.Style.FILL);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_visible_table_count, 3);
        a.recycle();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e("tag;", "onFinishInflate");
        int cCount = getChildCount();
        if (cCount <= 0) {
            return;
        }

        int screenWidth = getScreenWidth();

        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp= (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = screenWidth/mVisibleCount;
            view.setLayoutParams(lp);
        }

    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * draw()方法实现的功能如下：
     1 、绘制该View的背景
     2 、为显示渐变框做一些准备操作(见5，大多数情况下，不需要改渐变框)
     3、调用onDraw()方法绘制视图本身   (每个View都需要重载该方法，ViewGroup不需要实现该方法)
     4、调用dispatchDraw ()方法绘制子视图(如果该View类型不为ViewGroup，即不包含子视图，不需要重载该方法)
     值得说明的是，ViewGroup类已经为我们重写了dispatchDraw ()的功能实现，应用程序一般不需要重写该方法，但可以重载父类
     函数实现具体的功能。

     4.1 dispatchDraw()方法内部会遍历每个子视图，调用drawChild()去重新回调每个子视图的draw()方法(注意，这个
     地方“需要重绘”的视图才会调用draw()方法)。值得说明的是，ViewGroup类已经为我们重写了dispatchDraw()的功能
     实现，应用程序一般不需要重写该方法，但可以重载父类函数实现具体的功能。
     * @param canvas
     */

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.e("tag;", "dispatchDraw");

        canvas.save();
        canvas.translate(mInitIndicatorWidth + mTranslationX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("tag;", "onDraw");

        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.e("tag;", "onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
        mIndicatorWidth = (int) (w/mVisibleCount * INDICATOR_WIDTH_RADIUS);

        mIndicatorHeight = mIndicatorWidth/2 - 2;

        mInitIndicatorWidth =  w/mVisibleCount/2 - mIndicatorWidth/2;

        initTriangle();




    }

    private void initTriangle() {
        mPath = new Path();
        mPath.moveTo(0,0);
        mPath.lineTo(mIndicatorWidth, 0);
        mPath.lineTo(mIndicatorWidth/2, -mIndicatorHeight);
        mPath.close();
    }

    /**
     * 假设三角形指示器在第一个，总共有六个，显示四个，scrollto的话，就相当于把整个view移动了x，y，那么此时的指示器还是在第一个下面，已经被移出了屏幕
     * 所以还要将指示器移动，所以要调用invalidate，从而调用dispatchDraw来移动指示器
     *
     * 移动指示器，并且重绘
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {
        int width = getWidth() / mVisibleCount;
        mTranslationX = (int) (width * (position + offset));

        if (position >= mVisibleCount-2 && getChildCount() > mVisibleCount && offset > 0 && position!=(getChildCount() - 2)) {
            this.scrollTo((int) (width * offset + (position - (mVisibleCount - 2)) * width), 0);
        }

        invalidate();
    }

    private List<String> mTitles;

    public void setVisibleCount(int count) {
        this.mVisibleCount = count;
    }

    public void setTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            this.removeAllViews();
            this.mTitles = titles;
            for (String title : mTitles) {
                TextView tv = generateView(title);
                addView(tv);
            }
        }
    }

    private TextView generateView(String title) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth()/mVisibleCount;
        tv.setGravity(Gravity.CENTER);
        tv.setText(title);
        tv.setTextColor(0x77ffffff);
        tv.setTextSize(UIHelper.dip2px(getContext(), 5));
        tv.setLayoutParams(lp);
        return tv;
    }

    public interface PageChangeListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
        public void onPageSelected(int position);
        public void onPageScrollStateChanged(int state);
    }

    private PageChangeListener mListener;

    public void setPageChangeListener(PageChangeListener listener) {
        this.mListener = listener;
    }


    private ViewPager mViewPager;
    public void setViewPager(ViewPager viewPager, int pos) {
        this.mViewPager = viewPager;

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //移动的距离是  tabWidth * positionOffset + tabWidth * position
                scroll(position, positionOffset);
                if (mListener != null) {
                    mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });

        mViewPager.setCurrentItem(pos);

    }


}

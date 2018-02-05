package com.example.androidwidget.androidwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.example.androidwidget.androidwidget.Helpers.UIHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangshu on 16/7/6.
 * 1。学习到在ViewGroup中动态添加View，并且设置位置以及边距的方法
 * 2。事件传递机制， action_down方法，子view并不会拦截，除开在子View返回true，否则是默认不拦截的，也就是说如果都使用默认的话，会先执行最底层的action_down，然后一层一层上报，每个父View都会执行
 * action_down方法,但是如果是其他的，action_move,等，子View是默认消费的，也就是说父View并不会执行自己的action_move方法，只有最底层的那个view会执行action_move方法
 * 此处是在父ViewGroup要自己执行action_move方法,并且消费掉，所以此处应该返回true，
 * 3。更健壮的做法应该是，哪个事件需要自己消费就返回true，否则返回false
 * 4。dispatchDraw方法会在onDraw里面执行，并且会后执行
 * 5。path.close会形成一个闭环
 * 6。canvas save restore translate方法的应用
 * 7。mPaint.setStyle的应用
 *
 *
 *
 * Activity 里面有个GestureLockViewGroup，GestureLockViewGroup是由若干个LockView组成的。
 # 默认情况下

 - 触发Touch事件（Action_down)事件
 - DockerView（先会到达顶级View）——》Activity，然后分发到Activity，触发Activity.dispathTouchEvent方法
 - Activity.dispathTouchEvent方法返回false，调用Activity.intercepterTouchEvent方法
 - Activity.intercepterTouchEvent方法返回false，事件传递到GestureLockViewGroup
 - 触发GestureLockViewGroup.dispathTouchEvent方法
 - GestureLockViewGroup.dispathTouchEvent方法返回false，调用GestureLockViewGroup.intercepterTouchEvent方法
 - GestureLockViewGroup.intercepterTouchEvent方法返回false，事件传递到LockView
 - 调用LockView.dispathTouchEvent方法，返回false，(如果是系统的View，比如bottom 可点击的view，会返回true，这里是自定义View，会返回false)
 - 此时已经是最后一个控件，虽然不消费该事件，但是也会调用LockView.onTouchEvent方法
 - 此时LockView.onTouchEvent返回false，事件传递给其的上级，也就是GestureLockViewGroup，并调用GestureLockViewGroup.onTouchEvent方法
 - GestureLockViewGroup.onTouchEvent方法返回false，事件传递给其的上级，也就是Activity，并调用Activity.onTouchEvent方法
 - activity是最上层（忽略dockerView，phoneWindow等上层），事件向上传递终止
 - 因为此时action_down方法已经交由activity处理，此后的action_move,action_up就会直接调用activity的onTouchEvent方法，而不会去调用其下级的方法了

 # 现在的情形是GestureLockViewGroup需要处理滑动事件

 两种方法：
 1 修改GestureLockViewGroup.intercepterTouchEvent方法，让其返回true
 2  修改GestureLockViewGroup.onTouchEvent方法，让其返回true
 此时流程如下：

 - 触发Touch事件（Action_down)事件
 - DockerView（先会到达顶级View）——》Activity，然后分发到Activity，触发Activity.dispathTouchEvent方法
 - Activity.dispathTouchEvent方法返回false，调用Activity.intercepterTouchEvent方法
 - Activity.intercepterTouchEvent方法返回false，事件传递到GestureLockViewGroup
 - 触发GestureLockViewGroup.dispathTouchEvent方法
 - GestureLockViewGroup.dispathTouchEvent方法返回false，调用GestureLockViewGroup.intercepterTouchEvent方法
 - GestureLockViewGroup.intercepterTouchEvent方法返回true，
 - 意味着GestureLockViewGroup拦截了该事件，该事件不会向下传递，就会调用GestureLockViewGroup.onTouchEvent方法
 - （GestureLockViewGroup.onTouchEvent方法默认返回false，如果不修改成true的话，事件传递给其的上级，也就是Activity，并调用Activity.onTouchEvent方法，此时过程类似于12，13）
 - GestureLockViewGroup.onTouchEvent方法修改成true，意味着事件在此处已经进行了消费
 - 因为此时action_down方法已经交由GestureLockViewGroup消费，此后的action_move,action_up就会直接调用GestureLockViewGroup的onTouchEvent方法，而不会去调用其下级的方法了

 第二种方法：
 1 修改GestureLockViewGroup.onTouchEvent方法，让其返回true

 - 触发Touch事件（Action_down)事件
 - DockerView（先会到达顶级View）——》Activity，然后分发到Activity，触发Activity.dispathTouchEvent方法
 - Activity.dispathTouchEvent方法返回false，调用Activity.intercepterTouchEvent方法
 - Activity.intercepterTouchEvent方法返回false，事件传递到GestureLockViewGroup
 - 触发GestureLockViewGroup.dispathTouchEvent方法
 - GestureLockViewGroup.dispathTouchEvent方法返回false，调用GestureLockViewGroup.intercepterTouchEvent方法
 - GestureLockViewGroup.intercepterTouchEvent方法返回false，事件传递到LockView
 - 调用LockView.dispathTouchEvent方法，返回false，(如果是系统的View，比如bottom 可点击的view，会返回true，这里是自定义View，会返回false)
 - 此时已经是最后一个控件，虽然不消费该事件，但是也会调用LockView.onTouchEvent方法
 - （如果此时的LockView是可点击或者可触摸的，那么它会默认消费掉该事件，也即是LockView.onTouchEvent返回true，事件在该View上被消费，不会往上回传）
 - 但是此时LockView.onTouchEvent返回false（并不是类似于Button等可点击的View），事件传递给其的上级，也就是GestureLockViewGroup，并调用GestureLockViewGroup.onTouchEvent方法
 - GestureLockViewGroup.onTouchEvent方法修改成true，意味着事件在此处已经进行了消费
 - 因为此时action_down方法已经交由GestureLockViewGroup消费，此后的action_move,action_up就会直接调用GestureLockViewGroup的onTouchEvent方法，而不会去调用其下级的方法了

 两种方法区别，第二种方法还是会执行其下级的action_down事件，但是第一种不会，直接在GestureLockViewGroup拦截了。
 拦截只是将事件拦截在此处，不往下传递，但是，如果该View.onTouchEvent返回false，那么事件还是会往上传递，依次调用父布局的onTouchEvent方法，直到有某一个的onTouchEvent方法返回true或者到达了最顶层View为止，此时，事件在哪里消费还是不确定的，而消费是指此事件在这里终止了，不会往下传递，也不会往上传递。
 *
 *
 *
 *
 */
public class GestureLockViewGroup extends RelativeLayout {

    private int mCount = 4;
    private GestureLockView[] mGestureLockViews;

    private int mAnswer[] = new int[]{1, 2, 5, 8, 9};

    private List<Integer> mChoose = new ArrayList<Integer>();

    private Paint mPaint;

    private int mGestureLockViewWidth;
    private int mPadding;

    private int mWidth;
    private int mHeight;

    private Path mPath;


    // 指引线的两个点
    private int mLastX;
    private int mLastY;

    private Point mTarget = new Point();

    private Context mContext;

    /**
     * 最大尝试次数
     */
    private int mTryTimes = 4;




    /**
     * GestureLockView无手指触摸的状态下内圆的颜色
     */
    private int mNoFingerInnerCircleColor = 0xff00ff00;
    /**
     * GestureLockView无手指触摸的状态下外圆的颜色
     */
    private int mNoFingerOuterCircleColor = 0xff00ff00;
    /**
     * GestureLockView手指触摸的状态下内圆和外圆的颜色
     */
    private int mFingerOnColor =  0xff0000ff;
    /**
     * GestureLockView手指抬起的状态下内圆和外圆的颜色
     */
    private int mFingerUpColor = 0xff68228b;


    public GestureLockViewGroup(Context context) {
        this(context, null);
    }

    public GestureLockViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        mContext = context;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mHeight = mWidth = mHeight > mWidth ? mWidth : mHeight;

        if (mGestureLockViews == null) {
            mGestureLockViews = new GestureLockView[mCount * mCount];
            mGestureLockViewWidth = 4 * mWidth / (5 * mCount + 1);
            mPadding = (int) (0.25 * mGestureLockViewWidth);

            for (int i = 0; i < mGestureLockViews.length; i++) {
                mGestureLockViews[i] = new GestureLockView(mContext);
                mGestureLockViews[i].setId(i + 1);
                RelativeLayout.LayoutParams params = new LayoutParams(mGestureLockViewWidth, mGestureLockViewWidth);

                //不是每行的第一个，设置为在上一个的右边
                if (i % mCount != 0) {
                    params.addRule(RIGHT_OF, mGestureLockViews[i - 1].getId());
                }
                //不是第一行，设置为上一行的下边
                if (i > mCount - 1) {
                    params.addRule(BELOW, mGestureLockViews[i - mCount].getId());
                }


                int rightMargin = mPadding;
                int bottomMargin = mPadding;
                int leftMagin = 0;
                int topMargin = 0;

                /**
                 * 每个View都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
                 */
                if (i >= 0 && i < mCount)// 第一行
                {
                    topMargin = mPadding;
                }
                if (i % mCount == 0)// 第一列
                {
                    leftMagin = mPadding;
                }

                params.setMargins(leftMagin, topMargin, rightMargin, bottomMargin);
                mGestureLockViews[i].setMode(GestureLockView.STATUS_NO_FINGER);
                addView(mGestureLockViews[i], params);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();


        switch (action) {
            case MotionEvent.ACTION_UP:

                mPaint.setColor(mFingerUpColor);
                mPaint.setAlpha(50);

                // 将终点设置位置为起点，即取消指引线
                mTarget.x = mLastX;
                mTarget.y = mLastY;

                // 改变子元素的状态为UP
                changeItemMode();

                // 计算每个元素中箭头需要旋转的角度
                for (int i = 0; i + 1 < mChoose.size(); i++) {
                    int childId = mChoose.get(i);
                    int nextChildId = mChoose.get(i + 1);

                    GestureLockView startChild = (GestureLockView) findViewById(childId);
                    GestureLockView nextChild = (GestureLockView) findViewById(nextChildId);

                    int dx = nextChild.getLeft() - startChild.getLeft();
                    int dy = nextChild.getTop() - startChild.getTop();
                    // 计算角度
                    int angle = (int) Math.toDegrees(Math.atan2(dy, dx)) + 90;
                    startChild.setArrowDegree(angle);
                }

                break;
            case MotionEvent.ACTION_DOWN:
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                mPaint.setColor(mFingerOnColor);
                mPaint.setAlpha(50);
                mPaint.setStrokeWidth(UIHelper.dip2px(mContext, 5));
                GestureLockView child = getChildIdByPos(x, y);
                if (child != null) {
                    int cid = child.getId();
                    if (!mChoose.contains(cid)) {
                        mChoose.add(cid);
                        child.setMode(GestureLockView.STATUS_FINGER_ON);

                        // 设置指引线的起点
                        mLastX = child.getLeft() / 2 + child.getRight() / 2;
                        mLastY = child.getTop() / 2 + child.getBottom() / 2;
                        if (mChoose.size() == 1)// 当前添加为第一个
                        {
                            mPath.moveTo(mLastX, mLastY);
                        } else
                        // 非第一个，将两者使用线连上
                        {
                            mPath.lineTo(mLastX, mLastY);
                        }
                    }
                }
                mTarget.x = x;
                mTarget.y = y;
                break;
        }
        invalidate();

        //事件传递机制， action_down方法，子view并不会拦截，除开在子View返回true，否则是默认不拦截的，也就是说如果都使用默认的话，会先执行最底层的action_down，然后一层一层上报，每个父View都会执行
        //action_down方法,但是如果是其他的，action_move,等，子View是默认消费的，也就是说父View并不会执行自己的action_move方法，只有最底层的那个view会执行action_move方法

        // 此处是在父View要自己执行action_move方法,并且消费掉，所以此处应该返回true，更健壮的做法应该是，哪个事件需要自己消费就返回true，否则返回false
        boolean b = super.onTouchEvent(event);
        Log.e("tag:", "view group ontouch " + b);
        return true;
    }

    /**
     * 绘制子视图调用的方法
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
        //绘制指引线
        if (mChoose.size() > 0) {
            if (mLastX != 0 && mLastY != 0) {
                canvas.drawLine(mLastX, mLastY, mTarget.x,
                        mTarget.y, mPaint);
            }
        }
    }

    private void changeItemMode() {
        for (GestureLockView gestureLockView : mGestureLockViews) {
            if (mChoose.contains(gestureLockView.getId())) {
                gestureLockView.setMode(GestureLockView.STATUS_FINGER_UP);
            }
        }
    }

    /**
     * 做一些必要的重置
     */
    private void reset() {
        mChoose.clear();
        mPath.reset();
        for (GestureLockView gestureLockView : mGestureLockViews) {
            gestureLockView.setArrowDegree(-1);
            gestureLockView.setMode(GestureLockView.STATUS_NO_FINGER);
        }
    }

    /**
     * 点（x,y)是否在view里
     */
    private boolean checkPosition(GestureLockView view, int posX, int posY) {
        int left = view.getLeft();
        int right = view.getRight();
        int top = view.getTop();
        int bottom = view.getBottom();
        if (posX >= left && posX <= right && posY >= top && posY <= bottom) {
            return true;
        }
        return false;
    }

    private GestureLockView getChildIdByPos(int x, int y) {
        for (GestureLockView gestureLockView : mGestureLockViews) {
            if (checkPosition(gestureLockView, x, y)) {
                return gestureLockView;
            }
        }
        return null;
    }

}

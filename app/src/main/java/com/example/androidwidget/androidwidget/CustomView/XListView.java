package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.example.androidwidget.androidwidget.Helpers.LogHelper;
import com.example.androidwidget.androidwidget.Helpers.UIHelper;
import com.example.androidwidget.androidwidget.R;

import android.util.Log;




/**
 *
 * 实现方式是 1.刚开始的时候headerview的高度是0，在第一个显示的item继续往下滑，
 * headerview随着下滑的高度不断增加，此时箭头向下，到达一定高度后，箭头变为向上，
 * 继续向下到达一定高度后，箭头隐藏，headerview变小，直到为0.
   2.刚开始footerview不显示，拉取成功后，
 hasMore为true时，显示在最下边，继续上拉，加载更多，
 再通过hasMore判断是否显示footerview，原理也是setBootomMargin
 ，一开始是0，随着手指上拉，margin越来越大，到达一定高度，提示语改变，松开后，bootommargin由最大值慢慢变为0
 *
 *
 * 另一种实现方式是将headerview的padding置为负数
 * Created by wangshu on 15/11/5.
 */
public class XListView extends ListView implements AbsListView.OnScrollListener{

    /**
     * save last event Y
     */
    private float mLastY = -1;

    /**
     * TODO shayisi ?
     * suppert iOS like pull feature
     */
    private final static float OFFSET_RADIO = 1.8f;

    /**
     * total list items, used to detect is at the bottom of listview
     */
    private int mTotalItemCount;

    //todo 是否是上拉加载更多，否就是点击才能加载更多？ 有待验证
    private boolean mEnablePullLoad = true;

    //todo 什么含义呢？
    private boolean mPullLoading;


    private boolean mIsFooterReady = false;
    /**
     * enable or disable pull down refresh feature.default is enable
     */
    private boolean mEnablePullRefresh = true;

    /**
     * the current listview's state is refreashing or not
     */
    private boolean mPullRefreshing = false;

    private boolean mHeaderPlaceHolderEnable = false;

    //实现View平滑滚动类
    private Scroller mScroller;

    //listview的头部
    private XListViewHeader mHeaderView;
    private RelativeLayout mHeaderViewContent;
    /**
     * header view's height
     */
    private int mHeaderViewHeight;

    //ListView的footer
    private XListViewFooter mFooterView;

    /**
     * for mScroller ,scroll back from header or footer.
     */
    private int mScrollBack;

    /**
     * scorll back type
     */
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    /**
     * scroll back duration
     */
    private final static int SCROLL_DURATION = 400;

    /**
     * 向上滑动的时候超过50px，触发家在更多
     * when pull up >=50px at bottom, trigger load more.
     */
    private final static int PULL_LOAD_MORE_DELTA = 50;

    /**
     * the interface to trigger refresh and load more.
     */
    private IXListViewListener mListViewListener;

    /**
     * user's scroll listener
     */
    private OnScrollListener mScrollListener;

    private OnScrollDirectionListener mScrollDirection;

    /**
     * record first visible item top
     */
    private int mOldFirstVisibleItemTop = -1;
    /**
     * record first visible item
     */
    private int mOldFirstVisibleItem = -1;
    /**
     * minimum scroll distance when track scroll direction
     */
    private final static int MIN_SCROLL_DIFF = 20;

    /**
     * Listener for scroll direction, can use {@link SimpleScrollDirectionListener} by default
     * @param listener
     */
    public void setOnScrollDirectionListener(OnScrollDirectionListener listener) {
        this.mScrollDirection = listener;
    }

    /**
     * @param context
     */
    public XListView(Context context) {
        this(context, null);
    }

    public XListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XListView);
        mEnablePullLoad = typedArray.getBoolean(R.styleable.XListView_xlvLoadEnable, mEnablePullLoad);
        mEnablePullRefresh = typedArray.getBoolean(R.styleable.XListView_xlvRefreshEnable, mEnablePullRefresh);
        mHeaderPlaceHolderEnable = typedArray.getBoolean(R.styleable.XListView_xlvHeaderPlaceHolderEnable, mHeaderPlaceHolderEnable);
        typedArray.recycle();
        setPullLoadEnable(mEnablePullLoad);
        setPullRefreshEnable(mEnablePullRefresh);
        addHead();
    }


    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);


        // init header view
        mHeaderView = new XListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView
                .findViewById(R.id.xlistview_header_content);
        // mHeaderTimeView = (TextView)
        // mHeaderView.findViewById(R.id.xlistview_header_time);

        // init footer view
        mFooterView = new XListViewFooter(context);

        // init header height
        /*
        ViewTreeObserver 监听view的变化
        包含了以下几个接口：
        interfaceViewTreeObserver.OnGlobalFocusChangeListener
        interfaceViewTreeObserver.OnGlobalLayoutListener
        interface ViewTreeObserver.OnPreDrawListener
        interfaceViewTreeObserver.OnScrollChangedListener
        interfaceViewTreeObserver.OnTouchModeChangeListener
        */
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                //获得header的真实高度
                mHeaderViewHeight = mHeaderViewContent.getHeight();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * enable or disable pull up load more feature.
     * if disable, click to load more
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad){
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        } else{
            mPullLoading = false;

            mFooterView.show();
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    public void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(XListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    private void addHead() {
        if(mHeaderPlaceHolderEnable) {
            View v = getHeadViewWhenParallax(getContext());
            if(v != null) {
                addHeaderView(v);
            }
        }
        addHeaderView(mHeaderView);
    }


    protected View getHeadViewWhenParallax(Context ctx) {

        View view = new View(ctx);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIHelper.dip2px(ctx, 48));
        view.setLayoutParams(lp);
        return view;
    }


    /**
     * Auto Pull down to refresh, used to init listview data
     */
    public void initData() {
        mHeaderView.setVisiableHeight(mHeaderView.measureHeight);
        mPullRefreshing = true;
        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
        mFooterView.hide();
        if (mListViewListener != null) {
            mListViewListener.onRefresh();
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {

        // make sure XListViewFooter is the last footer view, and only add once.
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    public void setFooterHintText(String text) {
        if (mFooterView != null) {
            mFooterView.setHintText(text);
        }
    }

    public boolean isRefreshing(){
        return mPullRefreshing;
    }

    public boolean isEnablePullLoad(){
        return mEnablePullLoad && mFooterView!=null;
    }

    public boolean isLoadingMore(){
        return mPullLoading;
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {

        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {

        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void updateHeaderHeight(float delta) {

        if (mEnablePullRefresh && !mPullRefreshing) {
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(XListViewHeader.STATE_NORMAL);
            }
            mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
        }
    }

    private void updateFooterHeight(float delta) {

        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load more.
                mFooterView.setState(XListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(XListViewFooter.STATE_NORMAL);
            }
            mFooterView.setBottomMargin(height);
        } else {
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
        }

    }

    private void resetFooterHeight() {

        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }



    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore(boolean normal) {
        // if (mFooterView.getVisibility() == View.GONE)
        // mFooterView.setVisibility(View.VISIBLE);
        mFooterView.show();
        if (mPullLoading == true) {
            mPullLoading = false;
            if(normal) {
                mFooterView.setState(XListViewFooter.STATE_NORMAL);
            } else {
                mFooterView.setState(XListViewFooter.STATE_FAIL);
            }
        }
    }

    public void loadNoMore() {
        // mFooterView.setVisibility(View.GONE);

        mFooterView.hide();
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        // mHeaderTimeView.setText(time);
    }

    /**
     * onScrolling hook
     */
    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
			/* 手指滑动速率 */
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0
                        && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1
                        && (mFooterView.getBottomMargin() > 0 || deltaY < 0) && !mPullLoading) {
                    // 新增条件(&& !mPullLoading),解决在加载过程中,再次拖动还会显示文字的问题
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        mPullRefreshing = true;
                        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        boolean ret = true;
        try { //预防越界crash，问题来源于点击通知栏推送进入单帖页再返回滑动时出现，具体原因待跟踪
            ret = super.onTouchEvent(ev);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    public void setXListViewListener(IXListViewListener l) {
        mListViewListener = l;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    public OnScrollListener getOnScrollListener() {
        return mScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        //visible count 0 means no direction
        if(visibleItemCount == 0){
            return;
        }

        calculateDirection(view, firstVisibleItem, visibleItemCount,
                totalItemCount);
    }

    private void calculateDirection(AbsListView view, int firstVisibleItem,
                                    int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem==0 || firstVisibleItem+visibleItemCount==totalItemCount){
            return;
        }
        if(mScrollDirection == null) {
            return;
        }
        if (firstVisibleItem == mOldFirstVisibleItem) {
            int top = view.getChildAt(0).getTop();
            if (top > mOldFirstVisibleItemTop
                    && Math.abs(top - mOldFirstVisibleItemTop) > MIN_SCROLL_DIFF) {
                // scroll up
                mScrollDirection.onScrollUp();
                mOldFirstVisibleItemTop = top;
            } else if (top < mOldFirstVisibleItemTop
                    && Math.abs(top - mOldFirstVisibleItemTop) > MIN_SCROLL_DIFF) {
                // scroll down
                mScrollDirection.onScrollDown();
                mOldFirstVisibleItemTop = top;
            }
        } else {
            View child = view.getChildAt(0);
            if (child != null) {
                mOldFirstVisibleItem = firstVisibleItem;
                mOldFirstVisibleItemTop = child.getTop();
            }
        }
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener
    {
        public void onXScrolling(View view);
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface IXListViewListener
    {
        /**
         * 列表加载本地历史缓存
         */
        public void onInitHistoryData();

        /**
         * 用户手工加载最新的第一页的数据,下拉刷新列表
         */
        public void onRefresh();

        /**
         * 加载跟多数据,列表底部点击/上拉加载更多
         */
        public void onLoadMore();
    }

    public interface OnScrollDirectionListener {
        /**
         * Indicated that user touch from top to bottom.
         */
        public void onScrollUp();
        /**
         * Indicated that user touch from bottom to top.
         */
        public void onScrollDown();
    }
}

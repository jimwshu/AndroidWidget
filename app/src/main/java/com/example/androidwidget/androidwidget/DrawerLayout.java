package com.example.androidwidget.androidwidget;

import android.content.Context;
import android.provider.Contacts;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidwidget.androidwidget.Helpers.UIHelper;

/**
 * Created by wangshu on 16/7/26.
 */
public class DrawerLayout extends ViewGroup {

    private View mContentView;
    private View mMenuView;

    private static final int MIN_DRAWER_MARGIN = 60;

    private static final int MIN_FLING_VELOCITY = 400;

    private int mMinDargMargin;
    private ViewDragHelper mViewDragHelper;

    private float mLeftMenuOnScreen;



    public DrawerLayout(Context context) {
        this(context, null);
    }

    public DrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        final float mMinVel = UIHelper.dip2px(getContext(), MIN_FLING_VELOCITY);
        mMinDargMargin = UIHelper.dip2px(getContext(), MIN_DRAWER_MARGIN);

        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return false;
            }
        });

        //
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);



    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}

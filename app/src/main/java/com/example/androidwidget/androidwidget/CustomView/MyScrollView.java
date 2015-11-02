package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;


/**
 * Created by wangshu on 15/11/2.
 */
public class MyScrollView extends ScrollView {

    private MyScrollerListener myScrollerListener;

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, MyScrollerListener listener) {
        this(context, attrs);
        this.myScrollerListener = listener;
    }

    public void setMyScrollerListener(MyScrollerListener listener) {
        if (listener instanceof MyScrollerListener) {
            this.myScrollerListener = listener;
        }
    }

    public interface MyScrollerListener {
        public void onScrollerChanged (int l, int t, int oldl, int oldt);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        if (myScrollerListener != null) {
            myScrollerListener.onScrollerChanged(l, t, oldl, oldt);
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void removeListener () {
        this.myScrollerListener = null;
    }
}

package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidwidget.androidwidget.Helpers.LogHelper;
import com.example.androidwidget.androidwidget.R;

import java.util.zip.Inflater;

/**
 * Created by wangshu on 15/11/6.
 */
public class XListViewFooter extends LinearLayout{

    //footer的状态
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;
    public final static int STATE_NOMORE = 3;
    public final static int STATE_NODATA = 4;
    public final static int STATE_FAIL = 5;

    private View mContainer;
    private ProgressBar mProgressBar;
    private TextView mHintView;
    private String text;

    public XListViewFooter(Context context) {
        super(context);
        initWidget(context);
    }

    public XListViewFooter(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initWidget(context);
    }

    public void initWidget(Context context) {

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.activity_footer_view, null, false);
        addView(linearLayout);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mContainer = linearLayout.findViewById(R.id.xlistview_footer_content);
        mProgressBar = (ProgressBar) linearLayout.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) linearLayout.findViewById(R.id.xlistview_footer_hint_textview);
    }

    public void setHintText(String text) {
        this.text = text;
        mHintView.setText(text);
    }

    public void setState(int state) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (state == STATE_READY)
        {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.xlistview_footer_hint_ready);
        } else if (state == STATE_LOADING)
        {
            mHintView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else if (state == STATE_FAIL) {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.xlistview_footer_hint_fail);
        } else
        {
            mHintView.setVisibility(View.VISIBLE);
            if(text != null) {
                mHintView.setText(text);
            } else {
                mHintView.setText(R.string.xlistview_footer_hint_normal);
            }
        }
        // else if (state == STATE_NODATA)
        // {
        // mHintView.setVisibility(View.GONE);
        // }else{
        // mHintView.setVisibility(View.VISIBLE);
        // mHintView.setText(R.string.xlistview_footer_hint_over);
        // }
    }

    public void setBottomMargin(int height) {

        if (height < 0) {
            height = 0;
        }

        LinearLayout.LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.bottomMargin = height;
        mContainer.setLayoutParams(lp);
    }

    public int getBottomMargin() {

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
        return lp.bottomMargin;
    }

    /**
     * normal status
     */
    public void normal() {
        mHintView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * loading status
     */
    public void loading() {
        mHintView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * hide footer when disable pull load more
     */
    public void hide() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
        lp.height = 0;
        mContainer.setLayoutParams(lp);
    }

    /**
     * show footer
     */
    public void show() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mContainer.setLayoutParams(lp);
    }

    public int getVisiableHeight() {

        return mContainer.getHeight();
    }
}

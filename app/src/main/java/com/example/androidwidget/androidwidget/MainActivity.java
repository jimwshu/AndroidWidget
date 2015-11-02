package com.example.androidwidget.androidwidget;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.androidwidget.androidwidget.CustomView.CustomTextView;
import com.example.androidwidget.androidwidget.CustomView.MyScrollView;

public class MainActivity extends ActionBarActivity implements MyScrollView.MyScrollerListener{

    private CustomTextView customTextView;
    private MyScrollView myScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customTextView = (CustomTextView) findViewById(R.id.custom_text_view);
        myScrollView = (MyScrollView) findViewById(R.id.my_scroll_view);
        myScrollView.setMyScrollerListener(this);

    }

    @Override
    public void onScrollerChanged(int l, int t, int oldl, int oldt) {
        Log.e("tag", "mainActivity scrollChanged");
    }

    @Override
    protected void onStop() {
        myScrollView.removeListener();
        super.onStop();
    }
}

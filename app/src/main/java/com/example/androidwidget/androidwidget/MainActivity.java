package com.example.androidwidget.androidwidget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.androidwidget.androidwidget.CustomView.CustomTextView;
import com.example.androidwidget.androidwidget.CustomView.MyScrollView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends ActionBarActivity implements MyScrollView.MyScrollerListener{

    private CustomTextView customTextView;
    private MyScrollView myScrollView;
    private ImageView captureImageview;

    private ImageView startButton;
    private SurfaceViewTemplate pan;

    private String filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customTextView = (CustomTextView) findViewById(R.id.custom_text_view);
        myScrollView = (MyScrollView) findViewById(R.id.my_scroll_view);
        myScrollView.setMyScrollerListener(this);
        customTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUserInfoList(v.getContext());
            }
        });
        captureImageview = (ImageView) findViewById(R.id.capture_imageview);

        filePath = getIntent().getStringExtra("filePath");

        if (!TextUtils.isEmpty(filePath)) {
            //默认横屏，让其旋转90度，拍照的时候让照片旋转了，也要旋转
            //captureImageview.setImageBitmap(BitmapFactory.decodeFile(filePath));

            try {
                FileInputStream fi = new FileInputStream(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(fi);
                Matrix matrix = new Matrix();
                matrix.setRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),matrix, true);
                captureImageview.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        pan = (SurfaceViewTemplate) findViewById(R.id.pan);
        startButton = (ImageView) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pan.isStart()) {
                    pan.panStart(0);
                } else {
                    if ( !pan.isShouleEnd()) {
                        pan.panEnd();
                    }
                }
            }
        });

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

    public void gotoUserInfoList(Context context) {
        Intent intent = new Intent(MainActivity.this, UserInfoListActivity.class);
        startActivity(intent);
    }

    public void gotoCapture(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, CustomCameraActivity.class);
        startActivity(intent);
    }

    public void gotoColor(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, PrimaryColorActivity.class);
        startActivity(intent);
    }

    public void gotoMatrix(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ImageXYChange.class);
        startActivity(intent);
    }



}

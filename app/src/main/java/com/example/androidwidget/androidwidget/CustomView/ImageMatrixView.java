package com.example.androidwidget.androidwidget.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

import com.example.androidwidget.androidwidget.R;

/**
 * Created by wangshu on 15/11/25.
 */
public class ImageMatrixView extends View{
    private Bitmap mBitmpa;

    /**
     * 在Android中，对图片的处理需要使用到Matrix类，Matrix是一个3 x 3的矩阵，他对图片的处理分为四个基本类型：
     1、Translate————平移变换
     2、Scale————缩放变换
     3、Rotate————旋转变换
     4、Skew————错切变换
     * 在Android的API里对于每一种变换都提供了三种操作方式：
     * set（用于设置Matrix中的值）、
     * post（后乘，根据矩阵的原理，相当于左乘）、
     * pre（先乘，相当于矩阵中的右乘）。
     * 默认时，这四种变换都是围绕（0，0）点变换的，当然可以自定义围绕的中心点，通常围绕中心点。
     */
    private Matrix mMatrix;


    public ImageMatrixView(Context context) {
        super(context);
        initView();
    }

    public ImageMatrixView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ImageMatrixView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        mBitmpa = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        setImageMatrix(new Matrix());
    }

    public void setImageMatrix(Matrix matrix) {
        this.mMatrix = matrix;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmpa,0,0,null);
        canvas.drawBitmap(mBitmpa, mMatrix, null);
    }
}

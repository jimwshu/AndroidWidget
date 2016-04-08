package com.example.androidwidget.androidwidget;

/**
 * 1.平移变换 --》 x = x + _x   1 0 _x
 *            y = y + _y      0 1 _y
 *                             0 0 1
 *
 *    2.坐标变换
 *    3.缩放变换  x = kx  k 0 0
 *               y = my  0 m 0
 *                        0 0 1
 *    4.错切变换   1 k 0
 *                m 1 0
 *                0 0 1
 *
 *
 *                A B C    C F 控制平移变换
 *                D E F    B D 控制错切变换
 *                H I G    A E 控制缩放
 */

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;

import com.example.androidwidget.androidwidget.CustomView.ImageMatrixView;

/**
 * 图像坐标变换类
 */
public class ImageXYChange extends Activity{

    private GridLayout mGridLayout;
    private ImageMatrixView mImageMatrixView;
    private int height;
    private int width;

    private  float[] mImageMatrix = new float[9];
    private EditText[] mEts = new EditText[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matrix);
        mImageMatrixView = (ImageMatrixView) findViewById(R.id.view);
        mGridLayout = (GridLayout) findViewById(R.id.gridlayout);

        //直接mGridLayout.getWidth()为0
        mGridLayout.post(new Runnable() {
            @Override
            public void run() {
                width = mGridLayout.getWidth()/3;
                height = mGridLayout.getHeight()/3;
                addEditText();
                initImageMatrix();
            }
        });
    }

    private void addEditText() {
        for (int i = 0; i< 9; i++) {
            EditText editText = new EditText(this);
            editText.setGravity(Gravity.CENTER);
            mEts[i] = editText;
            mGridLayout.addView(editText, width, height);
        }

    }

    private void initImageMatrix() {
        for (int i = 0; i < 9; i++) {
            if (i % 4 == 0) {
                mEts[i].setText(1 + "");
            } else {
                mEts[i].setText(0 + "");
            }
        }
    }

    private void getImageMatrix() {
        for ( int i = 0; i < 9; i++) {
            EditText et = mEts[i];
            mImageMatrix[i] = Float.parseFloat(et.getText().toString());
        }
    }

    public void change(View view) {
        getImageMatrix();

        Matrix matrix = new Matrix();
        matrix.setValues(mImageMatrix);
        /**
         *     平移    matrix.setTranslate(150,150);
         *             matrix.postScale(2,2); --> post表示先平移在double
         *
         */
        mImageMatrixView.setImageMatrix(matrix);
        mImageMatrixView.postInvalidate();

    }

    public void reset(View view) {
        initImageMatrix();
        getImageMatrix();
        Matrix matrix = new Matrix();
        matrix.setValues(mImageMatrix);
        mImageMatrixView.setImageMatrix(matrix);
        mImageMatrixView.postInvalidate();
    }

}

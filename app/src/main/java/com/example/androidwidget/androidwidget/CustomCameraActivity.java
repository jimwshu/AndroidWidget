package com.example.androidwidget.androidwidget;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.androidwidget.androidwidget.Helpers.StreamHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 1.自定义相机
 * 2.自定义SurfaceView
 * 3.相机和SurfaceView的关联
 * 4.调整相机的显示效果
 * 5.调整相机的预览效果
 *
 * 拍照界面的自定义，比如水印，点击旋转等功能。
 */
public class CustomCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private Camera camera;
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            try {

                String path = StreamHelper.getSDPath();

                Log.e("TAG", path + File.separator + "temp.png");

                File temFile = new File(path + File.separator + "temp.png");


                FileOutputStream fo = new FileOutputStream(temFile);
                fo.write(data);
                fo.close();

                Intent intent = new Intent();
                intent.setClass(CustomCameraActivity.this, MainActivity.class);
                intent.putExtra("filePath", temFile.getAbsolutePath());
                startActivity(intent);
                finish();
            } catch (FileNotFoundException e) {
                Log.e("TAG", "FileNotFoundException");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);
        mPreview = (SurfaceView) findViewById(R.id.capture_surfaceview);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(this);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.autoFocus(null);
            }
        });
    }


    public void capture(View view) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPreviewSize(800, 400);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    camera.takePicture(null, null , pictureCallback);
                }
            }
        });

    }


    /**
     * 获取相机对象
     * @return
     */
    private Camera getCamera() {
        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 预览相机内容 --> camera和SurfaceHolder 进行关联，并让camera开始预览工作
     */
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            //默认横屏，让其旋转90度
            camera.setDisplayOrientation(90);
            camera.startPreview();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机资源
     */
    private void releaseCamere() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null) {
            camera = getCamera();
            if (mHolder != null) {
                setStartPreview(camera, mHolder);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamere();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(camera , mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.stopPreview();
        setStartPreview(camera, mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamere();
    }
}
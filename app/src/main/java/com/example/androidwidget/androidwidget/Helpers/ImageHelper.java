package com.example.androidwidget.androidwidget.Helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by wangshu on 15/11/24.
 */
public class ImageHelper {

    /**
     * ColorMatrix是一个5x4阶的矩阵 在下面表示为A,第一行表示R红色分量,第二行表示G绿色分量,第三行表示B蓝色分量,第四行表示透明度:
     * @param bitmap
     * @param hue
     * @param saturation
     * @param lum
     * @return
     */

    public static Bitmap handleImageEffect(Bitmap bitmap, float hue, float saturation, float lum) {
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        ColorMatrix colorMatrix = new ColorMatrix( );
        colorMatrix.setRotate(0, hue);
        colorMatrix.setRotate(1, hue);
        colorMatrix.setRotate(2, hue);

        ColorMatrix saturationColorMatrix = new ColorMatrix( );
        saturationColorMatrix.setSaturation(saturation);

        ColorMatrix lumColorMatrix = new ColorMatrix( );
        lumColorMatrix.setScale(lum,lum,lum, 1);

        ColorMatrix imageColorMatrix = new ColorMatrix( );
        imageColorMatrix.postConcat(colorMatrix);
        imageColorMatrix.postConcat(saturationColorMatrix);
        imageColorMatrix.postConcat(lumColorMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(imageColorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return  bmp;
    }

    public static Bitmap handleImageEffect1(Bitmap bitmap, float hue, float saturation, float lum) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int color;
        int r, g, b, a;
        //所有像素点的数组
        int [] old = new int [width * height];
        int [] newOld = new int[width * height];
        //获得bitmap的所有像素点，并存到数组中
        bitmap.getPixels(old,0, width, 0,0, 0, height);

        for(int i = 0; i < width * height; i++) {
            color = old[i];
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            //经过变换之后，调用下面方法，就将bitmap进行了变换，可以达到各种各样的效果
            newOld[i] = Color.argb(r,g,b,a);
        }
        bitmap.setPixels(newOld,0,width,0,0,width,height);
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        ColorMatrix colorMatrix = new ColorMatrix( );
        colorMatrix.setRotate(0, hue);
        colorMatrix.setRotate(1, hue);
        colorMatrix.setRotate(2, hue);

        ColorMatrix saturationColorMatrix = new ColorMatrix( );
        saturationColorMatrix.setSaturation(saturation);

        ColorMatrix lumColorMatrix = new ColorMatrix( );
        lumColorMatrix.setScale(lum,lum,lum, 1);

        ColorMatrix imageColorMatrix = new ColorMatrix( );
        imageColorMatrix.postConcat(colorMatrix);
        imageColorMatrix.postConcat(saturationColorMatrix);
        imageColorMatrix.postConcat(lumColorMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(imageColorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return  bmp;
    }


}

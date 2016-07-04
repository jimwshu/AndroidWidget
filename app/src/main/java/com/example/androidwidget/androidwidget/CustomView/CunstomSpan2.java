package com.example.androidwidget.androidwidget.CustomView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.widget.TextView;

/**
 * Created by wangshu on 16/6/12.
 *
 * 用StaticLayout实现自定义的Span，但是这里的行间距只能是默认值，如果在TextView或者EditText使用android:lineSpacingExtra="5dp"，就会对不齐
 *
 * 网上推荐使用RectF
 *  @Override
 *  public  void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint)
 *   {
 *   RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
 *   paint.setColor(Color.BLUE);
 *   canvas.drawRoundRect(rect, 100f, 30f, paint);
 *   paint.setColor(Color.WHITE);
    canvas.drawText(text, start, end, x, y, paint);
}
 *
 */
public class CunstomSpan2 extends ReplacementSpan{

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return  Math.round(measureText(paint, text, start, end));
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
        paint.setColor(Color.BLUE);
        canvas.drawRoundRect(rect, 100f, 30f, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText(text, start, end, x, y, paint);
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return  Math.round(paint.measureText(text, start, end));
    }

}

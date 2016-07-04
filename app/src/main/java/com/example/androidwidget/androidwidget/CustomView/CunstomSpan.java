package com.example.androidwidget.androidwidget.CustomView;

import android.graphics.Canvas;
import android.graphics.Paint;
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
public class CunstomSpan extends ReplacementSpan{
    private StaticLayout layout;

    public CunstomSpan(String text, TextView textView) {
        layout = new StaticLayout(text, textView.getPaint(), 1024 * 1024, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
    }

    public CunstomSpan(String text, TextView textView, int color) {
        TextPaint textPaint = new TextPaint();
        textPaint.set(textView.getPaint());
        textPaint.setColor(color);
        layout = new StaticLayout(text, textPaint, 1024 * 1024, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
    }


    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (fm != null) {
            fm.ascent = -layout.getLineTop(layout.getLineCount() - 1) + layout.getLineTop(layout.getLineCount());
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }
        return Math.round(layout.getLineRight(layout.getLineCount() - 1));
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        canvas.save();

        int transY = bottom - layout.getHeight();

        canvas.translate(x, transY);
        layout.draw(canvas);
        canvas.restore();
    }

}

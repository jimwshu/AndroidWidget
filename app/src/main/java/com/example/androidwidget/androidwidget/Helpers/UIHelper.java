package com.example.androidwidget.androidwidget.Helpers;

import android.content.Context;

/**
 * Created by wangshu on 15/11/7.
 */
public class UIHelper {

    public static int dip2px(Context context, float dip) {

        return (int) (getDensity(context) * dip + 0.5f);
    }

    public static double getDensity(Context context) {
       return  context.getResources().getDisplayMetrics().density;
    }

    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

}

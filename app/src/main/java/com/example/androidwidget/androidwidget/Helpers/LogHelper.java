package com.example.androidwidget.androidwidget.Helpers;

import android.util.Log;
import android.util.Log;

/**
 * Created by power on 14-2-12.
 */
public class LogHelper {

    public static String getClassName (int i) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[i];
        return ste.getClassName();
    }

    public static void d(String msg,int offset){
        if(!DebugHelper.DEBUG){
            return;
        }
        String name = getClassName(4+offset);
        Log.d(name, msg);
    }

    public static void d(String msg){
        d(msg,1);
    }

    public static void w(String msg){
        if(!DebugHelper.DEBUG){
            return;
        }
        String name = getClassName(4);
        Log.w(name, msg);
    }

    public static void e(String msg){
        if(!DebugHelper.DEBUG){
            return;
        }
        String name = getClassName(4);
        Log.e(name, msg);
    }
}


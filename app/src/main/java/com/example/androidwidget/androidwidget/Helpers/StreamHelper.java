package com.example.androidwidget.androidwidget.Helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by wangshu on 15/11/8.
 */
public class StreamHelper {

    public static String inputSteam2String(InputStream is) throws Exception{



        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer();
        String line = null;
        while( (line = br.readLine()) != null) {
            sb.append(line);
        }
        return line;
    }

    public static String toString(InputStream is) throws Exception {
        StringBuffer sb = new StringBuffer();
        byte [] b = new byte[2048];

        for(int n; (n=is.read(b)) != -1;) {
            sb.append(new String(b,0,n));
        }
        return sb.toString();
    }

    public static InputStream toInputStream(String str) throws Exception {
        InputStream   in_nocode   =   new ByteArrayInputStream(str.getBytes("UTF-8"));
        return in_nocode;
    }

}

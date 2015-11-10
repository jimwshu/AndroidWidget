package com.example.androidwidget.androidwidget.Helpers;

import android.util.Log;

import com.example.androidwidget.androidwidget.MyException.AppException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangshu on 15/11/8.
 */
public class HttpClientHelper {

    private static HttpClientHelper httpClient = null;

    public static HttpClientHelper getInstance() {
        if (httpClient == null) {
            httpClient = new HttpClientHelper();
        }
        return httpClient;
    }

    public String get(String url) throws AppException{
        URL getUrl = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        String result = null;
        try {
            getUrl = new URL(url);
            conn = (HttpURLConnection) getUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept_Language", "zh-CN");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Connection", "Keep-Alive");

            int responseCode = conn.getResponseCode();
            Log.e("resultcode", responseCode + "");
            if (responseCode != 200) {
                //todo 抛异常
                throw new AppException("数据请求失败", responseCode);
            } else {
                is = conn.getInputStream();
                //获取is的可读字节数，也就是总大小
                result = StreamHelper.toString(is);

/*
     这个available发生了什么？todo
int available = is.available();

                if (available > 0) {
                    result = StreamHelper.toString(is);
                }*/
                is.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                conn.disconnect();
            }
            throw new AppException("数据解析失败", 404);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        Log.e("reslut", result);
        return result;
    }


    public String post(String url, HashMap<String, Object> params) throws AppException{
        URL getUrl = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        String result = null;
        OutputStream os = null;
        StringBuffer sb = new StringBuffer();
        try {
            getUrl = new URL(url);
            conn = (HttpURLConnection) getUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept_Language", "zh-CN");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setDoInput(true);
            // Post请求必须设置允许输出
            conn.setDoOutput(true);
            // Post请求不能使用缓存
            conn.setUseCaches(false);


            // 隐含调用connect方法? TODO RIGHT?
            if (params.size() != 0) {
                //解析params
                String param = parsePostParams1(params);
                Log.e("params", param);
                //请求形式是 key:value这种形式，而springmvc 数据绑定的是key=value这种形式
                os = conn.getOutputStream();
                os.write(param.getBytes());
                os.flush();
                os.close();
            } else {

                //do nothing?

            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                //todo 抛异常
                throw new AppException("数据请求失败", responseCode);
            } else {
                is = conn.getInputStream();
                //获取is的可读字节数，也就是总大小
                int available = is.available();

                if (available > 0) {
                    result = StreamHelper.toString(is);
                }
                is.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                conn.disconnect();
            }
            throw new AppException("数据解析失败", 999);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    /**
     *
     * @param params
     * @return
     * @throws JSONException
     * 返回的字符串是json格式的，类似于{"test":"test"}
     * 在解析的时候要注意
     */
    public String parsePostParams(HashMap<String,Object> params) throws JSONException{

        JSONObject object = new JSONObject();
        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            object.put((String)entry.getKey(), entry.getValue());
        }

        return object.toString();
    }

    /**
     *
     * @param params
     * @return
     * @throws JSONException
     * 返回的字符串是=格式的类似于 test=test
     *springmvc可以直接解析
     */
    public String parsePostParams1(HashMap<String,Object> params) throws JSONException{

        StringBuffer sb = new StringBuffer();
        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            sb.append("&" + entry.getKey() + "=" + entry.getValue());
        }

        return sb.substring(1).toString();
    }
}

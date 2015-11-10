package com.example.androidwidget.androidwidget.Helpers;

import android.util.Pair;

import android.os.AsyncTask;
import android.util.Pair;
import org.json.JSONObject;

import java.util.Map;

abstract public class HttpAsyncTask extends AsyncTask<String,Void,Pair<Integer,String>> {

    protected JSONObject _response =null;

    public JSONObject getResponse(){
        return _response;
    }

    public void run(){
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static Pair<Integer,String> getLocalError(){
//        return new Pair<Integer, String>(HttpClient.RESP_CODE_LOCAL_ERROR,HttpClient.getLocalErrorStr());

        return new Pair<Integer,String>(333,"i am");
    }

    public HttpAsyncTask(){
    }

    private String _url=null;

    public HttpAsyncTask(String url){
        this._url=url;
    }

    private Map<String,Object> _params=null;

    public HttpAsyncTask(String url,Map<String,Object> params){
        this._url=url;
        this._params=params;
    }

    //自行实现
    public Map<String,Object> getPostParams(){
        return _params;
    }

    //自行实现
    public String getURL(){
        return _url;
    }

   /* @Override
    protected Pair<Integer, String> doInBackground(String... params) {
        try {
            Map<String,Object> reqParams=getPostParams();
            String resp = HttpClient.getIntentce().post(getURL(), reqParams);
            _response = new JSONObject(resp);
            int errorCode = _response.getInt("err");
            return new Pair<Integer,String>(errorCode, _response.optString("err_msg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<Integer,String>(HttpClient.RESP_CODE_LOCAL_ERROR,HttpClient.getLocalErrorStr());
    }*/
}

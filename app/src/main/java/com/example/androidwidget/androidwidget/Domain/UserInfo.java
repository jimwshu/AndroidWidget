package com.example.androidwidget.androidwidget.Domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wangshu on 15/11/8.
 */
public class UserInfo {

    private int id;
    private String userName;
    private String psw;
    private String url;
    private int age;
    private int sex;

    public UserInfo() {

    }

    public UserInfo(int id, String userName, String psw, String url) {
        this.id = id;
        this.userName = userName;
        this.psw = psw;
        this.url = url;
    }

    public UserInfo(int id, String userName, String psw, String url, int age, int sex) {
        this.id = id;
        this.userName = userName;
        this.psw = psw;
        this.url = url;
        this.age = age;
        this.sex = sex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void parseJson(JSONObject jsonObject) throws JSONException{

        if (jsonObject.has("id")) {
            id = jsonObject.getInt("id");
        }

        if (jsonObject.has("name")) {
            userName = jsonObject.getString("name");
        }

        if (jsonObject.has("age")) {
            age = jsonObject.getInt("age");
        }

        if (jsonObject.has("sex")) {
            sex = jsonObject.getInt("sex");
        }

        if (jsonObject.has("psw")) {
            psw = jsonObject.getString("psw");
        }

        if (jsonObject.has("url")) {
            url = jsonObject.getString("url");
        }



    }
}

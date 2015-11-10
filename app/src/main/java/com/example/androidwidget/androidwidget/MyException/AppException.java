package com.example.androidwidget.androidwidget.MyException;

/**
 * Created by wangshu on 15/11/9.
 */
public class AppException extends Exception{

    private int statusCode = -1;

    public AppException(String msg) {
        super(msg);
    }

    public AppException(Exception cause) {
        super(cause);
    }

    public AppException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;

    }

    public AppException(String msg, Exception cause) {
        super(msg, cause);
    }

    public AppException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;

    }

    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String toString() {
        return super.toString()+" statucCode:"+statusCode;
    }
}

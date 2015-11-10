package com.example.androidwidget.androidwidget.CustomView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshu on 15/11/5.
 * 弱引用
 */
public class OnListener {

    private static List<WeakReference<MyLocationListener>> listeners;
    User firstUser;
    User secondUser;

    private static WeakReference<OnListener> instance;


    public void registerListener(MyLocationListener locationListener) {

        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        listeners.add(new WeakReference<MyLocationListener>(locationListener));

        if (instance != null) {
            OnListener onListener = instance.get();
            if (onListener != null) {
                if (locationListener != null) {
                    locationListener.locationSucc(onListener);
                }
                return;
            }
        }

        OnListener onListener = new OnListener();
        onListener.firstUser = new User("zhangsan", "man");
        onListener.secondUser = new User("lisi", "woman");

        instance = new WeakReference<OnListener>(onListener);
        if (locationListener != null) {
            locationListener.locationSucc(onListener);
        }
    }


    public void onRefresh() {
        instance = null;
        OnListener onListener = new OnListener();
        onListener.firstUser = new User("wangwu", "man");
        onListener.secondUser = new User("tanliu", "woman");
        instance = new WeakReference<OnListener>(onListener);
        notifyChange();
    }


    public void notifyChange() {
            if (listeners!=null){
                for (int i = 0; i < listeners.size(); i++) {
                    WeakReference<MyLocationListener> lRef = listeners.get(i);
                    if (lRef==null || lRef.get()==null){
                        listeners.remove(i);
                        i--;
                    } else{
                        lRef.get().locationSucc(instance.get());
                    }
                }
            }
    }

    public static void unregister(MyLocationListener listener){
        if (listeners != null){
            for (int i = 0; i < listeners.size(); i++) {
                WeakReference<MyLocationListener> lRef = listeners.get(i);
                if (lRef==null || lRef.get()==null || lRef.get()==listener){
                    listeners.remove(i);
                    i--;
                }
            }
            if (listeners.size()==0){
                listeners = null;
            }
        }
        if (instance != null) {
            instance = null;
        }
    }




    public interface MyLocationListener {
        public void locationSucc(OnListener onListener);
    }

    public class User {
        String name;
        String sex;

        public User(String name, String sex) {
            this.name = name;
            this.sex = sex;
        }
    }
}

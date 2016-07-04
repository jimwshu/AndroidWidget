package com.example.androidwidget.androidwidget;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidwidget.androidwidget.CustomView.AutoPagerView;
import com.example.androidwidget.androidwidget.CustomView.XListView;
import com.example.androidwidget.androidwidget.Domain.UserInfo;
import com.example.androidwidget.androidwidget.Helpers.HttpAsyncTask;
import com.example.androidwidget.androidwidget.Helpers.HttpClientHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wangshu on 15/11/8.
 */
public class RefreshViewPagerActivity extends Activity {
    String TAG = "RefreshViewPagerActivity";

    private AutoPagerView autoPagerView;
    private int page = 50000;
    private ArrayList<Object> datas;
    RefreshViewPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_viewpager);
        datas = new ArrayList<>();
        for (int i = 0; i < 100; i ++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setAge(10);
            userInfo.setUserName("www");
            userInfo.setId(i);
            userInfo.setPsw("dddddd");
            userInfo.setSex(1);
            userInfo.setUrl("www.baidu.com");
            datas.add(userInfo);
        }

        autoPagerView = (AutoPagerView) findViewById(R.id.view_pager);

        autoPagerView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e(TAG, " 11");
            }

            @Override
            public void onPageSelected(int position) {
                UserInfo user = (UserInfo) datas.get(position);
                Log.e(TAG, " 22");

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e(TAG, " 33");

                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    Log.e(TAG, " 77");

                   /* int pos = autoPagerView.getCurrentItem();
                    if (pos < 100) {
                        autoPagerView.setCurrentItem(pos+1, false);
                    }*/
                }
            }
        });
        adapter = new RefreshViewPagerAdapter();
        autoPagerView.setAdapter(adapter);
        autoPagerView.setCurrentItem(0);
        adapter.notifyDataSetChanged();


    }


    class RefreshViewPagerAdapter extends PagerAdapter {

        private ArrayList<ViewHolder> cache = new ArrayList<ViewHolder>();

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ViewHolder) object).view;
        }

        public RefreshViewPagerAdapter() {
            super();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final ViewHolder holder;
            if (cache.size() == 0) {
                holder = new ViewHolder();
                holder.view = LayoutInflater.from(container.getContext()).inflate(R.layout.user_info_item, container, false);
                holder.userLin = (LinearLayout) holder.view.findViewById(R.id.user_lin);
                holder.userName = (TextView) holder.view.findViewById(R.id.user_name);
                holder.userPsw = (TextView) holder.view.findViewById(R.id.user_psw);
                holder.userImg = (ImageView) holder.view.findViewById(R.id.user_img);
            } else {
                holder = cache.remove(cache.size() - 1);
            }

            UserInfo userInfo = (UserInfo) datas.get(position);
            Log.e(TAG, "userinfo;" + userInfo.getId());
            holder.userImg.setImageResource(R.mipmap.ic_launcher);
            holder.userName.setText(userInfo.getUserName());
            holder.userPsw.setText(userInfo.getId() + " " + userInfo.getAge()  + "" +userInfo.getSex());
            holder.pos = position;
            container.addView(holder.view);
            return holder;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewHolder holder = (ViewHolder) object;
            container.removeView(holder.view);
            cache.add(holder);
        }

        @Override
        public int getCount() {
            return datas.size();
        }
    }


    public class ViewHolder{
        int pos;
        View view;
        LinearLayout userLin;
        TextView userName;
        TextView userPsw;
        ImageView userImg;

    }

}

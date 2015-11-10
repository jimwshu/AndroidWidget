package com.example.androidwidget.androidwidget;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
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

import com.example.androidwidget.androidwidget.CustomView.XListView;
import com.example.androidwidget.androidwidget.Domain.UserInfo;
import com.example.androidwidget.androidwidget.Helpers.HttpAsyncTask;
import com.example.androidwidget.androidwidget.Helpers.HttpClientHelper;
import com.example.androidwidget.androidwidget.Helpers.LogHelper;
import com.example.androidwidget.androidwidget.Helpers.StreamHelper;
import com.example.androidwidget.androidwidget.MyException.AppException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangshu on 15/11/8.
 */
public class UserInfoListActivity extends Activity implements XListView.IXListViewListener{

    private XListView listView;
    private ArrayList<Object> datas;
    private UserInfoAdapter adapter;

    private static final int PAGE = 1;
    private int nextPage = PAGE;
    private int prePage = PAGE;
    private int currentPage = PAGE;

    private boolean hasMore = true;
    public static String user_url = "http://192.168.1.105:8080/iszt-demo-mybatis/app/user/listPageUser?page=%1$d&rows=20";
    public static String user_url_2 = "http://172.16.0.94:8080/iszt-demo-mybatis/app/user/listPageUser?page=%1$d&rows=20";
    //public static String user_url_2 = "http://172.16.0.94:8080/iszt-demo-mybatis/app/registration/postParams";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.day);
        setContentView(R.layout.activity_userinfo_list);
        initWidget();
        initListener();
        loadData();
        super.onCreate(savedInstanceState);
    }

    public void initWidget() {
        listView = (XListView) findViewById(R.id.listview);
    }

    public void initListener() {
        datas = new ArrayList<>();
        adapter = new UserInfoAdapter(UserInfoListActivity.this);
        listView.setPullLoadEnable(false);
        listView.setXListViewListener(this);

        listView.setAdapter(adapter);
    }

    public void loadData() {
        if (!hasMore) {
            return;
        }

        HttpAsyncTask task = new HttpAsyncTask() {
            JSONObject jo = null;
            @Override
            protected Pair<Integer, String> doInBackground(String[] params) {

                try {
                    /*HashMap<String,Object> postParams = new HashMap<>();
                    postParams.put("test", "testcc");*/
                    String result = HttpClientHelper.getInstance().get(String.format(user_url,currentPage));

                    if (TextUtils.isEmpty(result)) {
                        return new Pair<>(100,"返回了空的东西 why？");
                    } else {
                        jo = new JSONObject(result);
                        return new Pair<>(jo.getInt("err"),result);
                    }

                }  catch (Exception e) {
                    e.printStackTrace();
                    return new Pair<>(999,"网络连接失败，请重试");
                }
            }

            @Override
            protected void onPostExecute(Pair<Integer, String> result) {




                if (result.first != 0) {
                    if( currentPage == PAGE ){
                        listView.stopRefresh();
                    } else {
                        listView.setPullLoadEnable(false);
                    }
                    Toast.makeText(getApplicationContext(),result.first + result.second, Toast.LENGTH_LONG).show();
                } else {
                    try {

                        if (currentPage == PAGE) {
                            datas.clear();
                            listView.stopRefresh();
                        } else {
                            listView.stopLoadMore(true);
                        }

                        JSONObject jos = new JSONObject(result.second);
                        JSONArray jsonArray = jos.getJSONArray("rows");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            UserInfo userInfo = new UserInfo();
                            userInfo.parseJson(jsonArray.getJSONObject(i));
                            datas.add(userInfo);
                        }
                        adapter.notifyDataSetChanged();

                        hasMore = jos.getBoolean("hasMore");
                        if (hasMore) {
                            listView.setPullLoadEnable(true);
                        } else {
                            listView.setPullLoadEnable(false);
                        }
                        currentPage++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        //保证线程是按照顺序一个一个执行的
        //task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }


    public class UserInfoAdapter extends BaseAdapter {

        private Context context;

        public UserInfoAdapter(Context context) {
            this.context = context;
        }


        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserInfoHolderView holderView = null;
            if (null == convertView || !(convertView.getTag() instanceof UserInfoHolderView)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.user_info_item,null, false);
                holderView = new UserInfoHolderView(convertView);
                convertView.setTag(holderView);
            } else {
                holderView = (UserInfoHolderView) convertView.getTag();
            }
            UserInfo userInfo = (UserInfo) datas.get(position);
            holderView.userImg.setImageResource(R.mipmap.ic_launcher);
            holderView.userName.setText(userInfo.getUserName());
            holderView.userPsw.setText(userInfo.getId() + " " + userInfo.getAge()  + "" +userInfo.getSex());

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    public class UserInfoHolderView{
        LinearLayout userLin;
        TextView userName;
        TextView userPsw;
        ImageView userImg;

        public UserInfoHolderView (View view) {
            userLin = (LinearLayout) view.findViewById(R.id.user_lin);
            userName = (TextView) view.findViewById(R.id.user_name);
            userPsw = (TextView) view.findViewById(R.id.user_psw);
            userImg = (ImageView) view.findViewById(R.id.user_img);
        }


    }

    @Override
    public void onLoadMore() {
        loadData();
    }

    @Override
    public void onRefresh() {
        currentPage = PAGE;
        loadData();
    }

    @Override
    public void onInitHistoryData() {
        //do nothing
    }
}

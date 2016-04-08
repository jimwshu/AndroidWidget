package com.example.androidwidget.androidwidget;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.androidwidget.androidwidget.CustomView.CheckedImageView;
import com.example.androidwidget.androidwidget.Domain.ImageFolderInfo;
import com.example.androidwidget.androidwidget.Domain.ImageInfo;
import com.example.androidwidget.androidwidget.Helpers.ImagePickerManager;

import java.util.ArrayList;
import java.util.List;

public class ImagePickerActivity extends ActionBarActivity {


   /* private GridView mGridView;
    private ImageGridViewAdapter adapter;

    private List<ImageInfo> imageInfoList;
    private ImagePickerManager imagePickerManager;

    private List<ImageFolderInfo> folders;

    private List<Object> imageInfos;

    private int imageWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        mGridView = (GridView) findViewById(R.id.image_gridview);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = (int) ((metrics.widthPixels - 5 * 4 * metrics.density) / 4);

    }


    @Override
    protected void onResume() {
        super.onResume();
        initFolders();
    }

    private void initFolders(){
        imagePickerManager.reset();
        imagePickerManager.init(this, new ImagePickerManager.OnInitCompletedListener() {
            @Override
            public void onCompleted() {
                folders = imagePickerManager.getImageFolders();
                selectFolder(0);
            }
        });
    }

    private void selectFolder(int pos){
        ImageFolderInfo folder = folders.get(pos);
        List<ImageInfo> infos = folder.list();
        imageInfos = new ArrayList<Object>();
        imageInfos.addAll(infos);
        adapter = new ImageGridViewAdapter(ImagePickerActivity.this);
        mGridView.setAdapter(adapter);
    }

    class ImageGridViewAdapter extends BaseAdapter {
        public Context mContext;

        public ImageGridViewAdapter() {
            mContext = getApplicationContext();
        }
        public ImageGridViewAdapter (Context context){
            mContext = context;
        }


        @Override
        public int getCount() {
            return imageInfoList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Holder holder;

            if (convertView == null) {
                holder = new Holder();
                convertView = getLayoutInflater().inflate(R.layout.item_image_picker,null);
                holder.image = (CheckedImageView) convertView.findViewById(R.id.image_folder);
                convertView.setTag(holder);
                holder.image.setOnCheckedChangeListener(new CheckedImageView.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChange(CheckedImageView view, boolean checked, boolean isFromTouch) {
                        if (isFromTouch) {
                            ImageGridViewAdapter.Holder holder = (ImageGridViewAdapter.Holder) view.getTag();
                            final ImageInfo info = (ImageInfo) adapter.getItem(holder.pos);

                        }
                    }
                });
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.pos = position;
            final ImageInfo info = (ImageInfo) getItem(position);

            if (holder.info != info) {
                if(holder.info!=null) {
                    mImageLoader.cancelDisplayTask(holder.image);
                }
                mImageLoader.displayImage("file://" + info.path, new ImageViewAware(holder.image){ // ugly
                    @Override
                    public int getWidth() {
                        return imageWidth;
                    }

                    @Override
                    public int getHeight() {
                        return imageWidth;
                    }
                }, mImageOptions);
                holder.info = info;
            }

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        class Holder {
            int pos;
            ImageInfo info;
            CheckedImageView image;
        }
    }*/


}

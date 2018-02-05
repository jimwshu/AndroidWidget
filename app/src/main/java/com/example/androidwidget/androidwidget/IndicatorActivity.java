package com.example.androidwidget.androidwidget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangshu on 16/7/14.
 *
 * 1. ViewPager的使用
 * 2. FragmentPagerAdapter
 * 3. FragmentActivity
 *
 */
public class IndicatorActivity extends FragmentActivity {


    private ViewPagerIndicator mViewPagerIndicator;
    private ViewPager mViewPager;

    private List<String> mTitles = Arrays.asList(new String [] {"短信1", "收藏2", "推荐3","短信4", "收藏5", "推荐6","短信7", "收藏8", "推荐9"});

    private FragmentPagerAdapter mPagerAdapter;
    private List<IndecatorFragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_pager_indicator_2);
        initView();
        initDate();
        mViewPagerIndicator.setVisibleCount(5);
        mViewPagerIndicator.setTitles(mTitles);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPagerIndicator.setViewPager(mViewPager, 0);
        mViewPagerIndicator.setPageChangeListener(new ViewPagerIndicator.PageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initDate() {
        for (String title : mTitles) {
            IndecatorFragment fragment = IndecatorFragment.newInstance(title);
            mFragments.add(fragment);
        }

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles.get(position);
            }
        };
    }

    private void initView() {
        mViewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.view_pager_indicator);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

}

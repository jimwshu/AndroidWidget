package com.example.androidwidget.androidwidget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by wangshu on 16/7/14.
 */
public class IndecatorFragment extends Fragment {

    public final static String FRAGMENT_TITLE = "title";

    private String mTitle;

    public static IndecatorFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_TITLE, title);
        IndecatorFragment fragment = new IndecatorFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(FRAGMENT_TITLE);
        }

        TextView textView = new TextView(getActivity());
        textView.setText(mTitle);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }
}

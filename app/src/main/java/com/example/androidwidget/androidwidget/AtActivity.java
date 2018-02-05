package com.example.androidwidget.androidwidget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import com.example.androidwidget.androidwidget.CustomView.OnListener;

/**
 * Created by wangshu on 16/6/12.
 * AppCompatActivity是5.0为了替换actionbaractivity而来的。
 * actionbar 也北划上了横线，取而代之的是toolbar
 *  see 印象笔记
 *
 * 输入我：由空变为我
 before:   String:      start:0      count:0      after:1
 on:          我      start:0      before:0      count:1
 after:      String:我

 输入是：由我变为我是
 String:我      start:1      count:0      after:1
 String:我是      start:1      before:0      count:1
 String:我是

 规律：  增加字符串   —》 before：after > 0,由start到start + after就是即将要增加的内容，无法获取增加的内容
 on： count > 0, 由start到start + count就是增加的内容
 输入框的原内容字符串s，从索引位置start开始，有count个字符即将被替换，替换这count个字符的新的字符个数为after

 删除是：由我是变为我
 String:我是      start:1      count:1      after:0
 String:我      start:1      before:1      count:0
 String:我

 删除字符串   —》  before: count > 0, 由start到start + count就是删除内容
 on：before > 0, 无法获取删除内容
 在变化时的新的字符串s里，从索引位置start开始，有count个字符，是替换了原来的before个字符的

 增加事实是： 由我变成了我事实是
 String:我      start:1      count:0      after:3
 String:我事实是      start:1      before:0      count:3
 String:我事实是

 替换我：有我事实是变成了他事实是
 String:我事实是      start:0      count:1      after:1
 String:他事实是      start:0      before:1      count:1
 String:他事实是

 替换字符串  —》   before： count > 0, after > 0,  由start到start+after就是即将要增加的内容（无法获取）， start到start+count就是将要被替换的内容（可以获取）
 on：  before  > 0, count > 0，由start到start+count就是增加的内容（可以获取），start到start+before就是以前被替换的内容（无法获取）

 总结：在before中获取将要被删除的内容，早on中获取将要被增加的内容。
 *
 */
public class AtActivity extends AppCompatActivity {

    private static final String TAG = "AtActivity";

    EditText mEditText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_at_info);
        setContentView(R.layout.activity_at_info);
       /* mEditText = (EditText) findViewById(R.id.content);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 第一个是修改之前的string，start修改开始的位置，count修改的内容数，改变之后的内容数
                Log.e(TAG, "String:" + s.toString() + "      start:"  + start + "      count:"  + count + "      after:"  + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "String:" + s.toString() + "      start:"  + start + "      before:"  + before + "      count:"  + count);

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "String:" + s.toString());
            }
        });*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);

        Log.e("tag:", "activity ontouch " + b);

        return b;
    }
}

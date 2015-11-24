package com.example.androidwidget.androidwidget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.androidwidget.androidwidget.Helpers.ImageHelper;

/**初始矩阵
 * 1 0 0 0 0     R    第一行与R有关，最后一个0是偏移量 其余类似
 * 0 1 0 0 0 --> G
 * 0 0 1 0 0     B
 * 0 0 0 1 0     A    A代表透明度。
 *               1    R G B的值一样，进行组合时，会产生白色， 所以亮度和R G B的值有关。
 *
 *底片效果： B.R = 255 -B.R B.G= 255 - B.G B.B = 255- B.B
 *怀旧效果： B.R = 0.393*B.R + 0.769*B.G + 0.189*B.B
 *          B.G = 0.349*B.R + 0.686*B.G + 0.168*B.B
 *          B.B = 0.272*B.R + 0.534*B.G + 0.131*B.B
 *
 *
 */
public class PrimaryColorActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private SeekBar mSeekbarhue, mSeekbarSaturation, mSeekbarLum;
    private float mHue, mSaturation, mLum;
    private ImageView mImageView;
    private Bitmap mBitmap;

    public static int MAX_VALUE = 255;
    public static int MID_VALUE = 177;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_color);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.found_ic_cross_night);
        mImageView = (ImageView) findViewById(R.id.iamgeview);
        mSeekbarhue = (SeekBar) findViewById(R.id.hueSeekbar);
        mSeekbarSaturation = (SeekBar) findViewById(R.id.saturationSeekbar);
        mSeekbarLum = (SeekBar) findViewById(R.id.lumSeekbar);


        mSeekbarhue.setOnSeekBarChangeListener(this);
        mSeekbarhue.setMax(MAX_VALUE);
        mSeekbarhue.setProgress(MID_VALUE);

        mSeekbarSaturation.setOnSeekBarChangeListener(this);
        mSeekbarSaturation.setMax(MAX_VALUE);
        mSeekbarSaturation.setProgress(MID_VALUE);

        mSeekbarLum.setOnSeekBarChangeListener(this);
        mSeekbarLum.setMax(MAX_VALUE);
        mSeekbarLum.setProgress(MID_VALUE);
        mImageView.setImageResource(R.mipmap.found_ic_cross_night);


    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.hueSeekbar:
                //todo why? 经验值
                mHue = (progress - MID_VALUE) * 1.0F / MID_VALUE;
                break;
            case R.id.saturationSeekbar:
                mSaturation = (progress ) * 1.0F / MID_VALUE;
                break;
            case R.id.lumSeekbar:
                mLum = (progress) * 1.0F / MID_VALUE;
                break;
        }

        if (mBitmap != null) {
            mImageView.setImageBitmap(ImageHelper.handleImageEffect(mBitmap, mHue, mSaturation, mLum));
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
}

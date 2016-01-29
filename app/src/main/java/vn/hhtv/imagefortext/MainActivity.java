package vn.hhtv.imagefortext;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;

import vn.hhtv.imagefortext.fragments.ImageFragment;
import vn.hhtv.imagefortext.widget.AutoResizeEditText;

public class MainActivity extends AppCompatActivity {

    private AutoResizeEditText mContentEdt;
    private ViewPager mViewPager;
    private Handler mHandler;
    private RelativeLayout mRootView;
    private String mContent = "";
    private Bitmap bitmap;
    private int timeDelay = 1 * 1000; // 1second
    private Runnable runCheck = new Runnable() {
        @Override
        public void run() {
            checkContent();
//            mHandler.postDelayed(this, timeDelay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mContentEdt = (AutoResizeEditText) findViewById(R.id.editText);
        mRootView = (RelativeLayout) findViewById(R.id.rootView);
        mRootView.setDrawingCacheEnabled(false);
        mContentEdt.addTextChangedListener(contentChange);
        mContentEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewPager.onTouchEvent(event);
                return false;
            }
        });
        mContentEdt.setHeightLimit(500);
        mViewPager.setOffscreenPageLimit(4);
        mContentEdt.setSelected(false);
        if(Build.VERSION.SDK_INT > 10)
        mContentEdt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        mViewPager.setAdapter(new ImagePageAdapter(getSupportFragmentManager()));
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    mRootView.setDrawingCacheEnabled(true);
                    File sdcard = Environment.getExternalStorageDirectory();
                    File f = new File(sdcard, "temp.jpg");
                    FileOutputStream out = null;
                    out = new FileOutputStream(f);
                    mRootView.setDrawingCacheEnabled(true);
                    bitmap = mRootView.getDrawingCache(true).copy(
                            Bitmap.Config.ARGB_8888, false);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();
                    mRootView.setDrawingCacheEnabled(false);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f)); // imageUri
                    sharingIntent.setType("image/jpg");

                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f)); // imageUri
                    startActivity(Intent.createChooser(sharingIntent, "Share Image"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void checkContent(){
        String text = mContentEdt.getText().toString();
        if(mContent.equals(text)){
            return;
        }
        mContent = text;
    }

    private TextWatcher contentChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mHandler.removeCallbacks(runCheck);
            if(count > 0)
            mHandler.postDelayed(runCheck, timeDelay);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private class ImagePageAdapter extends FragmentPagerAdapter{

        public ImagePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 5;
        }


        @Override
        public Fragment getItem(int position) {
            return ImageFragment.getInstance(position);
        }
    }
}

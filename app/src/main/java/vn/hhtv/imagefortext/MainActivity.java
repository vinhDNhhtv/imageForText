package vn.hhtv.imagefortext;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;

import vn.hhtv.imagefortext.animation.MoveBottomTopAnimation;
import vn.hhtv.imagefortext.animation.MoveLeftRightAnimation;
import vn.hhtv.imagefortext.animation.MovePositionAnimation;
import vn.hhtv.imagefortext.dialogs.SelectionFontDialog;
import vn.hhtv.imagefortext.fragments.ImageFragment;
import vn.hhtv.imagefortext.widget.AutoResizeEditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoResizeEditText mContentEdt;
    private ViewPager mViewPager;
    private Handler mHandler;
    private RelativeLayout mRootView;
    private String mContent = "";
    private Bitmap bitmap;
    public static int timeMove = 200;
    public static float distanceMove = 430f;
    public static float density = 1f;
    private int timeDelay = 2 * 1000; // 1second
    private Runnable runCheck = new Runnable() {
        @Override
        public void run() {
            checkContent();
//            mHandler.postDelayed(this, timeDelay);
        }
    };

    private ImageView mGridBtn, mFbBtn, mInsBtn, mTwBtn, mSettingBtn;
    private LinearLayout mFbRl, mInsRl, mTwRl, mSettingRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        density = getResources().getDisplayMetrics().density;
        distanceMove = density * 100;
        mHandler = new Handler();
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mContentEdt = (AutoResizeEditText) findViewById(R.id.editText);
        mRootView = (RelativeLayout) findViewById(R.id.rootView);
        mGridBtn = (ImageView) findViewById(R.id.grid);
        mGridBtn.setOnClickListener(this);
        mFbBtn = (ImageView) findViewById(R.id.fb);
        mInsBtn = (ImageView) findViewById(R.id.inta);
        mTwBtn = (ImageView) findViewById(R.id.tw);
        mSettingBtn = (ImageView) findViewById(R.id.setting);
        mFbRl = (LinearLayout) findViewById(R.id.fb_rl);
        mFbRl.setOnClickListener(this);
        mInsRl = (LinearLayout) findViewById(R.id.inta_rl);
        mInsRl.setOnClickListener(this);
        mTwRl = (LinearLayout) findViewById(R.id.tw_rl);
        mTwRl.setOnClickListener(this);
        mSettingRl = (LinearLayout) findViewById(R.id.setting_rl);
        mSettingRl.setOnClickListener(this);
        mRootView.setDrawingCacheEnabled(false);
        mContentEdt.addTextChangedListener(contentChange);
        mContentEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewPager.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_MOVE)
                    return true;
                return false;
            }
        });
        mContentEdt.setHeightLimit(500);
        mViewPager.setOffscreenPageLimit(4);
        mContentEdt.setSelected(false);
        if (Build.VERSION.SDK_INT > 10)
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
    }

    private void capture(){
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

    private void checkContent() {
        String text = mContentEdt.getText().toString();
        if (mContent.equals(text)) {
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
            if (count > 0)
                mHandler.postDelayed(runCheck, timeDelay);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean isExpand = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.grid:
                mFbRl.startAnimation(new MoveLeftRightAnimation(mFbRl,isExpand, null));
                mSettingRl.startAnimation(new MoveBottomTopAnimation(mSettingRl, isExpand, null));
                mInsRl.startAnimation(new MovePositionAnimation(mInsRl, 6f, isExpand,null));
                mTwRl.startAnimation(new MovePositionAnimation(mTwRl, 3f, isExpand,null));
                isExpand = !isExpand;
                break;
            case R.id.fb_rl:
                v.startAnimation(scaleClick());
                onClick(mGridBtn);
                capture();
                break;
            case R.id.tw_rl:
                v.startAnimation(scaleClick());
                onClick(mGridBtn);
                break;
            case R.id.inta_rl:
                v.startAnimation(scaleClick());
                onClick(mGridBtn);
                break;
            case R.id.setting_rl:
                v.startAnimation(scaleClick());
                onClick(mGridBtn);
                SelectionFontDialog dialog = SelectionFontDialog.newInstance(new SelectionFontDialog.SelectedFontListener() {
                    @Override
                    public void onSelected(int p, String font) {
                        Typeface type = Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/" + font);
                        mContentEdt.setTypeface(type);
                    }
                });
                dialog.show(getSupportFragmentManager(), "SelectionFont");
                break;

        }
    }

    private static Animation moveLeftToRight(boolean reverse) {
        AnimationSet animationSet = new AnimationSet(true);
        Animation moveLefttoRight = new TranslateAnimation(reverse ? -distanceMove : 0,reverse ? 0 : -distanceMove, 0, 0);
        moveLefttoRight.setDuration(timeMove);
        moveLefttoRight.setFillAfter(true);
        Animation aSc = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if(reverse){
            animationSet.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        }
        aSc.setDuration(timeMove);
        aSc.setFillAfter(true);
        animationSet.addAnimation(aSc);
        animationSet.addAnimation(moveLefttoRight);
        Animation alp = new AlphaAnimation(reverse? 1.0f : 0.0f, reverse ? 0.0f : 1.0f);
        alp.setDuration(timeMove);
        alp.setFillAfter(true);
        animationSet.addAnimation(alp);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private static Animation scaleClick() {
        AnimationSet animationSet = new AnimationSet(true);
        Animation aSc = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        aSc.setDuration(timeMove);
        aSc.setFillAfter(false);
        animationSet.addAnimation(aSc);
        animationSet.setFillAfter(false);
        return animationSet;
    }

    private static Animation moveBottomToTop(boolean reverse) {
        AnimationSet animationSet = new AnimationSet(true);
        Animation moveLefttoRight = new TranslateAnimation(0, 0, reverse ? -distanceMove : 0,reverse ? 0 : -distanceMove);
        moveLefttoRight.setDuration(timeMove);
        moveLefttoRight.setFillAfter(false);
        Animation aSc = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if(reverse){
            animationSet.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        }
        aSc.setDuration(timeMove);
        aSc.setFillAfter(true);
        animationSet.addAnimation(aSc);
        animationSet.addAnimation(moveLefttoRight);
        Animation alp = new AlphaAnimation(reverse? 1.0f : 0.0f, reverse ? 0.0f : 1.0f);
        alp.setDuration(timeMove);
        alp.setFillAfter(true);
        animationSet.addAnimation(alp);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private static Animation moveToPosition(float p, boolean reverse) {
        AnimationSet animationSet = new AnimationSet(true);
        Animation moveLefttoRight = new TranslateAnimation(0f, -(float) (distanceMove * Math.cos(Math.PI / p)), 0f, -(float) (distanceMove * Math.sin(Math.PI / p)));
        if(reverse){
            moveLefttoRight = new TranslateAnimation(-(float) (distanceMove * Math.cos(Math.PI / p)), 0f, -(float) (distanceMove * Math.sin(Math.PI / p)),0f);
        }
        moveLefttoRight.setDuration(timeMove);
        moveLefttoRight.setFillAfter(true);
        Animation aSc = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if(reverse){
            animationSet.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        }
        aSc.setDuration(timeMove);
        aSc.setFillAfter(true);
        animationSet.addAnimation(moveLefttoRight);
        animationSet.addAnimation(aSc);
        Animation alp = new AlphaAnimation(reverse? 1.0f : 0.0f, reverse ? 0.0f : 1.0f);
        alp.setDuration(timeMove);
        alp.setFillAfter(true);
        animationSet.addAnimation(alp);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private class ImagePageAdapter extends FragmentPagerAdapter {

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

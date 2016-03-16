package vn.hhtv.imagefortext;

/**
 * Created by vinhdn on 3/12/16.
 */

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;

import org.apache.http.conn.ConnectTimeoutException;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import vn.hhtv.imagefortext.activities.base.BaseActivity;

public class ThreadCheckRotate extends Thread {

    BaseActivity activity;
    boolean rotateAfterCheck;
    boolean canRotate = false;
    boolean rotating = false;
    float beforeRotate = 0f;
    float afterRotate = 0f;
    boolean interrupt = false;
    String bonus;
    ImageView wheelView, wheelArrow;
    RelativeLayout rotateLayout;
    String[] score = {"200", "100", "1000", "100", "200", "500", "100", "200",
            "100", "200", "100", "300"};

    private boolean isRotating;
    private int rota = 0;
    Handler mHandler;
    Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            if(isRotating){
                rota += 10;
                if(Build.VERSION.SDK_INT > 10)
                wheelView.setRotation(rota % 360 );
                mHandler.postDelayed(this, 10);
            }else {
                rota = 0;
            }
        }
    };
    public ThreadCheckRotate(BaseActivity activity,
                             RelativeLayout rotateLayout, ImageView wheelView,
                             ImageView wheelArrow, boolean rotateAfterCheck) {
        this.activity = activity;
        this.rotateAfterCheck = rotateAfterCheck;
        this.wheelView = wheelView;
        mHandler = new Handler();
        this.wheelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRotating){
                    rotating = false;
                    isRotating = false;
                    mHandler.removeCallbacks(mRunable);
//                    v.clearAnimation();
                }else {
                    quay();
                }
            }
        });
        this.wheelArrow = wheelArrow;
        this.rotateLayout = rotateLayout;
        rotateLayout.setEnabled(false);
    }

    @Override
    public void run() {
//        canRotate = true;
//        if (rotateAfterCheck) {
//            activity.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    rotate();
//                }
//            });
//        } else {
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    rotateLayout.setEnabled(true);
//                }
//            });
//            canRotate = false;
//            if (rotateAfterCheck) {
//            }
//        }
    }

    private void quay(){
        isRotating = true;
        mHandler.postDelayed(mRunable, 10);
    }

    private void rotate() {
        if (canRotate) {
            if (!rotating) {
                final int sizeVongQuay = score.length;
                final float gocMotVong = 360.0f / sizeVongQuay;
                Random rd = new Random();
                afterRotate = beforeRotate + 1800 + rd.nextInt(sizeVongQuay) * gocMotVong;
                bonus = calCoinRotate(sizeVongQuay, gocMotVong);
                while (bonus.equals("1000")) {
                    beforeRotate = 0f;
                    afterRotate = beforeRotate + 1800 + rd.nextInt(sizeVongQuay) * gocMotVong;
                    bonus = calCoinRotate(sizeVongQuay, gocMotVong);
                }

                afterRotate = 3000 * 1000;
                RotateAnimation anim = new RotateAnimation(0f,
                        afterRotate, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f) {

                };
                anim.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        rotating = true;
                        new Thread(new Runnable() {

                            @SuppressLint("NewApi")
                            @Override
                            public void run() {
                                while (rotating) {

                                    activity.runOnUiThread(new Runnable() {

                                        @SuppressLint("NewApi")
                                        @Override
                                        public void run() {
                                            if (Build.VERSION.SDK_INT >= 11) {
                                                wheelArrow.setRotation(-15);
                                                wheelArrow.setPivotX(wheelArrow.getWidth() * 0.31624f);
                                                wheelArrow.setPivotY(wheelArrow.getHeight() * 0.71242f);
                                            }
                                        }
                                    });
                                    try {
                                        Thread.sleep(90);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    activity.runOnUiThread(new Runnable() {

                                        @SuppressLint("NewApi")
                                        @Override
                                        public void run() {
                                            if (Build.VERSION.SDK_INT >= 11) {
                                                wheelArrow.setRotation(0);
                                            }
                                        }
                                    });
                                    try {
                                        Thread.sleep(90);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        bonus = calCoinRotate(sizeVongQuay, gocMotVong);
                        if (!interrupt) {
                            actionRotateSuccess(bonus);
                        }
                        rotateLayout.setEnabled(true);
                    }
                });
                anim.setDuration(3600 * 1000);
                anim.setRepeatCount(Animation.INFINITE);
                anim.setFillEnabled(true);
                anim.setFillAfter(true);
                wheelView.startAnimation(anim);

            }
        } else {
        }
    }

    private String calCoinRotate(int sizeVongQuay, float gocMotVong) {
        beforeRotate = afterRotate;
        rotating = false;
        int totalRotating = (int) ((afterRotate - 360 * ((int) ((int) afterRotate) / 360)) / gocMotVong);
        bonus = score[totalRotating % sizeVongQuay];
        return bonus;
    }

    private void actionRotateSuccess(String bonus) {
//		LogUtils.log("bonus = " + bonus);
//		ToastHDV.show(activity, bonus);
//        new ThreadUpdateCoinByRotate(activity, bonus).start();
    }

    private void showLoadingDialog() {
    }

    private void hideLoadingDialog() {

    }

    public void stopThread() {
        interrupt = true;
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                wheelView.clearAnimation();
                rotating = false;
                if (Build.VERSION.SDK_INT >= 11) {
                    wheelArrow.setRotation(0);
                }
            }
        });

    }
}
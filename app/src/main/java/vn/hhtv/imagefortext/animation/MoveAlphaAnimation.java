package vn.hhtv.imagefortext.animation;

import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import vn.hhtv.imagefortext.main.MainActivity;

/**
 * Created by vinhdn on 3/11/16.
 */
public class MoveAlphaAnimation extends Animation{
    float ttop;
    int mBLeft;
    View view;
    AnimationListener listener;
    private boolean isEnd = false;
    private boolean isReverse = false;
    public MoveAlphaAnimation(View v, AnimationListener listener) {
        this.listener = listener;
        view = v;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        mBLeft = params.leftMargin;
        this.ttop = (100  + 20) * v.getContext().getResources().getDisplayMetrics().density;
        setDuration(MainActivity.timeMove * 8);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(isReverse){
            interpolatedTime = 1 - interpolatedTime;
        }
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.leftMargin = mBLeft +  (int) (((ttop)) * interpolatedTime);
        view.setLayoutParams(params);
        if(Build.VERSION.SDK_INT > 10)
        view.setAlpha(interpolatedTime);
        if(interpolatedTime >= 1 && !isReverse && !isEnd){
            isEnd = true;
            if(listener != null)
                listener.onAnimationEnd(this);
        }
    }
}

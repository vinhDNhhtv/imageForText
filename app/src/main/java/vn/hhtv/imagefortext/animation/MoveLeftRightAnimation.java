package vn.hhtv.imagefortext.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import vn.hhtv.imagefortext.main.MainActivity;

/**
 * Created by vinh on 2/1/16.
 */
public class MoveLeftRightAnimation extends Animation{
    float ttop;
    View view;
    AnimationListener listener;
    private boolean isEnd = false;
    private boolean isReverse = false;
    public MoveLeftRightAnimation(View v, AnimationListener listener) {
        this.listener = listener;
        view = v;
        this.ttop = MainActivity.distanceMove;
        setDuration(MainActivity.timeMove);
    }

    public MoveLeftRightAnimation(View v,boolean isReverse, AnimationListener listener) {
        this.listener = listener;
        view = v;
        this.isReverse = isReverse;
        this.ttop = MainActivity.distanceMove + 30f * MainActivity.density;
        setDuration(MainActivity.timeMove);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(isReverse){
            interpolatedTime = 1 - interpolatedTime;
        }
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.rightMargin = (int) (((ttop)) * interpolatedTime);
        view.setScaleX(interpolatedTime);
        view.setScaleY(interpolatedTime);
        view.setAlpha(interpolatedTime);
        view.setLayoutParams(params);
        if(interpolatedTime >= 1 && !isReverse && !isEnd){
            isEnd = true;
            if(listener != null)
            listener.onAnimationEnd(this);
        }
    }

}

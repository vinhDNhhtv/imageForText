package vn.hhtv.imagefortext.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import vn.hhtv.imagefortext.main.MainActivity;

/**
 * Created by vinh on 2/1/16.
 */
public class MovePositionAnimation extends Animation{
    float ttop, tleft;
    View view;
    AnimationListener listener;
    private boolean isEnd = false;
    private boolean isReverse = false;
    public MovePositionAnimation(View v, AnimationListener listener) {
        this.listener = listener;
        view = v;
        this.ttop = MainActivity.distanceMove;
        setDuration(MainActivity.timeMove);
    }

    public MovePositionAnimation(View v,boolean isReverse, AnimationListener listener) {
        this.listener = listener;
        view = v;
        this.isReverse = isReverse;
        this.ttop = MainActivity.distanceMove;
        setDuration(MainActivity.timeMove);
    }

    public MovePositionAnimation(View v,float p, boolean isReverse, AnimationListener listener) {
        this.listener = listener;
        view = v;
        this.isReverse = isReverse;
        this.ttop = (MainActivity.distanceMove) * (float)Math.sin(Math.PI / p) + 30f * MainActivity.density;
        this.tleft = (MainActivity.distanceMove) * (float)Math.cos(Math.PI / p) + 30f * MainActivity.density;
        setDuration(MainActivity.timeMove);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(isReverse){
            interpolatedTime = 1 - interpolatedTime;
        }
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.bottomMargin = (int) (((ttop)) * interpolatedTime);
        params.rightMargin = (int) (((tleft)) * interpolatedTime);
        view.setScaleX(interpolatedTime);
        view.setRotation(35);
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

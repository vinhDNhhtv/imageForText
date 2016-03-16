package vn.hhtv.imagefortext.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import vn.hhtv.imagefortext.R;
import vn.hhtv.imagefortext.ThreadCheckRotate;
import vn.hhtv.imagefortext.activities.base.BaseActivity;
import vn.hhtv.imagefortext.main.MainActivity;

public class SplashActivity extends BaseActivity {

    ImageView ivRotate, ivArrow;
    RelativeLayout rootRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ivRotate = (ImageView) findViewById(R.id.imageRotate);
        ivArrow = (ImageView) findViewById(R.id.ic_arrow);
        rootRl = (RelativeLayout) findViewById(R.id.rootRl);
//        ThreadCheckRotate tcR = new ThreadCheckRotate(this, rootRl, ivRotate, ivArrow, true);
//        tcR.run();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 500l);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }
}

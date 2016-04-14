package vn.hhtv.imagefortext.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by vinhdn on 4/6/16.
 */
public class DrawImageView extends SurfaceView implements SurfaceHolder.Callback{

    private static final int BACKGROUND_COLOR = 0xFF28282a;

    private Bitmap mBgBitmap, mBitmap;
    private String mText;

    private Canvas mBitmapCanvas;
    private Paint mClearPaint;
    private Paint mBitmapPaint;
    private Paint mDrawPaint;
    private Context mContext;
    private DrawThead mDrawThead;
    private int mWidth;
    private int mHeight;

    public DrawImageView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DrawImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }


    public void render(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        Rect dest = new Rect(0, 0, getWidth(), getHeight());
        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(mBgBitmap, null, dest, paint); //draw your bitmap

//        droid.draw(canvas);
    }

    private void init(){
        mDrawThead = new DrawThead(getHolder(), this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        getHolder().addCallback(this);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);

        mClearPaint = new Paint();
        mClearPaint.setAntiAlias(true);
        mClearPaint.setDither(true);
        mClearPaint.setColor(BACKGROUND_COLOR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap != null && canvas != null){
            canvas.drawBitmap(mBitmap, 0,0, mBitmapPaint);
        }
        if(mBgBitmap != null){
            mBitmapCanvas.drawBitmap(mBgBitmap, 0,0,mBitmapPaint);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mDrawThead.setRunning(true);
        try {
            this.mDrawThead.start();
            return;
        } catch (Exception localException) {
            Log.e("DrawImageView",localException.toString());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (width != 0 && height != 0) {
            mWidth = width;
            mHeight = height;
            if (mBitmap != null) {
                mBitmap.recycle();
            }
            mBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            mBitmapCanvas = new Canvas(mBitmap);
            clear(mBitmapCanvas);
        }
    }

    public void clear(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawColor(BACKGROUND_COLOR);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mDrawThead.setRunning(false);
        while (retry) {
            try {
                mDrawThead.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void drawText(String text){

    }

    public void drawImage(Bitmap bitmap){
        this.mBgBitmap = bitmap;
    }



    class DrawThead extends Thread {

        private SurfaceHolder mSurfaceHolder;
        private DrawImageView mDrawSurfaceView;
        private boolean mRun = false;

        public DrawThead(SurfaceHolder surfaceHolder, DrawImageView view) {
            mSurfaceHolder = surfaceHolder;
            mDrawSurfaceView = view;
        }

        public void setRunning(boolean running) {
            mRun = running;
        }

        public SurfaceHolder getSurfaceHolder() {
            return mSurfaceHolder;
        }

        @Override
        public void run() {
            super.run();
            Canvas canvas;
            while (mRun) {
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        mDrawSurfaceView.onDraw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

    }

}

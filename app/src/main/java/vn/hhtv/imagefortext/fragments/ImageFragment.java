package vn.hhtv.imagefortext.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import vn.hhtv.imagefortext.main.MainActivity;
import vn.hhtv.imagefortext.R;
import vn.hhtv.imagefortext.models.Image;

/**
 * Created by iservice on 1/29/16.
 */
public class ImageFragment extends Fragment{

    int[] colors = {Color.CYAN, Color.BLUE, Color.DKGRAY, Color.GREEN, Color.YELLOW};
    String[] images = {"http://maxcdn.thedesigninspiration.com/wp-content/uploads/2013/09/mobileswall-047.jpg",
            "http://wallpaperswide.com/download/beautiful_space_view-wallpaper-768x1024.jpg",
    "http://wallpaperswide.com/download/glass_ball_2-wallpaper-768x1024.jpg",
    "http://www.onsecrethunt.com/wallpaper/wp-content/uploads/2015/01/Best-Mobile-Live-Wallpapers-Nature.jpg",
    "http://wallpaperswide.com/download/drops_of_water-wallpaper-640x960.jpg"};
    private int color = Color.CYAN;
    String image = "";

    public Image getImageM() {
        return imageM;
    }

    private Image imageM;
    String text = "nature";
    private ImageView iv;
    private RelativeLayout rootRl;

    public static ImageFragment getInstance(int position){
        ImageFragment fragment = new ImageFragment();
        fragment.color = fragment.colors[position % fragment.colors.length];
        fragment.image = fragment.images[position % fragment.images.length];
        return fragment;
    }
    public static ImageFragment getInstance(int position, Image image){
        ImageFragment fragment = new ImageFragment();
        fragment.imageM = image;
        fragment.color = fragment.colors[position % fragment.colors.length];
        fragment.image = fragment.images[position % fragment.images.length];
        return fragment;
    }
    public static ImageFragment getInstance(int position, Image image, String text){
        ImageFragment fragment = ImageFragment.getInstance(position, image);
        fragment.text = text;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_fragment, container, false);
        final ProgressBar pb = (ProgressBar) v.findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);
        pb.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        iv = (ImageView) v.findViewById(R.id.imageView);
        rootRl = (RelativeLayout) v.findViewById(R.id.rootRl);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setImageResource(R.drawable.bg_background);
        changeView();
        RequestCreator qc = null;
        if(imageM != null) {
            if(imageM.isOffline()){
                qc = Picasso.with(container.getContext()).load(Uri.parse(imageM.getSource()));
            }else {
                qc = Picasso.with(container.getContext()).load(imageM.getSource());
            }
        }else if(image != null)
        qc = Picasso.with(container.getContext()).load(image);
        if(qc != null){
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
//                        RenderScript rs = RenderScript.create(getContext());
//
////this will blur the bitmapOriginal with a radius of 8 and save it in bitmapOriginal
//                        final Allocation input = Allocation.createFromBitmap(rs, bitmap); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
//                        final Allocation output = Allocation.createTyped(rs, input.getType());
//                        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//                        script.setRadius(0f);
//                        script.setInput(input);
//                        script.forEach(output);
//                        output.copyTo(bitmap);
                        iv.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    pb.setVisibility(View.GONE);
                    Picasso.with(inflater.getContext()).load("http://lorempixel.com/" + MainActivity.screenImage + "/" + text).skipMemoryCache().into(iv);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            qc.into(iv
                    , new Callback() {
                @Override
                public void onSuccess() {
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    pb.setVisibility(View.GONE);
                    Picasso.with(inflater.getContext()).load("http://lorempixel.com/" + MainActivity.screenImage + "/" + text).skipMemoryCache().into(iv);
                }
            }
            );
        }
        return v;
    }

    public Bitmap getBitmap(){
        Bitmap bitmap = null;
        if(iv != null && iv.getDrawable() != null)
            bitmap = ((BitmapDrawable)iv.getDrawable()).getBitmap();
        if(bitmap != null){
            if(((MainActivity)getActivity()).getStateCrop() == 0) {
                if (bitmap.getWidth() >= bitmap.getHeight()) {

                    return Bitmap.createBitmap(
                            bitmap,
                            bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                            0,
                            bitmap.getHeight(),
                            bitmap.getHeight()
                    );
                } else {

                    return Bitmap.createBitmap(
                            bitmap,
                            0,
                            bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                            bitmap.getWidth(),
                            bitmap.getWidth()
                    );
                }
            }
            else if(((MainActivity)getActivity()).getStateCrop() == 2){
                return bitmap;
            }else {
                return Bitmap.createBitmap(
                        bitmap,
                        0,
                        bitmap.getHeight() / 2 - bitmap.getWidth() * 9 / 32,
                        bitmap.getWidth(),
                        bitmap.getWidth() * 9 / 16
                );
            }
        }
        return bitmap;
    }

    public void changeView(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        int mWidth = 0;
        if(display != null) {
            if (display != null) {
                if (Build.VERSION.SDK_INT >= 13) {
                    display.getSize(point);
                    mWidth = point.x;
                } else {
                    mWidth = display.getWidth();
                }
            }
        }
        int mHeight = 0;
//        if(((MainActivity)getActivity()).getStateCrop() == 1){
//            mHeight = (int)(((mWidth * 9f) / 16f));
//        }else if(((MainActivity)getActivity()).getStateCrop() == 0){
//            mHeight = (int)(((mWidth * 9f) / 16f));
//        }
        if (((MainActivity)getActivity()).getStateCrop() == 1) {
            mHeight = (int) (((mWidth * 9f) / 16f)) - (int)(20 * getResources().getDisplayMetrics().density);
        } else if (((MainActivity)getActivity()).getStateCrop() == 0) {
            mHeight = mWidth - (int)(20 * getResources().getDisplayMetrics().density);
        }else if(((MainActivity)getActivity()).getStateCrop() == 2){
            mHeight = (int) (((mWidth * 16f) / 9f)) - (int)(20 * getResources().getDisplayMetrics().density);
        }
        if(rootRl == null) return;
        final ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) iv.getLayoutParams();
//        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        lp.height = mHeight;
        final ViewGroup.LayoutParams lpr = (ViewGroup.LayoutParams) rootRl.getLayoutParams();
//        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        lpr.height = mHeight;
        iv.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv.setLayoutParams(lp);
            }
        }, 10);
//        rootRl.setLayoutParams(lpr);
    }
}

package vn.hhtv.imagefortext.fragments;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    private Image imageM;
    String text = "nature";
    private ImageView iv;

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
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setImageResource(R.drawable.bg_background);
        changeView();
        RequestCreator qc = null;
        if(imageM != null) {
            qc = Picasso.with(container.getContext()).load(imageM.getSource());
        }else
        qc = Picasso.with(container.getContext()).load(image);
        if(qc != null){
            qc.into(iv, new Callback() {
                @Override
                public void onSuccess() {
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    pb.setVisibility(View.GONE);
                    Picasso.with(inflater.getContext()).load("http://lorempixel.com/" + MainActivity.screenImage + "/" + text).skipMemoryCache().into(iv);
                }
            });
        }
        return v;
    }

    public void changeView(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        int mWidth = 0;
        if(display != null) {
            if (display != null) {
                if (Build.VERSION.SDK_INT >= 13) {
                    display.getSize(point);
                    mWidth = point.y;
                } else {
                    mWidth = display.getWidth();
                }
            }
        }
        int mHeight = RelativeLayout.LayoutParams.MATCH_PARENT;
        if(((MainActivity)getActivity()).getStateCrop() == 1){
            mHeight = (int)(((mWidth * 9f) / 16f) / getResources().getDisplayMetrics().density);
        }else if(((MainActivity)getActivity()).getStateCrop() == 0){
            mHeight = (int)(((mWidth * 9f) / 16f));
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                mHeight);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        if(iv != null)
        iv.setLayoutParams(lp);
    }
}

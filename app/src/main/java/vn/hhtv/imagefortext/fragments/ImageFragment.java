package vn.hhtv.imagefortext.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

    public static ImageFragment getInstance(int position){
        ImageFragment fragment = new ImageFragment();
        fragment.color = fragment.colors[position % fragment.colors.length];
        fragment.image = fragment.images[position % fragment.images.length];
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView iv = new ImageView(container.getContext());
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setBackgroundColor(color);
        Picasso.with(container.getContext()).load(image).into(iv);
        return iv;
    }
}

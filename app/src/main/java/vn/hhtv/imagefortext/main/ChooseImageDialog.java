package vn.hhtv.imagefortext.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import vn.hhtv.imagefortext.R;
import vn.hhtv.imagefortext.utils.ImageUtil;

/**
 * Created by vinhdn on 3/29/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ChooseImageDialog extends android.support.v4.app.DialogFragment{

    RecyclerView mRecyclerView;
    List<String> listImage;

    public ChooseImageDialog(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_image_dialog_layout, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(), 3));
        if(listImage != null)
            mRecyclerView.setAdapter(new ThumbImageAdapter(listImage));
        return view;
    }

    public void setListImage(List<String> data){
        listImage = data;
        if(mRecyclerView != null)
        mRecyclerView.setAdapter(new ThumbImageAdapter(listImage));
    }
}

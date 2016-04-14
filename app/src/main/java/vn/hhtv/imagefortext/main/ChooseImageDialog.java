package vn.hhtv.imagefortext.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

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

    private View.OnClickListener clickListener;
    public ChooseImageDialog(){
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // e.g. bottom + left margins:
//        dialog.getWindow().setGravity(Gravity.BOTTOM| Gravity.LEFT);
//        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
//        layoutParams.x = 100; // left margin
//        layoutParams.y = 170; // bottom margin
//        dialog.getWindow().setAttributes(layoutParams);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.choose_image_dialog_layout, null, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        if(listImage != null)
            mRecyclerView.setAdapter(new ThumbImageAdapter(listImage, clickListener));
        if(clickListener != null)
            view.findViewById(R.id.take_photo_btn).setOnClickListener(clickListener);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setClickListener(View.OnClickListener listener){
        this.clickListener = listener;
        if(getView() != null){
            getView().findViewById(R.id.take_photo_btn).setOnClickListener(clickListener);
        }
    }

    public void setListImage(List<String> data){
        listImage = data;
        if(mRecyclerView == null && getView() != null){
            mRecyclerView = (RecyclerView)getView().findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getView().getContext(), 3));
        }
        if(mRecyclerView != null)
        mRecyclerView.setAdapter(new ThumbImageAdapter(listImage,clickListener));
    }
}

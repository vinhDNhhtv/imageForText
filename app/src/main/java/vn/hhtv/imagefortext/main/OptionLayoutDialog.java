package vn.hhtv.imagefortext.main;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import vn.hhtv.imagefortext.R;

/**
 * Created by vinhdn on 4/15/16.
 */
public class OptionLayoutDialog extends DialogFragment{

    RecyclerView mRecyclerView;
    private View.OnClickListener clickListener;
    String text, image;
    private int typeLayout, width;
    float textSize;
    private String font;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.option_layout_dialog, null, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mRecyclerView.getLayoutParams();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int width = 0;
        int height = 0;
        try {
            if (Build.VERSION.SDK_INT >= 13) {
                display.getSize(size);
                width = size.x;
                height = size.y;
            }
        } catch (NoSuchMethodError e) {
            width = display.getWidth();
            height = display.getHeight();
        }
        float density = getResources().getDisplayMetrics().density;
        lp.height = (int)((width - 40 * density) * 4 / 3 + 10 * density);
        mRecyclerView.setLayoutParams(lp);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        if(!TextUtils.isEmpty(image)){
            mRecyclerView.setAdapter(new OptionLayoutAdapter(image, text, textSize, font, typeLayout, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(clickListener != null)
                        clickListener.onClick(v);
                }
            }));
        }
        return dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setClickListener(View.OnClickListener listener){
        this.clickListener = listener;
    }

    public void setData(String url, String text, float textSize, String font, int typeLayout) {
        this.text = text;
        this.image = url;
        this.textSize = textSize;
        this.font = font;
        this.typeLayout = typeLayout;
        if(mRecyclerView != null){
            mRecyclerView.setAdapter(new OptionLayoutAdapter(image, text, textSize, font, typeLayout, clickListener));
        }
    }

}

package vn.hhtv.imagefortext.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.hhtv.imagefortext.R;

/**
 * Created by vinh on 2/1/16.
 */
public class SelectionFontDialog extends DialogFragment{

    public interface SelectedFontListener{
        void onSelected(int p, String font);
    }

    RecyclerView mRecyclerView;
    SelectedFontListener mListener;

    public static SelectionFontDialog newInstance(SelectedFontListener listener){
        SelectionFontDialog dialog = new SelectionFontDialog();
        dialog.mListener = listener;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.selection_font_layout,null, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new FontAdapter());
//        dialog.setContentView(view);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    class FontAdapter extends RecyclerView.Adapter<FontHolder>{

        Context mContext;

        @Override
        public FontHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = getContext();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_font, null, false);
            return new FontHolder(view);
        }

        @Override
        public void onBindViewHolder(FontHolder holder, int position) {
            String font = getResources().getStringArray(R.array.list_font)[position];
            Typeface type = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + font);
            holder.mTextView.setTypeface(type);
        }

        @Override
        public int getItemCount() {
            return getResources().getStringArray(R.array.list_font).length;
        }
    }

    class FontHolder extends RecyclerView.ViewHolder{

        TextView mTextView;

        public FontHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onSelected(getPosition(), getResources().getStringArray(R.array.list_font)[getPosition()]);
                        dismiss();
                    }
                }
            });
            mTextView = (TextView)itemView.findViewById(R.id.textView);
        }
    }
}

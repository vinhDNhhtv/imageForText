package vn.hhtv.imagefortext.main;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.IOException;

import vn.hhtv.imagefortext.R;
import vn.hhtv.imagefortext.widget.AutoResizeEditText;

/**
 * Created by vinhdn on 4/15/16.
 */
public class OptionLayoutAdapter extends RecyclerView.Adapter<OptionLayoutAdapter.ViewHolder> {

    String text, image, font;
    private int typeLayout, width;
    private View.OnClickListener listener;
    float textSize;

    public OptionLayoutAdapter(String url, String text, float textSize, String font, int typeLayout) {
        this.text = text;
        this.image = url;
        this.font = font;
        this.textSize = textSize;
        this.typeLayout = typeLayout;
    }

    public OptionLayoutAdapter(String url, String text, float textSize, String font, int typeLayout, View.OnClickListener listener) {
        this.text = text;
        this.image = url;
        this.font = font;
        this.textSize = textSize;
        this.typeLayout = typeLayout;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option_layout, parent, false);
        width = parent.getWidth();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RelativeLayout.LayoutParams lap = (RelativeLayout.LayoutParams) holder.mImageView.getLayoutParams();
        FrameLayout.LayoutParams lapText = (FrameLayout.LayoutParams) holder.mTextEdt.getLayoutParams();
        ViewGroup.LayoutParams lapRoot = holder.itemView.getLayoutParams();
        lap.height = width * 27 / 90;
        lapRoot.height = lap.height;
        holder.itemView.setLayoutParams(lapRoot);
        RequestCreator qc = null;
        if (image != null) {
            if (image.startsWith("http"))
                qc = Picasso.with(holder.mImageView.getContext()).load(image).resize(lap.height, lap.height);
            else {
                qc = Picasso.with(holder.mImageView.getContext()).load(Uri.parse("file://" + image)).resize(lap.height, lap.height);

            }
        }
        if (qc != null) {
            qc.centerCrop();
//            try {
//                Bitmap bitmap = qc.get();
//                RenderScript rs = RenderScript.create(holder.itemView.getContext());
//
////this will blur the bitmapOriginal with a radius of 8 and save it in bitmapOriginal
//                final Allocation input = Allocation.createFromBitmap(rs, bitmap); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
//                final Allocation output = Allocation.createTyped(rs, input.getType());
//                final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//                script.setRadius(8f);
//                script.setInput(input);
//                script.forEach(output);
//                output.copyTo(bitmap);
//                holder.mImageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
//                        RenderScript rs = RenderScript.create(holder.itemView.getContext());
//
////this will blur the bitmapOriginal with a radius of 8 and save it in bitmapOriginal
//                        final Allocation input = Allocation.createFromBitmap(rs, bitmap); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
//                        final Allocation output = Allocation.createTyped(rs, input.getType());
//                        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//                        script.setRadius(3f);
//                        script.setInput(input);
//                        script.forEach(output);
//                        output.copyTo(bitmap);
                        holder.mImageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            qc.into(holder.mImageView);
        }
        holder.mImageView.setLayoutParams(lap);
        holder.mTextEdt.setText(text);
        holder.mTextEdt.setTextSize(textSize / 3f);
        Log.d("Font size", " " + textSize + "   " + holder.mTextEdt.getTextSize());
        if (!TextUtils.isEmpty(font)) {
            Typeface type = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "fonts/" + font);
            holder.mTextEdt.setTypeface(type);
        }
        holder.mTextEdt.setBackgroundColor(Color.parseColor("#90000000"));
        switch (position) {

            case 0: // Top Left
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                lapText.gravity = Gravity.TOP | Gravity.LEFT;
                holder.mTextEdt.setGravity(Gravity.LEFT);
                break;
            case 1: // Top center
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                lapText.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                holder.mTextEdt.setGravity(Gravity.CENTER);
                break;
            case 2: // Top right
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                lapText.gravity = Gravity.TOP | Gravity.RIGHT;
                holder.mTextEdt.setGravity(Gravity.RIGHT);
                break;
            case 3: // Center left
//                lapText.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                lapText.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                holder.mTextEdt.setGravity(Gravity.LEFT);
                break;
            case 4: // Center center
//                lapText.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                lapText.gravity = Gravity.CENTER;
                holder.mTextEdt.setGravity(Gravity.CENTER);
                break;
            case 5: // Center right
//                lapText.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                lapText.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                holder.mTextEdt.setGravity(Gravity.RIGHT);
                break;
            case 6: // Bottom Left
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                lapText.gravity = Gravity.BOTTOM | Gravity.LEFT;
                holder.mTextEdt.setGravity(Gravity.LEFT);
                break;
            case 7: // Bottom center
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                lapText.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                holder.mTextEdt.setGravity(Gravity.CENTER);
                break;
            case 8: // Bottom right
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                lapText.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                holder.mTextEdt.setGravity(Gravity.RIGHT);
                break;
            case 9: // Bottom Left
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                lapText.gravity = Gravity.BOTTOM | Gravity.LEFT;
                holder.mTextEdt.setBackgroundColor(Color.parseColor("#000000"));
                holder.mTextEdt.setGravity(Gravity.LEFT);
                break;
            case 10: // Bottom center
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                lapText.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                holder.mTextEdt.setBackgroundColor(Color.parseColor("#000000"));
                holder.mTextEdt.setGravity(Gravity.CENTER);
                break;
            case 11: // Bottom right
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                lapText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                lapText.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                holder.mTextEdt.setBackgroundColor(Color.parseColor("#000000"));
                holder.mTextEdt.setGravity(Gravity.RIGHT);
                break;
            default:
        }
        holder.mTextEdt.setLayoutParams(lapText);
        holder.mTextEdt.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.mTextEdt.reAdjust();
            }
        }, 50);
        if (listener != null) {
            holder.rootView.setTag(position);
            holder.rootView.setOnClickListener(listener);
        }
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private AutoResizeEditText mTextEdt;
        private View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mTextEdt = (AutoResizeEditText) itemView.findViewById(R.id.textEdt);
            rootView = itemView.findViewById(R.id.rootRl);
        }
    }
}

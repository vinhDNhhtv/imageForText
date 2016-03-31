package vn.hhtv.imagefortext.main;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import vn.hhtv.imagefortext.R;
import vn.hhtv.imagefortext.models.Image;

/**
 * Created by vinhdn on 3/15/16.
 */
public class ThumbImageAdapter extends RecyclerView.Adapter<ThumbImageAdapter.ThumbViewHolder>{

//    private List<Image> images;
    private List<String> imagesLocal;

    public ThumbImageAdapter(List<String> data){
        this.imagesLocal = data;
    }

    @Override
    public ThumbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumb_selection, parent, false);
        return new ThumbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ThumbViewHolder holder, int position) {
        String image = imagesLocal.get(position);
        if(image == null) return;
        RequestCreator qc = null;
        holder.imageView.setImageResource(0);
        if(image != null) {
            qc = Picasso.with(holder.imageView.getContext()).load(Uri.parse("file://" + image)).centerInside();
        }
        if(qc != null){
            qc.into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if(imagesLocal == null) return 0;
        return imagesLocal.size();
    }

    class ThumbViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView, cbox;

        public ThumbViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.imageView);
            cbox = (ImageView)itemView.findViewById(R.id.checkbox);
        }
    }
}

package com.and.netshare.home.homepage.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.and.netshare.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private ArrayList<SingleImage> imageArrayList;
    private Context context;

    ImagesAdapter(Context c,ArrayList<SingleImage> imageArrayList){
        this.imageArrayList = imageArrayList;
        this.context = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_single_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("acg_images/" + imageArrayList.get(position).getPath());
        Glide.with(context)
                .asBitmap()
                .load(ref)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.picture);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder){
        super.onViewRecycled(holder);
        ImageView viewR = holder.picture;
        if (viewR != null){
            Glide.with(context).clear(viewR);
        }
    }

    @Override
    public int getItemCount() {
        return imageArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;

        ViewHolder(View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.image_picture);
        }
    }
}

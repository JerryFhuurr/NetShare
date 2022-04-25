package com.and.netshare.home.homepage.images.anime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.and.netshare.R;
import com.and.netshare.home.homepage.images.SingleImage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.ViewHolder> {
    private ArrayList<SingleImage> imagesAnime;
    private Context context;
    private OnClickListener listener;

    AnimeAdapter(Context c, ArrayList<SingleImage> imagesAnime) {
        this.imagesAnime = imagesAnime;
        this.context = c;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
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
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("acg_images/" + imagesAnime.get(position).getPath());
        Glide.with(context)
                .asDrawable()
                .load(ref)
                .placeholder(R.drawable.loading_icon)
                .error(R.drawable.loading_failed_icon)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .into(holder.picture);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        ImageView viewR = holder.picture;
        if (viewR != null) {
            Glide.with(context).clear(viewR);
        }
    }

    @Override
    public int getItemCount() {
        return imagesAnime.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;

        ViewHolder(View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.image_picture);
            itemView.setOnClickListener(view -> {
                listener.onClick(imagesAnime.get(getBindingAdapterPosition()));
            });
        }
    }

    public interface OnClickListener {
        void onClick(SingleImage singleImage);
    }
}
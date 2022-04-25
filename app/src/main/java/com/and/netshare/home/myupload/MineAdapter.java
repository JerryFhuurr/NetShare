package com.and.netshare.home.myupload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.and.netshare.R;
import com.and.netshare.home.homepage.images.SingleImage;
import com.and.netshare.home.homepage.images.anime.AnimeAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MineAdapter extends RecyclerView.Adapter<MineAdapter.ViewHolder> {
    private ArrayList<SingleImage> imagesAnime;
    private ArrayList<SingleImage> imagesMeme;
    private ArrayList<SingleImage> imagesGame;
    private Context context;
    private AnimeAdapter.OnClickListener listener;
    private int catNum;

    MineAdapter(Context context, ArrayList<SingleImage> images, int num) {
        this.context = context;
        this.catNum = num;
        if (num == 0) {
            this.imagesAnime = images;
        } else if (num == 1) {
            this.imagesMeme = images;
        } else if (num == 2) {
            this.imagesGame = images;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_single_image_my, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StorageReference ref = null;
        if (catNum == 0) {
            ref = FirebaseStorage.getInstance().getReference().child("acg_images/" + imagesAnime.get(position).getPath());
        } else if (catNum == 1) {
            ref = FirebaseStorage.getInstance().getReference().child("memes/" + imagesMeme.get(position).getPath());
        } else if (catNum == 2) {
            ref = FirebaseStorage.getInstance().getReference().child("game_images/" + imagesGame.get(position).getPath());
        }
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
    public int getItemCount() {
        if (catNum == 0) {
            return imagesAnime.size();
        } else if (catNum == 1) {
            return imagesMeme.size();
        } else if (catNum == 2) {
            return imagesGame.size();
        }
        return 0;
    }

    public void setOnClickListener(AnimeAdapter.OnClickListener listener) {
        this.listener = listener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;

        ViewHolder(View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.image_picture_my);
            itemView.setOnClickListener(view -> {
                if (catNum == 0) {
                    listener.onClick(imagesAnime.get(getBindingAdapterPosition()));
                } else if (catNum == 1) {
                    listener.onClick(imagesMeme.get(getBindingAdapterPosition()));
                } else if (catNum == 2) {
                    listener.onClick(imagesGame.get(getBindingAdapterPosition()));
                }
            });
        }
    }

    public interface OnClickListener {
        void onClick(SingleImage singleImage);
    }
}

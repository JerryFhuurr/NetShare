package com.and.netshare.home.homepage.images;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.and.netshare.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Date;

public class SingleImageFragmentZoom extends Fragment {

    private ImageView image;
    private TextView name;
    private TextView cat;
    private TextView uploadTime;

    private StorageReference reference;
    private StorageReference imageRef;
    private String nameString;

    public SingleImageFragmentZoom() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseStorage.getInstance().getReference();
        nameString = SingleImage.getStaticPath();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_single_image_zoom, container, false);
        image = v.findViewById(R.id.image_zoom);
        name = v.findViewById(R.id.zoom_imageName);
        cat = v.findViewById(R.id.zoom_imageCategory);
        uploadTime = v.findViewById(R.id.zoom_uploadTime);

        name.setText(shortenString(nameString));
        uploadTime.setText("Upload : " + getTimeFromPath(nameString));

        if (SingleImage.getCategory().equals("Anime")) {
            cat.setText("Category : " + getString(R.string.tab_1));
            imageRef = reference.child("acg_images/" + SingleImage.getStaticPath());
            Glide.with(getContext())
                    .asDrawable()
                    .load(imageRef)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(image);
        } else if (SingleImage.getCategory().equals("Game")) {
            cat.setText("Category : " + getString(R.string.tab_3));
            imageRef = reference.child("game_images/" + SingleImage.getStaticPath());
            Glide.with(getContext())
                    .asDrawable()
                    .load(imageRef)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(image);
        } else if (SingleImage.getCategory().equals("Meme")) {
            cat.setText("Category : " + getString(R.string.tab_2));
            imageRef = reference.child("memes/" + SingleImage.getStaticPath());
            Glide.with(getContext())
                    .asDrawable()
                    .load(imageRef)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(image);
        }
        return v;
    }

    private String shortenString(String pathString) {
        int position = pathString.indexOf('_');
        String shorten = pathString.substring(position + 1, pathString.length());
        return shorten;
    }

    private String getTimeFromPath(String path) {
        int position = path.indexOf('+');
        String timeString = path.substring(0, position);
        Long timeLong = Long.valueOf(timeString);
        Date date = new Date(timeLong);
        DateFormat fDateFormat = DateFormat.getDateTimeInstance();
        return fDateFormat.format(date);
    }

}
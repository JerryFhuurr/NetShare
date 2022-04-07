package com.and.netshare.home.homepage.images;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.and.netshare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AnimeFragment extends Fragment {

    private FirebaseStorage storage;
    private RecyclerView images;
    private ImagesAdapter adapter;
    private GridLayoutManager layoutManager;
    ArrayList<SingleImage> imageList = new ArrayList<>();

    public AnimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_anime, container, false);
        StorageReference listRef = storage.getReference().child("acg_images");

        images = v.findViewById(R.id.anime_recycle);

        images.hasFixedSize();
        images.setLayoutManager(new LinearLayoutManager(getContext()));

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item: listResult.getItems()){
                            imageList.add(new SingleImage(item.getName()));
                        }
                        adapter = new ImagesAdapter(getContext(), imageList);
                        images.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("home failure", e.getMessage());
                    }
                });


        return v;
    }
}
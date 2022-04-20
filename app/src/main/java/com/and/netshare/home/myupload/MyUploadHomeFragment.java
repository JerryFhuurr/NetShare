package com.and.netshare.home.myupload;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.and.netshare.DataHandler;
import com.and.netshare.R;
import com.and.netshare.home.homepage.images.SingleImage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyUploadHomeFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private StorageReference userIconRef;
    private FirebaseDatabase db;
    private DatabaseReference usernameRef;
    private DatabaseReference userIconDBRef;
    private TextView username;
    private ImageView userIconVIew;
    private ImageView animeIcon;
    private ImageView memeIcon;
    private ImageView gameIcon;
    ArrayList<SingleImage> images = new ArrayList<>();

    public MyUploadHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        db = FirebaseDatabase.getInstance("https://netshare-f4723-default-rtdb.asia-southeast1.firebasedatabase.app");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_upload_home, container, false);
        userIconVIew = v.findViewById(R.id.mine_userIcon);
        username = v.findViewById(R.id.mine_username);
        animeIcon = v.findViewById(R.id.mine_anime_tab);
        memeIcon = v.findViewById(R.id.mine_meme_tab);
        gameIcon = v.findViewById(R.id.mine_game_tab);

        String userEmail = DataHandler.changeDotToComaEmail(currentUser.getEmail());
        usernameRef = db.getReference("UserEmail/" + userEmail);
        userIconDBRef = db.getReference("UserIcon/" + userEmail);
        getUsername(usernameRef);
        getUserIcon(userIconDBRef, v);

        StorageReference tabImageRef1 = FirebaseStorage.getInstance().getReference().child("acg_images");
        loadData(tabImageRef1, v, 0);
        StorageReference tabImageRef2 = FirebaseStorage.getInstance().getReference().child("memes");
        loadData(tabImageRef2, v, 1);
        StorageReference tabImageRef3 = FirebaseStorage.getInstance().getReference().child("game_images");
        loadData(tabImageRef3, v, 2);

        animeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUploadsHandler.setNum(0);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, new MineImagesFragment(), null)
                        .addToBackStack(null)
                        .commit();
            }
        });
        memeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUploadsHandler.setNum(1);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, new MineImagesFragment(), null)
                        .addToBackStack(null)
                        .commit();
            }
        });
        gameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUploadsHandler.setNum(2);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, new MineImagesFragment(), null)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return v;
    }

    private void getUsername(DatabaseReference ref) {
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String userName = String.valueOf(task.getResult().getValue());
                    username.setText(userName);
                }
            }
        });
    }

    private void getUserIcon(DatabaseReference r, View view) {
        r.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase icon", "Error getting data", task.getException());
                } else {
                    String path = String.valueOf(task.getResult().getValue());
                    Log.d("icon path", String.valueOf(task.getResult().getValue()));
                    userIconRef = FirebaseStorage.getInstance().getReference("user_icons/").child(path);
                    Glide.with(view)
                            .asDrawable()
                            .load(userIconRef)
                            .placeholder(R.drawable.loading_icon)
                            .error(R.drawable.loading_failed_icon)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .into(userIconVIew);
                }
            }
        });
    }

    private void loadData(StorageReference ref, View v, int categoryNum) {
        images.clear();
        ref.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            if (item.getName().contains(currentUser.getEmail())) {
                                images.add(new SingleImage(item.getName()));
                            }
                        }
                        Log.d("items", String.valueOf(images.size()));
                        loadImage(images.get(images.size() - 1) .getPath(), v, categoryNum);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("home anime failure", e.getMessage());
                    }
                });
    }

    private void loadImage(String path, View view, int categoryNum) {
        if (categoryNum == 0) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("acg_images/" + path);
            Glide.with(view)
                    .asDrawable()
                    .load(storageReference)
                    .placeholder(R.drawable.loading_icon_small)
                    .error(R.drawable.loading_error_icon_small)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .into(animeIcon);
        } else if (categoryNum == 1) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("memes/" + path);
            Glide.with(view)
                    .asDrawable()
                    .load(storageReference)
                    .placeholder(R.drawable.loading_icon_small)
                    .error(R.drawable.loading_error_icon_small)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .into(memeIcon);
        } else if (categoryNum == 2) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("game_images/" + path);
            Glide.with(view)
                    .asDrawable()
                    .load(storageReference)
                    .placeholder(R.drawable.loading_icon_small)
                    .error(R.drawable.loading_error_icon_small)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .into(gameIcon);
        }

    }
}
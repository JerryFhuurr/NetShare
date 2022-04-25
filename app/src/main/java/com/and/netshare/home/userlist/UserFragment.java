package com.and.netshare.home.userlist;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.and.netshare.CacheDataManager;
import com.and.netshare.DataHandler;
import com.and.netshare.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserFragment extends Fragment {

    private TextView userEmail;
    private TextView userNameText;
    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private DatabaseReference iconRef;
    private StorageReference storageReference;

    private String userName;

    private Button account;
    private Button about;
    private Button cache;
    private Button settings;
    private ImageView iconView;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance("https://netshare-f4723-default-rtdb.asia-southeast1.firebasedatabase.app");

        currentUser = userAuth.getCurrentUser();
        userName = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        userEmail = v.findViewById(R.id.user_email);
        iconView = v.findViewById(R.id.user_userIcon);

        account = v.findViewById(R.id.user_account);
        about = v.findViewById(R.id.user_about);
        settings = v.findViewById(R.id.user_settings);
        cache = v.findViewById(R.id.user_cache);
        userNameText = v.findViewById(R.id.user_name);
        userEmail.setText(currentUser.getEmail());

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean imageType = sharedPreferences.getBoolean("image_dateType", false);
        String email = DataHandler.changeDotToComaEmail(currentUser.getEmail());
        reference = db.getReference("UserEmail/" + email);
        iconRef = db.getReference("UserIcon/" + email);
        getUserName(reference);
        getUserIcon(iconRef, v, imageType);


        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_accountFragment);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_aboutFragment);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_settingsFragment);
            }
        });

        cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder cleanWarn = new AlertDialog.Builder(getContext());
                cleanWarn.setIcon(R.drawable.clean_icon);
                try {
                    cleanWarn.setTitle(getString(R.string.cache_title) + " " + CacheDataManager.getTotalCacheSize(getContext()));
                    cleanWarn.setMessage(R.string.cache_message);
                    cleanWarn.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            CacheDataManager.clearAllCache(getContext());
                            Toast.makeText(getContext(), R.string.cache_OK, Toast.LENGTH_SHORT).show();
                        }
                    });
                    cleanWarn.setNegativeButton(R.string.cache_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close
                        }
                    });
                    cleanWarn.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }


    private void getUserName(DatabaseReference ref) {
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    userName = String.valueOf(task.getResult().getValue());
                    userNameText.setText(userName);
                }
            }
        });
    }

    private void getUserIcon(DatabaseReference r, View view, boolean imageType) {
        r.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase icon", "Error getting data", task.getException());
                } else {
                    String path = String.valueOf(task.getResult().getValue());
                    Log.d("icon path", String.valueOf(task.getResult().getValue()));
                    storageReference = FirebaseStorage.getInstance().getReference("user_icons/").child(path);
                    if (imageType) {
                        Glide.with(view)
                                .asDrawable()
                                .load(storageReference)
                                .placeholder(R.drawable.loading_icon)
                                .error(R.drawable.loading_failed_icon)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .centerCrop()
                                .into(iconView);
                    } else {
                        Glide.with(view)
                                .asDrawable()
                                .load(storageReference)
                                .placeholder(R.drawable.loading_icon)
                                .error(R.drawable.loading_failed_icon)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .into(iconView);
                    }
                }
            }
        });
    }
}
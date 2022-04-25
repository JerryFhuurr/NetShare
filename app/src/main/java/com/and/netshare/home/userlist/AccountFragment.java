package com.and.netshare.home.userlist;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.and.netshare.DataHandler;
import com.and.netshare.FileProvider;
import com.and.netshare.PhotoUtils;
import com.and.netshare.R;
import com.and.netshare.UriUtils;
import com.and.netshare.home.homepage.UploadFragment;
import com.and.netshare.home.homepage.images.SingleImageZoomActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AccountFragment extends Fragment {

    private EditText userName;
    private TextView userEmail;
    private Button editUsername;
    private Button updatePassword;
    private ImageView iconView;
    private Uri photoOutputUri;
    private String mImagePath;
    private ProgressDialog progressDialog;

    private String iconNewPath = "";

    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference nameDbRef;
    private DatabaseReference iconDbRef;
    private StorageReference iconSRef;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance("https://netshare-f4723-default-rtdb.asia-southeast1.firebasedatabase.app");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        userName = v.findViewById(R.id.accountName);
        userEmail = v.findViewById(R.id.userEmail);
        userEmail.setText(currentUser.getEmail());
        editUsername = v.findViewById(R.id.account_change_name);
        updatePassword = v.findViewById(R.id.change_password);
        iconView = v.findViewById(R.id.account_icon);
        progressDialog = new ProgressDialog(getContext());

        nameDbRef = db.getReference("UserEmail/" + DataHandler.changeDotToComaEmail(currentUser.getEmail()));
        iconDbRef = db.getReference("UserIcon/" + DataHandler.changeDotToComaEmail(currentUser.getEmail()));

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean imageType = sharedPreferences.getBoolean("image_dateType", false);
        getUserName(nameDbRef);
        getUserIcon(iconDbRef, imageType);

        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                Intent local = new Intent(Intent.ACTION_PICK, null);
                local.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(local, 1);
            }
        });

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameDbRef.setValue(userName.getText().toString());
                Snackbar.make(v, R.string.account_setUsername, Snackbar.LENGTH_LONG).show();
            }
        });

        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuth.sendPasswordResetEmail(currentUser.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Reset password email sent.");

                                    Snackbar.make(v, R.string.account_resetPassword, Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
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
                    Log.d("firebase info", String.valueOf(task.getResult().getValue()));
                    String userNameString = String.valueOf(task.getResult().getValue());
                    userName.setText(userNameString);
                }
            }
        });
    }

    private void getUserIcon(DatabaseReference r, boolean imageType) {
        r.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase icon", "Error getting data", task.getException());
                } else {
                    String path = String.valueOf(task.getResult().getValue());
                    Log.d("icon path", String.valueOf(task.getResult().getValue()));
                    iconSRef = FirebaseStorage.getInstance().getReference("user_icons/").child(path);
                    if (imageType) {
                        Glide.with(getView())
                                .asDrawable()
                                .load(iconSRef)
                                .placeholder(R.drawable.loading_icon)
                                .error(R.drawable.loading_failed_icon)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .centerCrop()
                                .into(iconView);
                    } else {
                        Glide.with(getView())
                                .asDrawable()
                                .load(iconSRef)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = null;

        switch (requestCode) {
            case 1:
                if (data != null) {
                    if (null != data) {
                        photoOutputUri = data.getData();
                        mImagePath = data.toString();

                        //copy the origin file to Documents and crop it as user icon
                        //delete the copied file which stored in Documents
                        String pathO = UriUtils.getRealFilePathB(getContext(), photoOutputUri);
                        iconNewPath = "/storage/emulated/0/Documents/NetIcons" + DataHandler.getFileName(pathO) + "_copy" + "." + DataHandler.getSuffix(pathO);
                        FileProvider.copyFile(pathO, iconNewPath);
                        photoOutputUri = UriUtils.getUriFromPath(iconNewPath);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            intent = PhotoUtils.startPhotoZoom(getActivity(), data.getData(), photoOutputUri, 500);
                        } else {
                            intent = PhotoUtils.startPhotoZoom(getActivity(), data.getData(), mImagePath, 500);
                        }
                    }
                    startActivityForResult(intent, 2);
                }
                break;
            case 2:
                checkPermission();
                if (data != null) {
                    photoOutputUri = data.getData();
                    Glide.with(getActivity()).asDrawable()
                            .load(photoOutputUri)
                            .placeholder(R.drawable.loading_icon)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
                            .into(iconView);
                    Log.d("icon after", iconNewPath);

                    //upload icon to database
                    StorageReference iconNew = FirebaseStorage.getInstance().getReference("user_icons/").child(currentUser.getEmail());
                    InputStream iconStream = null;
                    try {
                        iconStream = new FileInputStream(new File(iconNewPath));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    showProgressDialog();
                    UploadTask storageTask = iconNew.putStream(iconStream);
                    storageTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v("icon failed", "upload failed");
                            progressDialog.dismiss();
                            Snackbar.make(getView(), R.string.upload_failed, Snackbar.LENGTH_SHORT).show();
                        }
                    })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    iconDbRef.setValue(currentUser.getEmail());
                                    Log.v("success", "upload success");
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    Toast.makeText(getContext(), R.string.upload_success, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });

                }
                break;
        }
    }

    private void checkPermission() {
        //permissions
        String[] PERMISSIONS = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"};
        //检测是否有写的权限
        int permission = ContextCompat.checkSelfPermission(getContext(),
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 1);
        }
    }

    private void showProgressDialog() {
        progressDialog.setTitle(R.string.upload_waiting_dialog_title);
        progressDialog.setMessage(getText(R.string.upload_waiting_dialog_message));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
package com.and.netshare;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class UploadFragment extends Fragment {

    private Button choose_local;
    private Button choose_camera;
    private Button upload;
    private ImageView preview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload, container, false);
        choose_camera = v.findViewById(R.id.choose_camera);
        choose_local = v.findViewById(R.id.choose_local);
        upload = v.findViewById(R.id.upload);
        preview = v.findViewById(R.id.image_preview);

        choose_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent local = new Intent(Intent.ACTION_PICK, null);
                local.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(local, 2);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (data != null) {
                Uri uri = data.getData();
                Log.i("uri of image", uri.toString());

                preview.setImageURI(uri);
            }
        } else {
            //operation error or not select image
            Log.i("MainActivtiy", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
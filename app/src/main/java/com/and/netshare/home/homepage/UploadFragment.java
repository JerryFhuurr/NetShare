package com.and.netshare.home.homepage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.and.netshare.DataHandler;
import com.and.netshare.R;
import com.and.netshare.UriUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UploadFragment extends Fragment {

    private Button choose_local;
    //private Button choose_camera;
    private Button upload;
    private ImageView preview;
    private Uri imageURI;
    private FirebaseStorage storage;
    private EditText imageName;
    private Spinner imageCat;
    private String imageCategory;
    private StorageReference storageRef;
    private Bitmap imageGet;
    private TextView sizeError;
    private String suffix;
    private String filePath;
    private FirebaseUser currentUser;
    //private final String filePath = Environment.getExternalStorageDirectory() + File.separator + "output_image.jpg";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        imageCategory = "";
        suffix = "";
        filePath = "";
        chooseImageDefault();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload, container, false);

        choose_local = v.findViewById(R.id.choose_local);
        upload = v.findViewById(R.id.upload);
        preview = v.findViewById(R.id.image_preview);
        imageName = v.findViewById(R.id.image_name);
        imageCat = v.findViewById(R.id.imageCat);
        sizeError = v.findViewById(R.id.preview_sizeError);

        choose_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent local = new Intent(Intent.ACTION_PICK, null);
                local.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(local, 2);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                if (suffix.equals("gif")){

                }else {
                    imageGet.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                }
                byte[] data = outputStream.toByteArray();


                 */
                try {
                    InputStream stream = new FileInputStream(new File(filePath));

                    if (imageCategory.equals("Anime")) {
                        storageRef = storage.getReference().child("acg_images");
                    } else if (imageCategory.equals("Meme")) {
                        storageRef = storage.getReference().child("memes");
                    } else if (imageCategory.equals("Game image")) {
                        storageRef = storage.getReference().child("game_images");
                    }
                    String uploadName = "";
                    if (imageName.getText().equals("")) {
                        uploadName = System.currentTimeMillis() + "+" + currentUser.getEmail() + "_ new " + imageCategory;
                    } else {
                        uploadName = System.currentTimeMillis() + "+" + currentUser.getEmail() + "_" + imageName.getText().toString();
                    }
                    StorageReference localRef = storageRef.child(uploadName);
                    UploadTask uploadTask = localRef.putStream(stream);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.v("failed", "upload failed");
                            Snackbar.make(getView(), R.string.upload_failed, Snackbar.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.v("success", "upload success");
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            Toast.makeText(getContext(), R.string.upload_success, Toast.LENGTH_SHORT).show();
                            UploadFragment.this.getActivity().onBackPressed();
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        imageCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                imageCategory = imageCat.getSelectedItem().toString();
                Log.i("image cat", imageCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //no func
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //1 -> camera ; 2 -> local file
            case 2:
                if (data != null) {
                    try {
                        imageURI = data.getData();
                        Log.d("uri path", UriUtils.getFileAbsolutePath(getContext(), imageURI));
                        imageName.setText("");

                        filePath = UriUtils.getFileAbsolutePath(getContext(), imageURI);
                        suffix = DataHandler.getSuffix(filePath);

                        if (suffix.equals("gif")) {
                            Glide.with(getView()).asDrawable().load(imageURI).into(preview);
                        } else {
                            //should scale the size of image
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                imageGet = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getActivity().getContentResolver(), imageURI));
                            } else
                                imageGet = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageURI);
                            //if image is larger than 2.8MB, use scaled bitmap
                            if (imageGet.getByteCount() >= 16000000) {
                                final Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageGet,
                                        (int) (imageGet.getWidth() * 0.5)
                                        , (int) (imageGet.getHeight() * 0.5)
                                        , true);
                                sizeError.setText(R.string.upload_size_error);
                                preview.setImageBitmap(scaledBitmap);
                            } else {
                                preview.setImageBitmap(imageGet);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("file error", e.getMessage());
                        Toast.makeText(getContext(), R.string.upload_file_error, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            default:
                //operation error or not select image
                Log.i("upload fragment", "operation error");
                break;
        }
    }

    private void chooseImageDefault() {
        Intent local = new Intent(Intent.ACTION_PICK, null);
        local.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(local, 2);
    }


}
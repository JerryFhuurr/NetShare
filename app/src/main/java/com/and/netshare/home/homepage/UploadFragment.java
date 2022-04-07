package com.and.netshare.home.homepage;

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
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.and.netshare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

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
    //private final String filePath = Environment.getExternalStorageDirectory() + File.separator + "output_image.jpg";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        imageCategory = "";
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
                preview.setDrawingCacheEnabled(true);
                preview.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) preview.getDrawable()).getBitmap();

                 */
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                imageGet.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] data = outputStream.toByteArray();

                if (imageCategory.equals("ACG image")) {
                    Log.v("working1", "acg");
                    storageRef = storage.getReference().child("acg_images");
                } else if (imageCategory.equals("Meme")) {
                    Log.v("working2", "meme");
                    storageRef = storage.getReference().child("memes");
                } else if (imageCategory.equals("Game image")) {
                    Log.v("working3", "game");
                    storageRef = storage.getReference().child("game_images");
                }
                StorageReference localRef = storageRef.child(imageURI.getLastPathSegment());
                UploadTask uploadTask = localRef.putBytes(data);
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
                        NavHostFragment.findNavController(UploadFragment.this).navigate(R.id.action_uploadFragment_to_homePageFragment);
                    }
                });
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
                        imageName.setText(imageURI.getLastPathSegment());

                        //should scale the size of image
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            imageGet = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getActivity().getContentResolver(), imageURI));
                        } else
                            imageGet = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageURI);
                        Log.i("size", String.valueOf(imageGet.getByteCount()));
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
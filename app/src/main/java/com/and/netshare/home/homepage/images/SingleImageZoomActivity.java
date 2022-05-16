package com.and.netshare.home.homepage.images;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.and.netshare.home.MainActivity;
import com.and.netshare.handlers.PhotoUtils;
import com.and.netshare.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Date;

public class SingleImageZoomActivity extends AppCompatActivity {
    private Toolbar topBar;
    private Bitmap imageSave;

    private ImageView image;
    private TextView name;
    private TextView cat;
    private TextView uploadTime;
    private Button delete;

    private StorageReference reference;
    private StorageReference imageRef;
    private String nameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image_zoom);
        setBasicData();
        initView();

        name.setText(shortenString(nameString));
        uploadTime.setText("Upload : " + getTimeFromPath(nameString));

        if (SingleImage.getCategory().equals("Anime")) {
            cat.setText("Category : " + getString(R.string.tab_1));
            imageRef = reference.child("acg_images/" + SingleImage.getStaticPath());
            loadImage(imageRef);
        } else if (SingleImage.getCategory().equals("Game")) {
            cat.setText("Category : " + getString(R.string.tab_3));
            imageRef = reference.child("game_images/" + SingleImage.getStaticPath());
            loadImage(imageRef);
        } else if (SingleImage.getCategory().equals("Meme")) {
            cat.setText("Category : " + getString(R.string.tab_2));
            imageRef = reference.child("memes/" + SingleImage.getStaticPath());
            loadImage(imageRef);
        }

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                popMenu(view, imageRef);
                return true;
            }
        });

        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(SingleImageZoomActivity.this);
        deleteAlert.setTitle(R.string.zoom_dialog_title);
        deleteAlert.setIcon(R.drawable.alert_icon);
        deleteAlert.setMessage(R.string.zoom_dialog_message);
        deleteAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                imageRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SingleImageZoomActivity.this, R.string.zoom_delete_ok, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SingleImageZoomActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SingleImageZoomActivity.this,
                                        "An error occurred when deleting image", Toast.LENGTH_SHORT).show();
                                Log.d("delete error", e.getMessage());
                            }
                        });
            }
        });
        deleteAlert.setNegativeButton("Let me think", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //nothing here, just cancel the operation
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlert.show();
            }
        });
    }

    private void loadImage(StorageReference ref) {
        Glide.with(this)
                .asDrawable()
                .load(ref)
                .thumbnail(Glide.with(this).load(R.drawable.loading_animation))
                .error(R.drawable.ic_loading_fail)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(image);
    }

    private void setBasicData() {
        reference = FirebaseStorage.getInstance().getReference();
        nameString = SingleImage.getStaticPath();
    }

    private void initView() {
        image = findViewById(R.id.image_zoom);
        name = findViewById(R.id.zoom_imageName);
        cat = findViewById(R.id.zoom_imageCategory);
        uploadTime = findViewById(R.id.zoom_uploadTime);
        topBar = findViewById(R.id.topBar);
        delete = findViewById(R.id.zoom_delete);

        if (nameString.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            delete.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(topBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_image_topbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            checkPermission();
            generateBmp(imageRef);
        }
        return super.onOptionsItemSelected(item);
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

    private void popMenu(View v, StorageReference r) {
        PopupMenu menu = new PopupMenu(SingleImageZoomActivity.this, v);
        //设置布局
        menu.getMenuInflater().inflate(R.menu.single_image_pop_menu, menu.getMenu());
        //设置点击事件
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.single_save) {
                    checkPermission();
                    generateBmp(r);
                    return true;
                } else if (item.getItemId() == R.id.single_share) {
                    Toast.makeText(SingleImageZoomActivity.this, "Share", Toast.LENGTH_SHORT).show();
                    r.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ClipboardManager clipboardManager = (ClipboardManager) SingleImageZoomActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("share_link", uri.toString());
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(SingleImageZoomActivity.this, R.string.zoom_share_ok, Toast.LENGTH_SHORT).show();

                            Intent intent_share = new Intent(Intent.ACTION_SEND);
                            intent_share.setType("text/plain");
                            intent_share.putExtra(Intent.EXTRA_TEXT, uri.toString());
                            startActivity(intent_share);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        menu.show();
    }

    private void checkPermission() {
        //permissions
        String[] PERMISSIONS = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"};
        //检测是否有写的权限
        int permission = ContextCompat.checkSelfPermission(SingleImageZoomActivity.this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(SingleImageZoomActivity.this, PERMISSIONS, 1);
        }
    }

    private void generateBmp(StorageReference reference) {
        Glide.with(this).asBitmap().load(reference)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageSave = resource;
                        PhotoUtils.saveImageToGallery(SingleImageZoomActivity.this, imageSave, "DCIM/NET");
                    }
                });
    }



}
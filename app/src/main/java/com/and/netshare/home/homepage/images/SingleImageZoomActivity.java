package com.and.netshare.home.homepage.images;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.and.netshare.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class SingleImageZoomActivity extends AppCompatActivity {
    private Toolbar topBar;
    private AppBarConfiguration configuration;
    private Bitmap imageSave;

    private ImageView image;
    private TextView name;
    private TextView cat;
    private TextView uploadTime;

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
    }

    private void loadImage(StorageReference ref) {
        Glide.with(this)
                .asDrawable()
                .load(ref)
                .placeholder(R.drawable.loading_icon)
                .error(R.drawable.loading_failed_icon)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
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
                        saveImageToGallery(SingleImageZoomActivity.this, imageSave);
                    }
                });
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "DCIM/NET");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "NetShare_" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
        Toast.makeText(context, R.string.zoom_save_ok, Toast.LENGTH_SHORT).show();
    }

}
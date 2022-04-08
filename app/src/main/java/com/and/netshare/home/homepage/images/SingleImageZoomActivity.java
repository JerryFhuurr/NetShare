package com.and.netshare.home.homepage.images;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.and.netshare.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Date;

public class SingleImageZoomActivity extends AppCompatActivity {
    private Toolbar topBar;
    private DrawerLayout layout;

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
            Glide.with(this)
                    .asDrawable()
                    .load(imageRef)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(image);
        } else if (SingleImage.getCategory().equals("Game")) {
            cat.setText("Category : " + getString(R.string.tab_3));
            imageRef = reference.child("game_images/" + SingleImage.getStaticPath());
            Glide.with(this)
                    .asDrawable()
                    .load(imageRef)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(image);
        } else if (SingleImage.getCategory().equals("Meme")) {
            cat.setText("Category : " + getString(R.string.tab_2));
            imageRef = reference.child("memes/" + SingleImage.getStaticPath());
            Glide.with(this)
                    .asDrawable()
                    .load(imageRef)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(image);
        }

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return false;
            }
        });
    }

    private void setBasicData(){
        reference = FirebaseStorage.getInstance().getReference();
        nameString = SingleImage.getStaticPath();
    }

    private void initView(){
        image = findViewById(R.id.image_zoom);
        name = findViewById(R.id.zoom_imageName);
        cat = findViewById(R.id.zoom_imageCategory);
        uploadTime = findViewById(R.id.zoom_uploadTime);
        topBar = findViewById(R.id.topBar);
        layout = findViewById(R.id.single_container);

        setSupportActionBar(topBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_image_topbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if (itemId == R.id.action_save){
            //save image
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
}
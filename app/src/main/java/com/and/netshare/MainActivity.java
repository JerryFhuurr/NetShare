package com.and.netshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.and.netshare.handlers.ActivityManager;
import com.and.netshare.handlers.SoundHandler;
import com.and.netshare.handlers.language.LanguageUtil;
import com.and.netshare.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mainAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private NavController navController;
    private AppBarConfiguration configuration;
    private static boolean isFirst = true;

    private long exitTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLanguage();
        setContentView(R.layout.activity_main);

        mainAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mainAuth.getCurrentUser();
        checkUser(firebaseUser);
        initView();
        setupNavigation();
    }

    private void checkLanguage() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_setting", "English");
        Log.d("language store", language);
        if (isFirst) {
            isFirst = false;
            if (language.equals("English")) {
                LanguageUtil.changeAppLanguage(this, "en", MainActivity.class);
            } else if (language.equals("Simplified - Chinese")) {
                LanguageUtil.changeAppLanguage(this, "ch", MainActivity.class);
            }
        }
    }

    private void initView() {
        drawerLayout = findViewById(R.id.main_drawer);
        bottomNavigationView = findViewById(R.id.bottom_navbar);
        toolbar = findViewById(R.id.topBar);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        appThemeLoad(sharedPreferences, this);

        SoundHandler.initSound(this);

        setSupportActionBar(toolbar);
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        setSupportActionBar(toolbar);

        configuration = new AppBarConfiguration.Builder(R.id.homePageFragment).build();
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        setBottomNavigationView();
    }

    private void setBottomNavigationView() {
        navController.addOnDestinationChangedListener(((navController1, navDestination, bundle) -> {
            final int id = navDestination.getId();
            if (id == R.id.homePageFragment) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                SoundHandler.playMainPageSoundClick();
            } else if (id == R.id.myFragment) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                SoundHandler.playSoundClick();
            } else if (id == R.id.userFragment) {
                bottomNavigationView.setVisibility(View.GONE);
                SoundHandler.playSoundClick();
            }
        }));
    }

    private void checkUser(FirebaseUser currentUser) {
        if (currentUser == null) {
            Toast.makeText(this, R.string.main_login_info, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    public void logoutUser(View v) {
        FirebaseAuth.getInstance().signOut();
        FirebaseUser firebaseUser = mainAuth.getCurrentUser();
        checkUser(firebaseUser);
    }

    public void exitApp(View v) {
        finish();
        ActivityManager.getInstance().exit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, configuration) || super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar, menu);
        return true;
    }

    private void appThemeLoad(SharedPreferences sharedPreferences, Activity a) {
        String appTheme = sharedPreferences.getString("theme_type", "follow_system");
        if (appTheme.equals("follow_system")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (appTheme.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (appTheme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}
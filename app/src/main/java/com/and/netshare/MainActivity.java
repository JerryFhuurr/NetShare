package com.and.netshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    private long exitTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mainAuth.getCurrentUser();
        checkUser(firebaseUser);
        initView();
        setupNavigation();
    }

    private void initView() {
        drawerLayout = findViewById(R.id.main_drawer);
        bottomNavigationView = findViewById(R.id.bottom_navbar);
        toolbar = findViewById(R.id.topBar);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        appThemeLoad(sharedPreferences, this);

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
            if (id == R.id.homePageFragment || id == R.id.myFragment) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                bottomNavigationView.setVisibility(View.GONE);
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

    //TODO 检查是否需要在此处添加语言相关的设置
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
package com.and.netshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
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
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        setSupportActionBar(toolbar);

        configuration = new AppBarConfiguration.Builder(R.id.homePageFragment)
                .setOpenableLayout(drawerLayout).build();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        setBottomNavigationView();
    }

    private void setBottomNavigationView() {
        navController.addOnDestinationChangedListener(((navController1, navDestination, bundle) -> {
            final int id = navDestination.getId();
            if (id == R.id.homePageFragment || id == R.id.userFragment) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                bottomNavigationView.setVisibility(View.GONE);
            }
        }));
    }

    private void checkUser(FirebaseUser currentUser) {
        if (currentUser != null) {
            Toast.makeText(this, getString(R.string.main_info) + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.main_login_info, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void logoutTest(View v) {
        FirebaseAuth.getInstance().signOut();
        FirebaseUser firebaseUser = mainAuth.getCurrentUser();
        checkUser(firebaseUser);
    }
}
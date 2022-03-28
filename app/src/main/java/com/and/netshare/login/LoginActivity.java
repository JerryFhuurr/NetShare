package com.and.netshare.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.and.netshare.R;
import com.and.netshare.login.ui.main.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavController navController;
    private AppBarConfiguration configuration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        initView();
        setupNavigation();
    }

    private void initView(){
        drawerLayout = findViewById(R.id.login_drawer);
        toolbar = findViewById(R.id.login_topbar);
    }

    private void setupNavigation(){
        navController = Navigation.findNavController(this, R.id.nav_login);
        setSupportActionBar(toolbar);

        configuration = new AppBarConfiguration.Builder(R.id.homePageFragment)
                .setOpenableLayout(drawerLayout).build();
    }

}
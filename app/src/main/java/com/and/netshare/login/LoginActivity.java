package com.and.netshare.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.and.netshare.ActivityManager;
import com.and.netshare.R;
import com.and.netshare.login.ui.main.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavController navController;
    private AppBarConfiguration configuration;

    private long exitTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ActivityManager.getInstance().addActivity(this);

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

        configuration = new AppBarConfiguration.Builder(R.id.loginFragment)
                .setOpenableLayout(drawerLayout).build();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), R.string.exit_toast,
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            ActivityManager.getInstance().exit();
            System.exit(0);
        }
    }

}
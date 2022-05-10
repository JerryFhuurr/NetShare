package com.and.netshare.login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.and.netshare.handlers.ActivityManager;
import com.and.netshare.handlers.DataHandler;
import com.and.netshare.MainActivity;
import com.and.netshare.R;
import com.and.netshare.login.ui.main.viewmodel.LoginViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavController navController;
    private AppBarConfiguration configuration;
    private LoginViewModel loginViewModel;

    private long exitTime = 0;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK)
                    goToMainActivity();
                else
                    Toast.makeText(this, R.string.login_otherCancel, Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ActivityManager.getInstance().addActivity(this);

        initView();
        setupNavigation();
    }

    private void initView() {
        drawerLayout = findViewById(R.id.login_drawer);
        toolbar = findViewById(R.id.login_topbar);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_login);
        setSupportActionBar(toolbar);

        configuration = new AppBarConfiguration.Builder(R.id.loginFragment)
                .setOpenableLayout(drawerLayout).build();
    }

    private void setUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://netshare-f4723-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference newUser = db.getReference("UserEmail/");
        DatabaseReference newIcon = db.getReference("UserIcon/");

        newUser.child(DataHandler.changeDotToComaEmail(user.getEmail()))
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String username = String.valueOf(task.getResult().getValue());
                    loginViewModel.setInfo(username, null, "google");
                    if (username.equals(null)) {
                        newUser.child(DataHandler.changeDotToComaEmail(user.getEmail())).setValue("NewUser");
                    } else {
                        newUser.child(DataHandler.changeDotToComaEmail(user.getEmail())).setValue(username);
                    }
                }
            }
        });

        newIcon.child(DataHandler.changeDotToComaEmail(user.getEmail()))
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String userIcon = String.valueOf(task.getResult().getValue());
                    if (userIcon.equals(null)) {
                        newIcon.child(DataHandler.changeDotToComaEmail(user.getEmail())).setValue("default_icon.png");
                    } else {
                        newIcon.child(DataHandler.changeDotToComaEmail(user.getEmail())).setValue(userIcon);
                    }
                }
            }
        });
    }

    private void goToMainActivity() {
        setUserInfo();
        startActivity(new Intent(this, MainActivity.class));
        finish();
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

    public void loginOtherAccount(View v) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .build();

        activityResultLauncher.launch(signInIntent);
    }

}
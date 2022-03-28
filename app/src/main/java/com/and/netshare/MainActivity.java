package com.and.netshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.and.netshare.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mainAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mainAuth.getCurrentUser();
        checkUser(firebaseUser);
    }

    private void checkUser(FirebaseUser currentUser){
        if (currentUser != null){
            Toast.makeText(this, getString(R.string.main_info) + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, R.string.main_login_info, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void logoutTest(View v){
        FirebaseAuth.getInstance().signOut();
        FirebaseUser firebaseUser = mainAuth.getCurrentUser();
        checkUser(firebaseUser);
    }
}
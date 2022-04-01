package com.and.netshare.home.userlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.and.netshare.MainActivity;
import com.and.netshare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UserFragment extends Fragment {

    private TextView userEmail;
    private TextView userNameText;
    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;

    private Button account;
    private Button about;
    private Button settings;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        userEmail = v.findViewById(R.id.user_email);

        account = v.findViewById(R.id.user_account);
        about = v.findViewById(R.id.user_about);
        settings = v.findViewById(R.id.user_settings);

        userNameText = v.findViewById(R.id.user_name);
        userNameText.setText(currentUser.getDisplayName());
        userEmail.setText(currentUser.getEmail());


        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_accountFragment);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_aboutFragment);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserFragment.this).navigate(R.id.action_userFragment_to_settingsFragment);
            }
        });

        return v;
    }
}
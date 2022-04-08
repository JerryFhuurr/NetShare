package com.and.netshare.home.userlist;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.and.netshare.MainActivity;
import com.and.netshare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserFragment extends Fragment {

    private TextView userEmail;
    private TextView userNameText;
    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference reference;

    private String userName;

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
        db = FirebaseDatabase.getInstance("https://netshare-f4723-default-rtdb.asia-southeast1.firebasedatabase.app");
        currentUser = userAuth.getCurrentUser();
        userName = "";
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

        userEmail.setText(currentUser.getEmail());
        String email = DataHandler.changeDotToComaEmail(currentUser.getEmail());
        reference = db.getReference("UserEmail/" + email);
        getUserName(reference);


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


    private void getUserName(DatabaseReference ref){
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase info", String.valueOf(task.getResult().getValue()));
                    userName = String.valueOf(task.getResult().getValue());
                    userNameText.setText(userName);
                }
            }
        });
    }
}
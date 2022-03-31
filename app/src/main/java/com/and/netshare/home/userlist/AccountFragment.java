package com.and.netshare.home.userlist;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.and.netshare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AccountFragment extends Fragment {

    private EditText userName;
    private TextView userEmail;
    private Button editUsername;
    private Button updatePassword;

    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;
    public AccountFragment() {
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
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        userName = v.findViewById(R.id.accountName);
        userEmail = v.findViewById(R.id.userEmail);
        userName.setText(currentUser.getDisplayName());
        userEmail.setText(currentUser.getEmail());
        editUsername = v.findViewById(R.id.account_change_name);
        updatePassword = v.findViewById(R.id.change_password);

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userName.getText().toString())
                        .build();

                currentUser.updateProfile(profileChangeRequest)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d(TAG, "User profile updated.");
                                    Log.d("update username", userName.getText().toString());
                                    Snackbar.make(v, R.string.account_setUsername, Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuth.sendPasswordResetEmail(currentUser.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d(TAG, "Reset password email sent.");

                                    Snackbar.make(v, R.string.account_resetPassword, Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        return v;
    }
}
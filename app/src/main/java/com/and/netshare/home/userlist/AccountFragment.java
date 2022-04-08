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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountFragment extends Fragment {

    private EditText userName;
    private TextView userEmail;
    private Button editUsername;
    private Button updatePassword;

    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference reference;
    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance("https://netshare-f4723-default-rtdb.asia-southeast1.firebasedatabase.app");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        userName = v.findViewById(R.id.accountName);
        userEmail = v.findViewById(R.id.userEmail);
        userEmail.setText(currentUser.getEmail());
        editUsername = v.findViewById(R.id.account_change_name);
        updatePassword = v.findViewById(R.id.change_password);

        reference = db.getReference("UserEmail/" + DataHandler.changeDotToComaEmail(currentUser.getEmail()));
        getUserName(reference);

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.setValue(userName.getText().toString());
                Snackbar.make(v, R.string.account_setUsername, Snackbar.LENGTH_LONG).show();
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

    private void getUserName(DatabaseReference ref){
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase info", String.valueOf(task.getResult().getValue()));
                   String userNameString = String.valueOf(task.getResult().getValue());
                    userName.setText(userNameString);
                }
            }
        });
    }
}
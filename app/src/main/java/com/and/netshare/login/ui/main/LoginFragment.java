package com.and.netshare.login.ui.main;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.and.netshare.ActivityManager;
import com.and.netshare.MainActivity;
import com.and.netshare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private TextView forgetPassword;
    private Button registerButton;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_fragment, container, false);
        emailText = v.findViewById(R.id.email_login);
        passwordText = v.findViewById(R.id.password_login);
        loginButton = v.findViewById(R.id.button_login);
        mAuth = FirebaseAuth.getInstance();
        forgetPassword = v.findViewById(R.id.login_forget);
        registerButton = v.findViewById(R.id.button_register);


        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.setEmail(emailText.getText().toString().trim());
                mViewModel.setPassword(passwordText.getText().toString());
                mAuth.signInWithEmailAndPassword(mViewModel.getEmail(), mViewModel.getPassword())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getContext(), R.string.login_fail_toast,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder resetPassword = new AlertDialog.Builder(getContext());
                resetPassword.setTitle(R.string.login_reset_title);
                resetPassword.setMessage(R.string.login_reset_body);
                final EditText reset_email = new EditText(getContext());
                resetPassword.setView(reset_email);
                resetPassword.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = reset_email.getText().toString();
                        String emailE = emailEncode(email);
                        Log.v("email replace", emailE);

                        mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Email sent.");
                                            Toast.makeText(getContext(), R.string.login_reset_ok, Toast.LENGTH_SHORT).show();
                                        }
                                        //add error if the email isn't exist
                                    }
                                });

                    }
                });

                resetPassword.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                resetPassword.show();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void reload() {
        Toast.makeText(getContext(), R.string.login_toast1, Toast.LENGTH_SHORT).show();
    }

    private void updateUI(FirebaseUser user) {
        mAuth.updateCurrentUser(user);
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    private String emailEncode(String email){
        return email.replace(".", ",");
    }
}
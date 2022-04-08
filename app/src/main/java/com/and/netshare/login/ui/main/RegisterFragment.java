package com.and.netshare.login.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {

    private EditText email;
    private EditText password;
    private EditText password_repeat;
    private Button login;
    private Button ok;
    private FirebaseAuth registerAuth;
    private FirebaseUser newUser;
    private TextView error_label;
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";   //verify email

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        email = v.findViewById(R.id.register_email);
        password = v.findViewById(R.id.register_password);
        password_repeat = v.findViewById(R.id.register_password_repeat);
        login = v.findViewById(R.id.register_change);
        ok = v.findViewById(R.id.register_ok);
        registerAuth = FirebaseAuth.getInstance();
        newUser = registerAuth.getCurrentUser();
        error_label = v.findViewById(R.id.error_label);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(RegisterFragment.this).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString();
                String password1 = password.getText().toString();
                String password2 = password_repeat.getText().toString();

                if (emailString.matches(REGEX_EMAIL) && (password1.trim()).equals(password2.trim()) && password1.length() > 6) {
                    registerAuth.createUserWithEmailAndPassword(emailString, password1)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(), R.string.register_info, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                    }
                                }
                            });
                } else {
                    error_label.setText(R.string.register_error);
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = registerAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }


    private void reload() {
        Toast.makeText(getContext(), R.string.login_toast1, Toast.LENGTH_SHORT).show();
    }

}
package com.and.netshare.home.userlist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.and.netshare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AboutFragment extends Fragment {

    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;
    private TextView gitLink;
    private TextView emailLink;
    private TextView version;

    public AboutFragment() {
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
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        gitLink = v.findViewById(R.id.gitLink);
        emailLink = v.findViewById(R.id.authorEmail);
        version = v.findViewById(R.id.version);

        gitLink.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("git_link", gitLink.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getActivity(), R.string.about_copyLink, Toast.LENGTH_LONG).show();
                return false;
            }
        });

        emailLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"fhuurr@163.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "A message from " + currentUser.getEmail());
                startActivity(intent);
            }
        });


        PackageManager manager = getContext().getPackageManager();
        String versionText = "Version ";
        try {
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            versionText += String.valueOf(info.versionName);
            version.setText(versionText);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("version error", e.getMessage());
        }
        return v;
    }
}
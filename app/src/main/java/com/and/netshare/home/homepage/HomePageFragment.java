package com.and.netshare.home.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.and.netshare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomePageFragment extends Fragment {

    private HomePageSettingsViewModel hpViewModel;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference ref = storage.getReference();
    private FloatingActionButton uploadButton;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);
        uploadButton = v.findViewById(R.id.home_floatButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomePageFragment.this).navigate(R.id.action_homePageFragment_to_uploadFragment);
            }
        });
        return v;
    }


}
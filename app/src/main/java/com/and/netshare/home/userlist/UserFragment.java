package com.and.netshare.home.userlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.and.netshare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UserFragment extends Fragment {

    private RecyclerView userSettingList;
    private UserAdapter userAdapter;
    private TextView userEmail;
    private TextView userName;
    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;
    private ArrayList<UserItem> items = new ArrayList<>();

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
        userName = v.findViewById(R.id.user_name);
        userEmail.setText(currentUser.getEmail());
        userName.setText(currentUser.getDisplayName());

        userSettingList = v.findViewById(R.id.user_list_view);
        userSettingList.hasFixedSize();
        userSettingList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Arraylist for all the items
        items.add(new UserItem(getString(R.string.user_item1), R.drawable.account_icon));
        items.add(new UserItem(getString(R.string.user_item2), R.drawable.about_icon));
        items.add(new UserItem(getString(R.string.user_item3), R.drawable.settings_icon));
        items.add(new UserItem(getString(R.string.user_item4), R.drawable.logout_icon));

        userAdapter = new UserAdapter(items);
        userSettingList.setAdapter(userAdapter);
        return v;
    }
}
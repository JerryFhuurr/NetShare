package com.and.netshare.home.homepage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import com.and.netshare.R;
import com.and.netshare.home.homepage.images.AnimeFragment;
import com.and.netshare.home.homepage.images.GamesFragment;
import com.and.netshare.home.homepage.images.MemeFragment;
import com.and.netshare.home.homepage.images.PageTitle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HomePageFragment extends Fragment {

    private HomePageSettingsViewModel hpViewModel;
    private FirebaseStorage storage;
    private StorageReference ref;
    private FloatingActionButton uploadButton;
    private TabLayout tabs;
    private ViewPager viewPager;
    ArrayList fragments = new ArrayList<Fragment>();


    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();

        initFragments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);
        MPagerAdapter adapter = new MPagerAdapter(getChildFragmentManager());
        tabs = v.findViewById(R.id.tabs);
        viewPager = v.findViewById(R.id.homepage_images_viewpager);
        tabs.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);

        uploadButton = v.findViewById(R.id.home_floatButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomePageFragment.this).navigate(R.id.action_homePageFragment_to_uploadFragment);
            }
        });

        return v;
    }

    private void initFragments(){
        fragments.add(new AnimeFragment());
        fragments.add(new MemeFragment());
        fragments.add(new GamesFragment());
    }

    class MPagerAdapter extends FragmentPagerAdapter {
        String[] temp = {getString(R.string.tab_1), getString(R.string.tab_2), getString(R.string.tab_3)};
        PageTitle title = new PageTitle();

        public MPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        //返回tablayout的标题文字;
        @Override
        public CharSequence getPageTitle(int position) {
            return temp[position];
        }
    }
}



package com.and.netshare.home.homepage.images.game;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.and.netshare.handlers.DataHandler;
import com.and.netshare.R;
import com.and.netshare.home.homepage.images.SingleImage;
import com.and.netshare.home.homepage.images.SingleImageZoomActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GamesFragment extends Fragment {

    private FirebaseStorage storage;
    private RecyclerView images;
    private Button filter;
    private SwipeRefreshLayout refresh;
    private GameAdapter adapter;
    private boolean shouldReverse;
    ArrayList<SingleImage> imageList = new ArrayList<>();
    ArrayList<SingleImage> imageListReverse = new ArrayList<>();

    public GamesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_games, container, false);
        StorageReference listRef = storage.getReference().child("game_images");

        refresh = v.findViewById(R.id.game_refresh);
        //?????????????????????????????????????????????
        refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        //??????????????????????????????
        refresh.setColorSchemeResources(R.color.red, R.color.royalBlue, R.color.teal_200);
        final Handler handler = new Handler();
        // ???????????????SwipeRefreshLayout????????????????????????????????????????????????????????????
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // ??????????????????????????????????????????
                //swipeRefreshLayout.setRefreshing(true);

                // ??????????????????
                // ???????????????????????????????????????????????????????????????????????????????????????
                new Thread(){
                    @Override
                    public void run () {
                        super.run();
                        //????????????????????????
                        //???????????? ????????? ?????????????????? ???????????????
                        loadList(shouldReverse);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // ??????????????????????????????????????????????????????????????????
                                refresh.setRefreshing(false);
                            }
                        }, 500);
                    }
                }.start();
            }
        });

        images = v.findViewById(R.id.game_recycler);
        images.hasFixedSize();
        loadData(listRef);

        filter = v.findViewById(R.id.game_sortSelector);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popMenu(view);
            }
        });
        return v;
    }

    private void loadData(StorageReference ref){
        imageList.clear();
        ref.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item: listResult.getItems()){
                            imageList.add(new SingleImage(item.getName()));
                        }
                        imageListReverse = DataHandler.reserveImageList(imageList);
                        loadList(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("home anime failure", e.getMessage());
                    }
                });
    }

    private void loadList(boolean needReverse) {
        if (!needReverse) {
            adapter = new GameAdapter(getContext(), imageList);
        } else {
            adapter = new GameAdapter(getContext(), imageListReverse);
        }
        images.setAdapter(adapter);
        adapter.setOnClickListener(singleImage -> {
            SingleImage.setPathStatic(singleImage.getPath());
            SingleImage.setCategory("Game");
            Intent intent = new Intent(getActivity(), SingleImageZoomActivity.class);
            startActivity(intent);
        });
    }

    private void popMenu(View v) {
        PopupMenu menu = new PopupMenu(GamesFragment.this.getContext(), v);
        menu.getMenuInflater().inflate(R.menu.filter_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.time_OtoN) {
                    shouldReverse = false;
                    loadList(false);
                } else if (item.getItemId() == R.id.time_NtoO) {
                    shouldReverse = true;
                    loadList(true);
                }
                return false;
            }
        });
        menu.show();
    }
}
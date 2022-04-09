package com.and.netshare.home.homepage.images.anime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.and.netshare.DataHandler;
import com.and.netshare.R;
import com.and.netshare.home.homepage.images.SingleImage;
import com.and.netshare.home.homepage.images.SingleImageZoomActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AnimeFragment extends Fragment {

    private FirebaseStorage storage;
    private Button filter;
    private RecyclerView images;
    private AnimeAdapter adapter;
    private SwipeRefreshLayout refresh;
    private boolean shouldReverse;
    ArrayList<SingleImage> imageList = new ArrayList<>();
    ArrayList<SingleImage> imageListReverse = new ArrayList<>();

    public AnimeFragment() {
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
        View v = inflater.inflate(R.layout.fragment_anime, container, false);
        StorageReference listRef = storage.getReference().child("acg_images");

        refresh = v.findViewById(R.id.anime_refresh);
        //下拉进度条的背景色，默认为白色
        refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        //设置下拉进度的主题色
        refresh.setColorSchemeResources(R.color.red, R.color.royalBlue, R.color.teal_200);
        final Handler handler = new Handler();
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 开始刷新，设置当前为刷新状态
                //swipeRefreshLayout.setRefreshing(true);

                // 这里是主线程
                // 一些比较耗时的操作，比如联网获取数据，需要放到子线程去执行
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        //同步加载网络数据
                        //加载数据 完毕后 关闭刷新状态 切回主线程
                        loadList(shouldReverse);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 加载完数据设置为不刷新状态，将下拉进度收起来
                                refresh.setRefreshing(false);
                            }
                        }, 500);
                    }
                }.start();
            }
        });

        images = v.findViewById(R.id.anime_recycle);
        images.hasFixedSize();
        loadData(listRef);

        filter = v.findViewById(R.id.anime_sortSelector);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popMenu(view);
            }
        });
        return v;
    }

    private void loadData(StorageReference ref) {
        imageList.clear();
        imageListReverse.clear();
        ref.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
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
            adapter = new AnimeAdapter(getContext(), imageList);
            images.setAdapter(adapter);
            adapter.setOnClickListener(singleImage -> {
                SingleImage.setPathStatic(singleImage.getPath());
                SingleImage.setCategory("Anime");
                Intent intent = new Intent(getActivity(), SingleImageZoomActivity.class);
                startActivity(intent);
            });
        } else {
            adapter = new AnimeAdapter(getContext(), imageListReverse);
            images.setAdapter(adapter);
            adapter.setOnClickListener(singleImage -> {
                SingleImage.setPathStatic(singleImage.getPath());
                SingleImage.setCategory("Anime");
                Intent intent = new Intent(getActivity(), SingleImageZoomActivity.class);
                startActivity(intent);
            });
        }
    }

    private void popMenu(View v) {
        PopupMenu menu = new PopupMenu(AnimeFragment.this.getContext(), v);
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
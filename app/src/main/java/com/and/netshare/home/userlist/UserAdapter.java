package com.and.netshare.home.userlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.and.netshare.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private ArrayList<UserItem> items;

    UserAdapter(ArrayList<UserItem> list){
        this.items = list;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_setting_item, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.item_name.setText(items.get(position).getTitle());
        viewHolder.item_icon.setImageResource(items.get(position).getIconId());
    }

    public int getItemCount() {
        return items.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item_name;
        private final ImageView item_icon;

        ViewHolder(View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.userList_text);
            item_icon = itemView.findViewById(R.id.userList_icon);
        }
    }
}

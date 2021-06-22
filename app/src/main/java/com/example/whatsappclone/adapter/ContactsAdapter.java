package com.example.whatsappclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.user.Users;
import com.example.whatsappclone.view.activitys.chats.ChatsActivity;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Users> list;
    private Context context;

    public ContactsAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {

        Users users =list.get(position);

        holder.userName.setText(users.getUserName());
        holder.desc.setText(users.getUserPhone());

        Glide.with(context).load(users.getImageProfile()).into(holder.imageProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userId", users.getUserId())
                        .putExtra("userName", users.getUserName())
                        .putExtra("userProfile", users.getImageProfile()));

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageProfile;
        private TextView userName, desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            userName = itemView.findViewById(R.id.tv_username);
            desc = itemView.findViewById(R.id.tv_desc);



        }
    }
}

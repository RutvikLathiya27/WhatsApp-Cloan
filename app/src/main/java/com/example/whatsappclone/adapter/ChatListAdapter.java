package com.example.whatsappclone.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.ChatList;
import com.example.whatsappclone.view.activitys.chats.ChatsActivity;
import com.example.whatsappclone.view.activitys.dialog.DialogViewUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {

    private List<ChatList> list;
    private Context context;

    public ChatListAdapter(List<ChatList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chat_list, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.Holder holder, int position) {

        ChatList chatList = list.get(position);

        holder.tvName.setText(chatList.getUserName());
        holder.tvDesc.setText(chatList.getDescription());
        holder.tvDate.setText(chatList.getDate());

        //for image
        if (chatList.getUrlProfile().equals("")){
            holder.profile.setImageResource(R.drawable.icon_male_ph);
        }else {
            Glide.with(context).load(chatList.getUrlProfile()).into(holder.profile);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userId", chatList.getUserID())
                        .putExtra("userName", chatList.getUserName())
                        .putExtra("userProfile", chatList.getUrlProfile()));
            }
        });

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogViewUser(context, chatList);
            }
        });

//        Glide.with(context)
//                .load(chatList.getUrlProfile())
//                .into(holder.profile);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        private TextView tvName, tvDesc, tvDate;
        private CircleImageView profile;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            profile = itemView.findViewById(R.id.image_profile);

        }
    }

}

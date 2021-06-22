package com.example.whatsappclone.view.activitys.dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.common.Common;
import com.example.whatsappclone.model.ChatList;
import com.example.whatsappclone.view.activitys.chats.ChatsActivity;
import com.example.whatsappclone.view.activitys.dispaly.ViewImageActivity;
import com.example.whatsappclone.view.activitys.profile.ProfileActivity;
import com.example.whatsappclone.view.activitys.profile.UserProfileActivity;

import java.util.Objects;

public class DialogViewUser extends AppCompatActivity {

    private Context context;

    public DialogViewUser(Context context, ChatList chatList) {
        this.context = context;
        initialize(chatList);
    }

    public void initialize(ChatList chatList) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.activity_dialog_view_user);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        ImageButton btnChat, btnCall, btnVideoCall, btnInfo;
        final ImageView profile;
        TextView userName;

        btnChat = dialog.findViewById(R.id.btn_chat);
        btnCall = dialog.findViewById(R.id.btn_call);
        btnVideoCall = dialog.findViewById(R.id.btn_video);
        btnInfo = dialog.findViewById(R.id.btn_info);

        profile = dialog.findViewById(R.id.image_profile);
        userName = dialog.findViewById(R.id.tv_username);

        userName.setText(chatList.getUserName());
        Glide.with(context).load(chatList.getUrlProfile()).into(profile);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userId", chatList.getUserID())
                        .putExtra("userName", chatList.getUserName())
                        .putExtra("userProfile", chatList.getUrlProfile()));
                dialog.dismiss();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Call Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        btnVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Video Call Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UserProfileActivity.class)
                        .putExtra("userId", chatList.getUserID())
                        .putExtra("userProfile", chatList.getUrlProfile())
                        .putExtra("userName", chatList.getUserName()));
                dialog.dismiss();
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.invalidate();
                Drawable dr = profile.getDrawable();
                Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, profile, "image");
                Intent intent = new Intent(context, ViewImageActivity.class);
                context.startActivity(intent, activityOptionsCompat.toBundle());
            }
        });

        dialog.show();

    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dialog_view_user);
//    }
}
package com.example.whatsappclone.view.activitys.status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivityAddStatusPicBinding;
import com.example.whatsappclone.managers.ChatService;
import com.example.whatsappclone.model.StatusModel;
import com.example.whatsappclone.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;
import java.util.UUID;

import kotlin.ranges.UIntRange;

public class AddStatusPicActivity extends AppCompatActivity {

    private Uri imageUri;
    private ActivityAddStatusPicBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_status_pic);

        imageUri = MainActivity.imageUri;

        setInfo();
        initclick();

    }

    private void initclick() {

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FirebaseService(AddStatusPicActivity.this).uploadImagetoFirBaseStorage(imageUri, new FirebaseService.OnCallBack() {
                    @Override
                    public void onUploadSuccess(String imageUri) {
                        StatusModel status = new StatusModel();
                        status.setId(UUID.randomUUID().toString());
                        status.setCreatedDate(new ChatService(AddStatusPicActivity.this).getCurrentDate());
                        status.setImageUrl(imageUri);
                        status.setUserId(FirebaseAuth.getInstance().getUid());
                        status.setViewCount("0");
                        status.setTextStatus(binding.edDescription.getText().toString());

                        new FirebaseService(AddStatusPicActivity.this).addNewStatus(status, new FirebaseService.OnAddNewStatusCallBack() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(AddStatusPicActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(AddStatusPicActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onUploadFailed(Exception e) {

                        Log.d("TAG", "onUploadFailed: "+ e);

                    }
                });
            }
        });

    }

    private void setInfo() {

        Glide.with(this).load(imageUri).into(binding.imageView);

    }
}
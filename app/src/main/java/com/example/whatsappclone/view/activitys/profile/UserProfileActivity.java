package com.example.whatsappclone.view.activitys.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivityUserProfileBinding;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String recieverID = intent.getStringExtra("userId");
        String userProfile = intent.getStringExtra("userProfile");

        if (recieverID != null) {
            binding.toolbar.setTitle(userName);
            if (userProfile != null) {
                if (userProfile.equals("")) {
                    binding.imageProfile.setImageResource(R.drawable.icon_male_ph);
                } else {
                    Glide.with(this).load(userProfile).into(binding.imageProfile);
                }
            }
        }

        initToolbar();
    }

    private void initToolbar() {

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


}
package com.example.whatsappclone.view.activitys.dispaly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.whatsappclone.R;
import com.example.whatsappclone.common.Common;
import com.example.whatsappclone.databinding.ActivityViewImageBinding;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_image);

        binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);
    }
}
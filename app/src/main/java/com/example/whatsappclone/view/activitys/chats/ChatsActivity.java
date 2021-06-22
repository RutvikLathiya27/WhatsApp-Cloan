package com.example.whatsappclone.view.activitys.chats;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ChatAdapter;
import com.example.whatsappclone.adapter.ChatListAdapter;
import com.example.whatsappclone.databinding.ActivityChatsBinding;
import com.example.whatsappclone.managers.ChatService;
import com.example.whatsappclone.managers.OnReadChatCallBack;
import com.example.whatsappclone.model.chat.Chats;
import com.example.whatsappclone.service.FirebaseService;
import com.example.whatsappclone.view.activitys.dialog.DialogReviewSendImage;
import com.example.whatsappclone.view.activitys.profile.UserProfileActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ChatsActivity extends AppCompatActivity {

    private static final int REQUEST_CORD_PERMISSION = 332;
    private ActivityChatsBinding binding;
    private String recieverID;
    private ChatAdapter adapter;
    private List<Chats> list = new ArrayList<>();

    private String userProfile;
    private String userName;
    private boolean isActionShowm =false;
    private ChatService chatService;

    private String TAG = "ChatsActivity";
    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;

    private MediaRecorder mediaRecorder;
    private String audio_path;
    private String sTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chats);

        initalize();
        initBtnClick();
        readChats();
    }

    private void initalize(){

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        recieverID = intent.getStringExtra("userId");
        userProfile = intent.getStringExtra("userProfile");

        chatService = new ChatService(this, recieverID);

        if (recieverID != null) {
            binding.tvUsername.setText(userName);
            if (userProfile != null) {
                if (userProfile.equals("")) {
                    binding.imageProfile.setImageResource(R.drawable.icon_male_ph);
                } else {
                    Glide.with(this).load(userProfile).into(binding.imageProfile);
                }
            }
        }

        binding.edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(binding.edMessage.getText().toString())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.btnSend.setVisibility(View.INVISIBLE);
                        binding.recordButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.btnSend.setVisibility(View.VISIBLE);
                        binding.recordButton.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(list, this);
        binding.recyclerView.setAdapter(adapter);

        //record button

        RecordView recordView = (RecordView) findViewById(R.id.record_view);
        RecordButton recordButton = (RecordButton) findViewById(R.id.record_button);

        //IMPORTANT
        recordButton.setRecordView(recordView);


        recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(ChatsActivity.this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
                if (recordPermissionAvailable) {
                    return true;
                }


                ActivityCompat.
                        requestPermissions(ChatsActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                0);

                return false;

            }
        });

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                if (!checkPermissionFromDevice()) {
                binding.btnEmoji.setVisibility(View.INVISIBLE);
                binding.btnFile.setVisibility(View.INVISIBLE);
                binding.btnCamera.setVisibility(View.INVISIBLE);
                binding.edMessage.setVisibility(View.INVISIBLE);

                startRecord();
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(100);
                }

            } else {
                requestPermission();
            }
            }

            @Override
            public void onCancel() {
                try {
                    mediaRecorder.reset();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnFile.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);

                try {
                    sTime = getHumanTimeText(recordTime);
                    stopRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onLessThanSecond() {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnFile.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
            }
        });

        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnFile.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
            }
        });


    }

    private String getHumanTimeText(long recordTime) {

        return String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(recordTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(recordTime)));

    }

    private void readChats() {
        chatService.readChatData(new OnReadChatCallBack() {
            @Override
            public void onReadSuccess(List<Chats> list) {

                binding.recyclerView.setAdapter(new ChatAdapter(list, ChatsActivity.this));
            }

            @Override
            public void onReadFailed() {

                Log.d(TAG, "onReadFailed: ");
            }
        });
    }

    private void initBtnClick() {

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.edMessage.getText().toString())) {

                    chatService.sendTextMsg(binding.edMessage.getText().toString());

                    binding.edMessage.setText("");
                }
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatsActivity.this, UserProfileActivity.class)
                        .putExtra("userId", recieverID)
                        .putExtra("userProfile", userProfile)
                        .putExtra("userName", userName));
            }
        });

        binding.btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isActionShowm){
                    binding.layoutActions.setVisibility(View.GONE);
                    isActionShowm = false;
                }else {

                    binding.layoutActions.setVisibility(View.VISIBLE);
                    isActionShowm = true;
                }

            }
        });

        binding.btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openGallary();
            }
        });

    }

    private boolean checkPermissionFromDevice() {
        int write_external_strorage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_strorage_result == PackageManager.PERMISSION_DENIED || record_audio_result == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_CORD_PERMISSION);
    }

    private void startRecord(){
        setUpMediaRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            //  Toast.makeText(InChatActivity.this, "Recording...", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ChatsActivity.this, "Recording Error , Please restart your app ", Toast.LENGTH_LONG).show();
        }
    }

    private void stopRecord(){

        try {
            if (mediaRecorder != null) {

                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

                //sendVoice();
                chatService.sendVoice(audio_path);

            } else {
                Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Stop Recording Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void setUpMediaRecorder() {
        String path_save = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "audio_record.m4a";
        audio_path = path_save;

        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(path_save);
        } catch (Exception e) {
            Log.d(TAG, "setUpMediaRecord: " + e.getMessage());
        }

    }

    private void openGallary() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            imageUri = data.getData();

            //uploadToFirebase();

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                reviewImage(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void reviewImage(Bitmap bitmap){
        new DialogReviewSendImage(ChatsActivity.this, bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void onButtnSendClick() {

                final ProgressDialog progressDialog = new ProgressDialog(ChatsActivity.this);
                progressDialog.setMessage("Sending image...");

                progressDialog.show();


                //hide action button
                binding.layoutActions.setVisibility(View.GONE);
                isActionShowm = false;

                //upload image in firebase storage to geturl image...
                if (imageUri!=null) {
                    new FirebaseService(ChatsActivity.this).uploadImagetoFirBaseStorage(imageUri, new FirebaseService.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String imageUri) {

                            chatService.sendImage(imageUri);
                            progressDialog.dismiss();

                        }

                        @Override
                        public void onUploadFailed(Exception e) {

                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }


}
package com.example.whatsappclone.view.activitys.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.BuildConfig;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivityViewImageBinding;
import com.example.whatsappclone.view.activitys.SplashScreen;
import com.example.whatsappclone.common.Common;
import com.example.whatsappclone.databinding.ActivityProfileBinding;
import com.example.whatsappclone.view.activitys.dispaly.ViewImageActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private BottomSheetDialog bottomSheetDialog, bsDialogEditName;
    private ProgressDialog progressDialog;

    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

        if (firebaseUser != null) {
            getInfo();
        }

        initActivityClick();

    }

    private void initActivityClick() {

        binding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomsheetPckPhotPhoto();
            }
        });

        binding.lnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomsheetEditName();
            }
        });

        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imageProfile.invalidate();
                Drawable dr = binding.imageProfile.getDrawable();
                Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this, binding.imageProfile, "image");
                Intent intent = new Intent(ProfileActivity.this, ViewImageActivity.class);
                startActivity(intent, activityOptionsCompat.toBundle());

            }
        });

        binding.btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiaogSignOut();
            }
        });
    }

    private void showDiaogSignOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setMessage("Do you want to sign out?");
        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, SplashScreen.class));
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                     dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showBottomsheetEditName() {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name, null);

        ((View) view.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsDialogEditName.dismiss();
            }
        });

        EditText edUserName = view.findViewById(R.id.ed_username);

        ((View) view.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edUserName.getText().toString())){
                    Toast.makeText(ProfileActivity.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                }else {
                    updateName(edUserName.getText().toString());
                    bsDialogEditName.dismiss();
                }


            }
        });

        bsDialogEditName = new BottomSheetDialog(this);
        bsDialogEditName.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bsDialogEditName.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bsDialogEditName.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bsDialogEditName = null;
            }
        });

        bsDialogEditName.show();

    }

    private void updateName(String newName) {

        firestore.collection("Users").document(firebaseUser.getUid()).update("userName", newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ProfileActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                getInfo();
            }
        });


    }

    private void showBottomsheetPckPhotPhoto() {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick, null);

        ((View) view.findViewById(R.id.ln_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openGallary();
                bottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.ln_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkCameraPermission();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);

        bottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog = null;
            }
        });
        bottomSheetDialog.show();
    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    222);
        }else {
            openCamera();
        }

    }

    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";


        try {
            File file = File.createTempFile("IMG_" + timeStamp, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,  imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, 440);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getInfo() {

        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String userName = documentSnapshot.getString("userName");
                String userPhone = documentSnapshot.getString("userPhone");
                String imageProfile = documentSnapshot.getString("imageProfile");

                binding.tvUsername.setText(userName);
                binding.tvPhone.setText(userPhone);

                Glide.with(ProfileActivity.this).load(imageProfile).into(binding.imageProfile);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void openGallary() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            imageUri = data.getData();

            uploadToFirebase();
//            try {
//
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                binding.imageProfile.setImageBitmap(bitmap);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        if (requestCode == 440
                && resultCode == RESULT_OK) {

            uploadToFirebase();
        }
    }

    private String getFileExtention(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadToFirebase() {

        if (imageUri != null){

            progressDialog.setMessage("Uploading..");
            progressDialog.show();

            StorageReference mountainsRef = FirebaseStorage.getInstance().getReference().child("Image").child("ImagesProfile/"+ System.currentTimeMillis()+"."+getFileExtention(imageUri));
            mountainsRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri doenloadUrl = uriTask.getResult();

                    final String sdownload_url = String.valueOf(doenloadUrl);


                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageProfile", sdownload_url);

                    progressDialog.dismiss();

                    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ProfileActivity.this, "upload sucessfully", Toast.LENGTH_SHORT).show();

                            getInfo();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "upload Failed", Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                }
            });

        }

    }



}
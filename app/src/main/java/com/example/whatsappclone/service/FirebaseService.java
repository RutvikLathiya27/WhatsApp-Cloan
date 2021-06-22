package com.example.whatsappclone.service;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.whatsappclone.model.StatusModel;
import com.example.whatsappclone.view.activitys.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class FirebaseService {

    private Context context;

    public FirebaseService(Context context) {
        this.context = context;
    }

    public void uploadImagetoFirBaseStorage(Uri uri, OnCallBack onCallBack){

        StorageReference mountainsRef = FirebaseStorage.getInstance().getReference().child("Image").child("ImagesChats/"+ System.currentTimeMillis()+"."+getFileExtention(uri));
        mountainsRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUrl = uriTask.getResult();

                final String sdownload_url = String.valueOf(downloadUrl);

                onCallBack.onUploadSuccess(sdownload_url);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCallBack.onUploadFailed(e);
            }
        });
    }

    private String getFileExtention(Uri uri){

        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public interface OnCallBack{
        void onUploadSuccess(String imageUri);
        void onUploadFailed(Exception e);
    }

    public void addNewStatus(StatusModel statusModel,  OnAddNewStatusCallBack onAddNewStatusCallBack){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Status Daily").document(statusModel.getId()).set(statusModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onAddNewStatusCallBack.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                onAddNewStatusCallBack.onFailed();
            }
        });



    }

    public interface OnAddNewStatusCallBack{
        void onSuccess();
        void onFailed();
    }

}

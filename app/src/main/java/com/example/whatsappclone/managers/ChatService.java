package com.example.whatsappclone.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whatsappclone.adapter.ChatAdapter;
import com.example.whatsappclone.model.chat.Chats;
import com.example.whatsappclone.view.activitys.chats.ChatsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatService {

    private Context context;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private String recieverID;

    private String TAG = "ChatService";

    public ChatService(Context context, String recieverID) {
        this.context = context;
        this.recieverID = recieverID;
    }

    public ChatService(Context context) {
        this.context = context;
    }

    public void readChatData(final OnReadChatCallBack onCallBack) {
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Chats> list = new ArrayList<>();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {


                    Chats chats = snapshot1.getValue(Chats.class);
                    try {
                        if (chats != null && chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(recieverID)
                                || chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(recieverID)
                        ) {
                            list.add(chats);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Log.d(TAG, "onDataChange: ss "+ list.size());
                onCallBack.onReadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onCallBack.onReadFailed();
            }
        });

    }

    public void sendTextMsg(String text) {


        Chats chats = new Chats(
                getCurrentDate(),
                text,
                "",
                "TEXT",
                firebaseUser.getUid(),
                recieverID
        );

        reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Send", "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Send", "onFailure " + e.getMessage());
            }
        });

        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(recieverID);
        chatRef1.child("chatid").setValue(recieverID);

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(recieverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());

    }

    public void sendImage(String imageUrl) {

        Chats chats = new Chats(
                getCurrentDate(),
                "",
                imageUrl,
                "IMAGE",
                firebaseUser.getUid(),
                recieverID
        );

        reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Send", "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Send", "onFailure " + e.getMessage());
            }
        });

        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(recieverID);
        chatRef1.child("chatid").setValue(recieverID);

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(recieverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());
    }

    public String getCurrentDate() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
        String today = format.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());

        return today + ", " + currentTime;
    }

    public void sendVoice(String audioPath) {

        Uri uriAudio = Uri.fromFile(new File(audioPath));
        final StorageReference audioRef = FirebaseStorage.getInstance().getReference().child("Chats/Voice/" + System.currentTimeMillis());
        audioRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                String voiceUrl = String.valueOf(downloadUrl);

                Chats chats = new Chats(
                        getCurrentDate(),
                        "",
                        voiceUrl,
                        "VOICE",
                        firebaseUser.getUid(),
                        recieverID
                );

                reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Send", "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Send", "onFailure: " + e.getMessage());
                    }
                });

                //Add to ChatList
                DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(recieverID);
                chatRef1.child("chatid").setValue(recieverID);

                //
                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(recieverID).child(firebaseUser.getUid());
                chatRef2.child("chatid").setValue(firebaseUser.getUid());

            }
        });

    }

}

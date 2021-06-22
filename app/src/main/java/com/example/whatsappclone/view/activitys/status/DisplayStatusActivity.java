package com.example.whatsappclone.view.activitys.status;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivityDisplayStatusBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayStatusActivity extends AppCompatActivity {

    private ActivityDisplayStatusBinding binding;
    private ImageView imageView;
    private CircleImageView imagePro;
    private TextView userNa;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_status);


        imageView = findViewById(R.id.image_view);
        progressBar = findViewById(R.id.progress_bar);
        imagePro = findViewById(R.id.image_profile);
        userNa= findViewById(R.id.tv_username);

        getStatus();

        getProfile();


    }

    private void getProfile() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String document = "66c32a50-fe01-4975-a452-e7f16a4b6cac";

        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String imageProfile = documentSnapshot.getString("imageProfile");
                String userName = documentSnapshot.getString("userName");

                userNa.setText(userName);

                Glide.with(DisplayStatusActivity.this).load(imageProfile).into(imagePro);




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });



    }

    private String getImageProfile(String imageProfile) {
        return imageProfile;
    }

    private void getStatus() {

        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String document = "66c32a50-fe01-4975-a452-e7f16a4b6cac";

        firestore.collection("Status Daily").document(document).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String imageProfile = documentSnapshot.getString("imageUrl");

                Glide.with(DisplayStatusActivity.this).load(imageProfile).into(imageView);

                progressBar.setVisibility(View.INVISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }
}
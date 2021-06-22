package com.example.whatsappclone.menu;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ChatListAdapter;
import com.example.whatsappclone.databinding.FragmentChatsBinding;
import com.example.whatsappclone.model.ChatList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    private static final String TAG = "ChatsFragment";

    public ChatsFragment() {
        // Required empty public constructor
    }

    private FragmentChatsBinding binding;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
    private List<ChatList> list;
    private ArrayList<String> alluserId;
    private ChatListAdapter adapter;

    private Handler handler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);


        list = new ArrayList<>();
        alluserId = new ArrayList<>();

        adapter = new ChatListAdapter(list, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

//        list.add(new ChatList("11","Samntha","Hi","20/2/2020","https://www.pinkvilla.com/files/styles/gallery-section/public/sama.jpg?itok=iBkuZ_Hy"));
//        list.add(new ChatList("11","Samntha","Hi","20/2/2020","https://www.pinkvilla.com/files/styles/gallery-section/public/sama.jpg?itok=iBkuZ_Hy"));
//        list.add(new ChatList("11","Samntha","Hi","20/2/2020","https://www.pinkvilla.com/files/styles/gallery-section/public/sama.jpg?itok=iBkuZ_Hy"));
//        list.add(new ChatList("11","Samntha","Hi","20/2/2020","https://www.pinkvilla.com/files/styles/gallery-section/public/sama.jpg?itok=iBkuZ_Hy"));
//        list.add(new ChatList("11","Samntha","Hi","20/2/2020","https://www.pinkvilla.com/files/styles/gallery-section/public/sama.jpg?itok=iBkuZ_Hy"));
//
//        recyclerView.setAdapter(new ChatListAdapter(list,getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            getChatList();
        }

        return binding.getRoot();
    }

    private void getChatList() {

        binding.progressCircular.setVisibility(View.VISIBLE);

        reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                alluserId.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String userID = snapshot1.child("chatid").getValue().toString();
                    Log.d(TAG, "onDataChange : userid " + userID);

                    binding.progressCircular.setVisibility(View.GONE);
                    alluserId.add(userID);
                }

                getUserInfo();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (String userID : alluserId) {

                    firestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            Log.d(TAG, "onSuccess: add" + documentSnapshot.getString("userName"));
                            try {
                                ChatList chat = new ChatList(
                                        documentSnapshot.getString("userId"),
                                        documentSnapshot.getString("userName"),
                                        "This is description..",
                                        "",
                                        documentSnapshot.getString("imageProfile")
                                );
                                list.add(chat);
                            } catch (Exception e) {
                                Log.d(TAG, "onSuccess: " + e.getMessage());
                            }
                            if (adapter!=null){
                                adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();

                                Log.d(TAG, "onSuccess : adapter " + adapter.getItemCount());

                            }
                        }
                    }).

                            addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Error L" + e.getMessage());
                                }
                            });
                }

                binding.progressCircular.setVisibility(View.GONE);

            }
        });


//        binding.recyclerView.setAdapter(new
//
//                ChatListAdapter(list, getContext()));
//        Log.d(TAG, "onDataChange: list" + list.size());
    }

}
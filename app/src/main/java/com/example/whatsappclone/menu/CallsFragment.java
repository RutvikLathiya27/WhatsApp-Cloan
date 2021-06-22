package com.example.whatsappclone.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.CallListAdapter;
import com.example.whatsappclone.model.CallList;

import java.util.ArrayList;
import java.util.List;

public class CallsFragment extends Fragment {


    public CallsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_calls, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCall);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        List<CallList> lists = new ArrayList<>();
//        lists.add(new CallList("01","samntha","20/02/2020, 9.04pm","https://www.pinkvilla.com/files/styles/gallery-section/public/sama.jpg?itok=iBkuZ_Hy","income"));
//        lists.add(new CallList("02","samntha","20/02/2020, 9.04pm","https://www.pinkvilla.com/files/styles/gallery-section/public/sama.jpg?itok=iBkuZ_Hy","missed"));
//        lists.add(new CallList("03","samntha","20/02/2020, 9.04pm","https://www.pinkvilla.com/files/styles/gallery-section/public/sama.jpg?itok=iBkuZ_Hy",""));
//
//        recyclerView.setAdapter(new CallListAdapter(lists,getContext()));
        return view;

    }
}
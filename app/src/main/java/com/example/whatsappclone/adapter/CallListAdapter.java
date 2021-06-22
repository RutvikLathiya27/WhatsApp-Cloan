package com.example.whatsappclone.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.CallList;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.Holder> {

    private List<CallList> list;
    private Context context;

    public CallListAdapter(List<CallList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_call_list, parent, false);
        return new Holder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CallListAdapter.Holder holder, int position) {

        CallList callList = list.get(position);

        holder.tvName.setText(callList.getUserName());
        holder.tvDate.setText(callList.getDate());

        if (callList.getCalltype().equals("missed")){
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward_black_24dp));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_red_dark));
        }else if (callList.getCalltype().equals("income")){
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward_black_24dp));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));

        }else {
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_upward_black_24dp));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));
        }
        Glide.with(context).load(callList.getUrlProfile()).into(holder.profile);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{

        private TextView tvName, tvDate;
        private CircleImageView profile;
        private ImageView arrow;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvName = itemView.findViewById(R.id.tv_name);
            profile = itemView.findViewById(R.id.image_profile);
            arrow = itemView.findViewById(R.id.img_arrow);


        }
    }


}

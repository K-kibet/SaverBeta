package com.kibet.saverbeta.Adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kibet.saverbeta.Model.ModelStatus;
import com.kibet.saverbeta.R;
import com.kibet.saverbeta.activities.BusinessVideoActivity;
import com.kibet.saverbeta.fragments.BusinessVideoFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusinessVideoAdapter extends RecyclerView.Adapter<BusinessVideoAdapter.VideoViewHolder> {
    public List<ModelStatus> videoList;
    public List<ModelStatus> selected_videoList;
    Context context;
    BusinessVideoFragment videoFragment;
    public final static String POSITION = "Image Position";
    public OnItemClickListener onItemClickListener;

    public BusinessVideoAdapter(Context context, List<ModelStatus> videoList, List<ModelStatus> selected_video, BusinessVideoFragment videoFragment){
        this.context = context;
        this.videoList = videoList;
        this.selected_videoList = selected_video;
        this.videoFragment = videoFragment;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        ModelStatus statusModel = videoList.get(position);

        Glide.with(context).load(statusModel.getFull_path()).into(holder.ivThumbnailImageView);

        if (selected_videoList.contains(videoList.get(position))){
            holder.opacity.setBackgroundColor(ContextCompat.getColor(context, R.color.image_selected));
            holder.opacity.setVisibility(View.VISIBLE);
        }else {
            holder.opacity.setBackgroundColor(ContextCompat.getColor(context, R.color.image_not_selected));
            holder.opacity.setVisibility(View.GONE);
        }

        holder.ivThumbnailImageView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            String stringId = Integer.valueOf(pos).toString();

            Intent i = new Intent(context, BusinessVideoActivity.class);
            i.putExtra(POSITION, stringId);
            i.putExtra("pos", pos);
            i.putExtra("video", statusModel.getFull_path());
            i.putExtra("type", "" + statusModel.getType());
            // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.pop_enter);
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ivThumbnail) ImageView ivThumbnailImageView;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.opacity) LinearLayout opacity;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getAdapterPosition(), v));
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}
